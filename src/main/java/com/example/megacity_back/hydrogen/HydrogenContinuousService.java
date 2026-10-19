package com.example.megacity_back.hydrogen;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class HydrogenContinuousService {
    private final HydrogenStateRepository states;
    private final HydrogenControlRepository controls;
    private final HydrogenHourlyRepository hours;
    private final Clock clock;
    @PersistenceContext private EntityManager em;
    @Value("${hydrogen.refill-threshold-nm3:2000}") private double threshold;
    @Value("${hydrogen.refill-target-nm3:10000}") private double target;
    @Value("${hydrogen.tank-capacity-kg:1500}") private double capacity;
    @Value("${hydrogen.scheduling-enabled:true}") private boolean enabled;
    public HydrogenContinuousService(HydrogenStateRepository states,HydrogenControlRepository controls,
        HydrogenHourlyRepository hours,@Qualifier("hydrogenClock") Clock clock) {
        this.states=states;this.controls=controls;this.hours=hours;this.clock=clock;
    }
    public record Request(String commandId,String type,BigDecimal percent,LocalDateTime effectiveAt) {}
    public record Status(HydrogenState state,HydrogenControl activeCommand,double expectedProductionKgH,
                         LocalDateTime nextSaveAt,boolean schedulingEnabled) {}
    public record History(Status status,List<HydrogenHourly> hours,List<HydrogenControl> commands) {}
    private LocalDateTime now() {return LocalDateTime.ofInstant(clock.instant(),HydrogenTime.ZONE).truncatedTo(ChronoUnit.SECONDS);}
    private static BigDecimal n(double v) {return BigDecimal.valueOf(v).setScale(9,RoundingMode.HALF_UP);}
    private HydrogenState locked() {return states.lockState().orElseThrow(()->new HydrogenProblem(503,"수소 시스템을 초기화하는 중입니다."));}
    private HydrogenContinuousEngine.Parameters parameters(HydrogenState s) {
        return new HydrogenContinuousEngine.Parameters(s.getDailyHydrogenKg().doubleValue()/24,s.getGasPerKg().doubleValue(),
            HydrogenContinuousEngine.ENERGY_KWH_PER_KG,s.getMaxProductionKgH().doubleValue(),s.getRefillThresholdNm3().doubleValue(),
            s.getRefillTargetNm3().doubleValue(),s.getTankCapacityKg().doubleValue());
    }
    @Transactional public void initialize() {
        if(states.existsById("MAIN")) {
            var existing=locked();
            var corrected=n(HydrogenContinuousEngine.ENERGY_KWH_PER_KG);
            if(existing.getEnergyPerKg().compareTo(corrected)!=0) {
                existing.setEnergyPerKg(corrected);existing.setUpdDate(now());
            }
            return;
        }
        if(!Double.isFinite(threshold)||!Double.isFinite(target)||!Double.isFinite(capacity)||threshold<0||target-threshold<1||target>20000||capacity<1||capacity>100000)
            throw new IllegalArgumentException("바이오가스 보충 및 탱크 용량 설정을 확인하세요.");
        var s=new HydrogenState();s.setSystemId("MAIN");s.setStartedAt(now());s.setCursorAt(s.getStartedAt());
        s.setStoredBiogasNm3(n(target));s.setStoredHydrogenKg(n(0));s.setDailyHydrogenKg(n(500));
        s.setGasPerKg(n(20));s.setEnergyPerKg(n(HydrogenContinuousEngine.ENERGY_KWH_PER_KG));s.setMaxProductionKgH(n(60));
        s.setRefillThresholdNm3(n(threshold));s.setRefillTargetNm3(n(target));s.setTankCapacityKg(n(capacity));
        s.setRegDate(now());s.setUpdDate(now());em.persist(s);em.flush();
    }
    @Transactional public void tick() {
        var s=locked();var until=HydrogenTime.fiveMinutes(now());var cursor=s.getCursorAt();
        for(int count=0;count<288 && cursor.isBefore(until);count++) {
            var end=HydrogenTime.fiveMinutes(cursor).plusMinutes(5);
            var list=new ArrayList<HydrogenControl>();
            controls.findFirstByEffectiveAtLessThanEqualOrderByEffectiveAtDescSequenceDesc(cursor).ifPresent(list::add);
            list.addAll(controls.findByEffectiveAtGreaterThanAndEffectiveAtLessThanOrderByEffectiveAtAscSequenceAsc(cursor,end));
            var input=list.stream().map(c->new HydrogenContinuousEngine.Control(c.getEffectiveAt(),c.getType(),
                c.getPercent()==null?0:c.getPercent().doubleValue(),c.getSequence())).toList();
            var r=HydrogenContinuousEngine.calculate(parameters(s),cursor,end,s.getStoredBiogasNm3().doubleValue(),s.getStoredHydrogenKg().doubleValue(),input);
            var h=new HydrogenHourly();h.setTm(HydrogenTime.format(HydrogenTime.fiveMinutes(cursor)));h.setIntervalStart(cursor);h.setIntervalEnd(end);
            h.setDurationSeconds(Math.toIntExact(Duration.between(cursor,end).toSeconds()));
            h.setNormalProductionKg(n(r.normalKg()));h.setDrProductionKg(n(r.drKg()));h.setReducedProductionKg(n(r.reducedKg()));
            h.setProductionKg(n(r.productionKg()));h.setEnergyKwh(n(r.energyKwh()));
            h.setOpeningBiogasNm3(s.getStoredBiogasNm3());h.setRefilledBiogasNm3(n(r.gasRefilled()));h.setUsedBiogasNm3(n(r.gasUsed()));h.setStoredBiogasNm3(n(r.gasStored()));
            h.setOpeningHydrogenKg(s.getStoredHydrogenKg());h.setStoredHydrogenKg(n(r.hydrogenStored()));h.setShippedHydrogenKg(n(r.shippedKg()));
            h.setTankExchanges(r.exchanges());h.setDrSeconds(r.drSeconds());h.setStopSeconds(r.stopSeconds());h.setReducedSeconds(r.reducedSeconds());h.setRegDate(now());
            em.persist(h);s.setStoredBiogasNm3(h.getStoredBiogasNm3());s.setStoredHydrogenKg(h.getStoredHydrogenKg());
            cursor=end;s.setCursorAt(cursor);s.setUpdDate(now());
        }
    }
    @Transactional public HydrogenControl receive(Request q) {
        if(q.commandId()==null||!q.commandId().matches("[A-Za-z0-9._:-]{1,64}")) throw new IllegalArgumentException("지령 ID 형식을 확인하세요.");
        if(!Set.of("NORMAL","STOP","PLUS_DR","REDUCE").contains(Objects.toString(q.type(),""))) throw new IllegalArgumentException("지령 종류를 확인하세요.");
        boolean percentage=Set.of("PLUS_DR","REDUCE").contains(q.type());
        if(percentage && (q.percent()==null||q.percent().scale()>9||q.percent().signum()<0||q.percent().compareTo(BigDecimal.valueOf(100))>0))
            throw new IllegalArgumentException("감소율과 플러스 DR 지령률은 0~100%입니다.");
        if(!percentage && q.percent()!=null) throw new IllegalArgumentException("정지·일반 운전 지령에는 percent를 보내지 마세요.");
        var s=locked();
        var old=controls.findById(q.commandId());
        if(old.isPresent()) {
            var c=old.get();
            boolean samePercent=c.getPercent()==null?q.percent()==null:q.percent()!=null&&c.getPercent().compareTo(q.percent())==0;
            if(!c.getType().equals(q.type())||!samePercent||!Objects.equals(c.getRequestedAt(),q.effectiveAt()))
                throw new HydrogenProblem(409,"동일 지령 ID의 내용이 다릅니다.");
            return c;
        }
        var received=now();var effective=q.effectiveAt()==null?received:q.effectiveAt();
        if(effective.getNano()!=0||effective.isBefore(received)||effective.isBefore(s.getCursorAt())||effective.isAfter(received.plusDays(31)))
            throw new IllegalArgumentException("지령 시각은 현재부터 31일 이내의 초 단위 한국 시각이어야 합니다. 즉시 적용은 effectiveAt을 생략하세요.");
        var c=new HydrogenControl();c.setCommandId(q.commandId());c.setType(q.type());c.setPercent(q.percent());
        c.setEffectiveAt(effective);c.setRequestedAt(q.effectiveAt());c.setReceivedAt(received);c.setSequence(controls.lastSequence()+1);
        em.persist(c);em.flush();return c;
    }
    private Status statusOf(HydrogenState s) {
        var active=controls.findFirstByEffectiveAtLessThanEqualOrderByEffectiveAtDescSequenceDesc(now()).orElse(null);
        return new Status(s,active,HydrogenContinuousEngine.rate(parameters(s),active==null?"NORMAL":active.getType(),
            active==null||active.getPercent()==null?0:active.getPercent().doubleValue()),HydrogenTime.fiveMinutes(s.getCursorAt()).plusMinutes(5),enabled);
    }
    @Transactional(readOnly=true,isolation=org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    public History history(LocalDateTime from,LocalDateTime to,int days) {
        var s=states.findById("MAIN").orElseThrow(()->new HydrogenProblem(503,"수소 시스템을 초기화하는 중입니다."));
        if((from==null)!=(to==null)) throw new IllegalArgumentException("조회 시작과 종료 시각을 함께 보내세요.");
        if(from==null) {
            if(days<1||days>31) throw new IllegalArgumentException("조회 기간은 1~31일입니다.");
            to=HydrogenTime.fiveMinutes(s.getCursorAt());
            if(!to.isAfter(HydrogenTime.fiveMinutes(s.getStartedAt()))) to=HydrogenTime.fiveMinutes(s.getStartedAt()).plusMinutes(5);
            from=to.minusDays(days);
        }
        HydrogenTime.wholeFiveMinutes(from);HydrogenTime.wholeFiveMinutes(to);
        long length=ChronoUnit.MINUTES.between(from,to);
        if(length<5||length>44640) throw new IllegalArgumentException("조회 기간은 5분~31일입니다.");
        return new History(statusOf(s),hours.findByTmGreaterThanEqualAndTmLessThanOrderByTmAsc(HydrogenTime.format(from),HydrogenTime.format(to)),controls.findTop50ByOrderBySequenceDesc());
    }
}
