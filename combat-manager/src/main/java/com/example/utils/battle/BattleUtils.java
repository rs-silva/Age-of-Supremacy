package com.example.utils.battle;

import com.example.dto.UnitDTO;
import com.example.enums.UnitNames;
import com.example.models.Army;
import com.example.utils.UnitConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

@Component
public class BattleUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BattleUtils.class);

    private final Map<String, Integer> frontLineUnitsLimits;

    private final ActiveDefensesPhaseUtils activeDefensesPhaseUtils;

    private final EngagementPhaseUtils engagementPhaseUtils;

    private final UnitConfigUtils unitConfigUtils;

    private final RestTemplate restTemplate;

    public BattleUtils(ActiveDefensesPhaseUtils activeDefensesPhaseUtils, EngagementPhaseUtils engagementPhaseUtils, UnitConfigUtils unitConfigUtils, RestTemplate restTemplate) {
        this.activeDefensesPhaseUtils = activeDefensesPhaseUtils;
        this.engagementPhaseUtils = engagementPhaseUtils;
        this.unitConfigUtils = unitConfigUtils;
        this.frontLineUnitsLimits = BattleFrontLineUnitsLimits.getFrontLineUnitsLimits();
        this.restTemplate = restTemplate;
    }

    public int getArmiesMetric(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        int totalMetric = 0;

        totalMetric += getArmiesGroundUnitsMetricTotal(armies, metricFunction);
        totalMetric += getArmiesArmoredUnitsMetricTotal(armies, metricFunction);
        totalMetric += getArmiesAirUnitsMetricTotal(armies, metricFunction);

        return totalMetric;
    }

    public int applyScalingFactor(int value) {
        double scalingFactor = getScalingFactor();
        LOG.info("scalingFactor = {}", scalingFactor);

        return (int) (value * scalingFactor);
    }

    public int getArmiesGroundUnitsMetricTotal(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        /* Infantry + Engineers + Sniper */
        return getUnitsMetricTotal(armies, UnitNames.getGroundUnitsNames(), metricFunction);
    }

    public int getArmiesArmoredUnitsMetricTotal(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        /* APC + MBT + Artillery */
        return getUnitsMetricTotal(armies, UnitNames.getArmoredUnitsNames(), metricFunction);
    }

    public int getArmiesAirUnitsMetricTotal(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        /* Jet Fighter + Bomber + Recon */
        return getUnitsMetricTotal(armies, UnitNames.getAirUnitsNames(), metricFunction);
    }

    public int getUnitsMetricTotal(List<Army> armies, List<String> unitNames, Function<UnitDTO, Double> metricFunction) {
        double totalMetric = 0;

        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            for (String unitName : unitNames) {
                int unitAmount = armyUnits.getOrDefault(unitName, 0);

                double unitMetric = unitAmount * unitConfigUtils.getUnitMetric(unitName, metricFunction);

                totalMetric += unitMetric;
            }

        }

        return (int) totalMetric;
    }

    private double getScalingFactor() {
        /* Scaling factor = Get a scaling factor between 75% and 125% of the original attacking power value */
        double MEAN = 1;
        double STANDARD_DEVIATION = 0.07;

        return getNumberFromGaussianDistribution(MEAN, STANDARD_DEVIATION);
    }

    private double getNumberFromGaussianDistribution(double mean, double standardDeviation) {
        Random random = new Random();

        return random.nextGaussian() * standardDeviation + mean;
    }

}
