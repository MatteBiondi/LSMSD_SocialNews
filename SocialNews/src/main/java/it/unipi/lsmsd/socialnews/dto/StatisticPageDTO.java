package it.unipi.lsmsd.socialnews.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;

public class StatisticPageDTO extends BaseDTO{
    ArrayNode mostActiveReaders;
    ObjectNode genderStatistic;
    ArrayNode nationalityStatistic;
    ArrayNode hottestMomentsOfDay;

    public StatisticPageDTO(){ }

    public StatisticPageDTO(Map<String, Object> computedStatistics){
        this.mostActiveReaders = (ArrayNode) computedStatistics.getOrDefault("mostActiveReaders",null);
        this.genderStatistic = (ObjectNode) computedStatistics.getOrDefault("genderStatistic",null);
        this.nationalityStatistic = (ArrayNode) computedStatistics.getOrDefault("nationalityStatistic",null);
        this.hottestMomentsOfDay = (ArrayNode) computedStatistics.getOrDefault("hottestMomentsOfDay",null);
    }

    public ArrayNode getMostActiveReaders() {
        return mostActiveReaders;
    }

    public void setMostActiveReaders(ArrayNode mostActiveReaders) {
        this.mostActiveReaders = mostActiveReaders;
    }

    public ObjectNode getGenderStatistic() {
        return genderStatistic;
    }

    public void setGenderStatistic(ObjectNode genderStatistic) {
        this.genderStatistic = genderStatistic;
    }

    public ArrayNode getNationalityStatistic() {
        return nationalityStatistic;
    }

    public void setNationalityStatistic(ArrayNode nationalityStatistic) {
        this.nationalityStatistic = nationalityStatistic;
    }

    public ArrayNode getHottestMomentsOfDay() {
        return hottestMomentsOfDay;
    }

    public void setHottestMomentsOfDay(ArrayNode hottestMomentsOfDay) {
        this.hottestMomentsOfDay = hottestMomentsOfDay;
    }

    @Override
    public String toString() {
        return "StatisticPageDTO{" +
                "mostActiveReaders=" + mostActiveReaders +
                ", genderStatistic=" + genderStatistic +
                ", nationalityStatistic=" + nationalityStatistic +
                ", hottestMomentsOfDay=" + hottestMomentsOfDay +
                '}';
    }
}
