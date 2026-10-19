package com.example.megacity_back.hydrogen;
import java.time.*;
import java.util.*;

/** Piecewise constant controls, instantaneous gas refills and full-tank exchanges. */
public final class HydrogenContinuousEngine {
    public static final double GAS_CAPACITY_NM3 = 20000;
    public static final double HYDROGEN_KG_PER_KWH = 0.115;
    public static final double ENERGY_KWH_PER_KG = 1.0 / HYDROGEN_KG_PER_KWH;
    public record Parameters(double normalKgH,double gasPerKg,double energyPerKg,double maxKgH,
                             double refillThreshold,double refillTarget,double tankCapacity) {}
    public record Control(LocalDateTime at,String type,double percent,long sequence) {}
    public record Result(double normalKg,double drKg,double reducedKg,double energyKwh,
                         double gasRefilled,double gasUsed,double gasStored,double hydrogenStored,
                         double shippedKg,int exchanges,int drSeconds,int stopSeconds,int reducedSeconds) {
        public double productionKg() { return normalKg+drKg+reducedKg; }
    }
    public static double rate(Parameters p,String type,double percent) {
        return switch(type) {
            case "STOP" -> 0;
            case "REDUCE" -> p.normalKgH*(1-percent/100.0);
            case "PLUS_DR" -> p.normalKgH+(p.maxKgH-p.normalKgH)*Math.min(100,Math.max(0,percent))/100.0;
            default -> p.normalKgH;
        };
    }
    public static Result calculate(Parameters p,LocalDateTime start,LocalDateTime end,
                                   double initialGas,double initialHydrogen,List<Control> controls) {
        var cuts=new TreeSet<LocalDateTime>();cuts.add(start);cuts.add(end);
        for(var c:controls) if(c.at.isAfter(start)&&c.at.isBefore(end)) cuts.add(c.at);
        var points=new ArrayList<>(cuts);
        // Repeatable per interval: a rolled-back transaction chooses the same refill levels on retry.
        var refillRandom=new Random(start.toEpochSecond(ZoneOffset.ofHours(9)));
        double gas=initialGas,hg=initialHydrogen,normal=0,dr=0,reduced=0,refilled=0,used=0,shipped=0;
        int exchanges=0,drSeconds=0,stopSeconds=0,reducedSeconds=0;
        for(int i=0;i<points.size()-1;i++) {
            var a=points.get(i);var b=points.get(i+1);
            int seconds=Math.toIntExact(Duration.between(a,b).toSeconds());
            var active=controls.stream().filter(c->!c.at.isAfter(a))
                .max(Comparator.comparing(Control::at).thenComparingLong(Control::sequence));
            String type=active.map(Control::type).orElse("NORMAL");
            double percent=active.map(Control::percent).orElse(0.0);
            double made=rate(p,type,percent)*seconds/3600.0;
            double demand=made*p.gasPerKg;used+=demand;
            // Refill exactly when the low-water threshold is reached, including at an interval end.
            if(demand>0) {
                if(gas<=p.refillThreshold+1e-8) {
                    double target=p.refillTarget+(GAS_CAPACITY_NM3-p.refillTarget)*refillRandom.nextDouble();
                    refilled+=target-gas;gas=target;
                }
                while(demand>=gas-p.refillThreshold-1e-8) {
                    demand=Math.max(0,demand-(gas-p.refillThreshold));
                    double target=p.refillTarget+(GAS_CAPACITY_NM3-p.refillTarget)*refillRandom.nextDouble();
                    refilled+=target-p.refillThreshold;gas=target;
                    if(demand<=1e-8) break;
                }
                gas-=demand;
            }
            hg+=made;
            // DB quantities have nine decimals; allow sub-microgram accumulation error at a full tank.
            int swaps=(int)Math.floor((hg+1e-7)/p.tankCapacity);
            if(swaps>0) { hg=Math.max(0,hg-swaps*p.tankCapacity);exchanges+=swaps;shipped+=swaps*p.tankCapacity; }
            switch(type) {
                case "PLUS_DR" -> {dr+=made;drSeconds+=seconds;}
                case "REDUCE" -> {reduced+=made;reducedSeconds+=seconds;}
                case "STOP" -> stopSeconds+=seconds;
                default -> normal+=made;
            }
        }
        return new Result(normal,dr,reduced,(normal+dr+reduced)*p.energyPerKg,refilled,used,gas,hg,shipped,
                          exchanges,drSeconds,stopSeconds,reducedSeconds);
    }
}
