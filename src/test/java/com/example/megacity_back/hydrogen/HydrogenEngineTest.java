package com.example.megacity_back.hydrogen;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class HydrogenEngineTest {
    final LocalDateTime start=LocalDateTime.of(2026,9,10,0,0);
    final HydrogenContinuousEngine.Parameters p=new HydrogenContinuousEngine.Parameters(500.0/24,20,1.0/0.115,60,2000,10000,1500);
    HydrogenContinuousEngine.Result calc(double gas,double hg,HydrogenContinuousEngine.Control...c) {
        return HydrogenContinuousEngine.calculate(p,start,start.plusHours(1),gas,hg,List.of(c));
    }
    HydrogenContinuousEngine.Control cmd(int minute,String type,double percent,long seq) {return new HydrogenContinuousEngine.Control(start.plusMinutes(minute),type,percent,seq);}
    @Test void normalConsumesStoredGasWithoutContinuousInflow() {
        var r=calc(10000,0);assertThat(r.productionKg()).isCloseTo(500.0/24,within(1e-8));
        assertThat(r.gasStored()).isCloseTo(10000-10000.0/24,within(1e-8));assertThat(r.gasRefilled()).isZero();
        assertThat(r.energyKwh()).isCloseTo((500.0/24)/0.115,within(1e-8));
    }
    @Test void ninetyPercentReductionMeansTenPercentProduction() {
        var r=calc(10000,0,cmd(0,"REDUCE",90,1));
        assertThat(r.productionKg()).isCloseTo(500.0/24*0.1,within(1e-8));
        var example=new HydrogenContinuousEngine.Parameters(20.87,20,1.0/0.115,60,2000,10000,1500);
        assertThat(HydrogenContinuousEngine.rate(example,"REDUCE",90)).isCloseTo(2.087,within(1e-8));
        assertThat(r.reducedSeconds()).isEqualTo(3600);
    }
    @Test void arbitraryReductionPercentScalesProductionGasAndEnergy() {
        for (double percent : new double[]{0, 5, 20, 33.3, 50, 75.5, 90, 99.9, 100}) {
            var r=calc(10000,0,cmd(0,"REDUCE",percent,1));
            double expected=(500.0/24)*(100-percent)/100;
            assertThat(r.productionKg()).as("production at %s%% reduction",percent).isCloseTo(expected,within(1e-8));
            assertThat(r.gasUsed()).isCloseTo(expected*20,within(1e-8));
            assertThat(r.energyKwh()).isCloseTo(expected/0.115,within(1e-8));
        }
    }
    @Test void stopHasZeroProductionEnergyAndGasConsumption() {
        var r=calc(5000,500,cmd(0,"STOP",0,1));
        assertThat(r.productionKg()).isZero();assertThat(r.energyKwh()).isZero();assertThat(r.gasStored()).isEqualTo(5000);
        assertThat(r.hydrogenStored()).isEqualTo(500);assertThat(r.stopSeconds()).isEqualTo(3600);
    }
    @Test void increaseIsRelativeToNormalAndCappedAtSixty() {
        assertThat(calc(10000,0,cmd(0,"PLUS_DR",0,1)).productionKg()).isCloseTo(500.0/24,within(1e-8));
        assertThat(calc(10000,0,cmd(0,"PLUS_DR",50,1)).productionKg()).isCloseTo((500.0/24+60)/2,within(1e-8));
        assertThat(calc(10000,0,cmd(0,"PLUS_DR",100,1)).productionKg()).isEqualTo(60);
    }
    @Test void splitsControlsAtTimeBoundariesAndLastReceivedWinsAtSameTime() {
        var r=calc(10000,0,cmd(30,"STOP",0,1),cmd(45,"PLUS_DR",188,2),cmd(45,"REDUCE",90,3));
        assertThat(r.productionKg()).isCloseTo(500.0/24*(0.5+0.25*0.1),within(1e-8));
        assertThat(r.stopSeconds()).isEqualTo(900);assertThat(r.reducedSeconds()).isEqualTo(900);assertThat(r.drSeconds()).isZero();
    }
    @Test void refillsAtThresholdAndPreservesMassBalance() {
        var r=calc(2100,0);assertThat(r.gasRefilled()).isBetween(8000.0,18000.0);
        assertThat(r.gasStored()).isCloseTo(2100+r.gasRefilled()-r.gasUsed(),within(1e-8));
        var exact=calc(2000+500.0/24*20,0);assertThat(exact.gasStored()).isBetween(10000.0,20000.0);
        assertThat(calc(2100,0)).isEqualTo(r);
        var levels=new HashSet<Double>();
        for(int hour=0;hour<48;hour++) {
            var a=start.plusHours(hour);
            var result=HydrogenContinuousEngine.calculate(p,a,a.plusHours(1),2000,0,List.of());
            double filledLevel=result.gasStored()+result.gasUsed();
            assertThat(filledLevel).isBetween(10000.0,20000.0);levels.add(filledLevel);
        }
        assertThat(levels.size()).isGreaterThan(40);
    }
    @Test void fullTankExchangedWithoutDroppingRemainingProduction() {
        var r=calc(10000,1490);assertThat(r.exchanges()).isEqualTo(1);assertThat(r.shippedKg()).isEqualTo(1500);
        assertThat(r.hydrogenStored()).isCloseTo(1490+r.productionKg()-1500,within(1e-8));
    }
    @Test void randomizedControlsConserveBothInventoriesAcrossManyDays() {
        var random=new Random(813);double gas=10000,hg=0;
        for(int i=0;i<1000;i++) {
            String type=List.of("NORMAL","REDUCE","STOP","PLUS_DR").get(random.nextInt(4));
            var r=calc(gas,hg,cmd(0,type,random.nextDouble()*100,1));
            assertThat(r.gasStored()).isCloseTo(gas+r.gasRefilled()-r.gasUsed(),within(1e-6));
            assertThat(r.hydrogenStored()).isCloseTo(hg+r.productionKg()-r.shippedKg(),within(1e-6));
            assertThat(r.gasStored()).isBetween(2000.0,20000.0);assertThat(r.hydrogenStored()).isBetween(0.0,1499.99999999);
            gas=r.gasStored();hg=r.hydrogenStored();
        }
    }
}
