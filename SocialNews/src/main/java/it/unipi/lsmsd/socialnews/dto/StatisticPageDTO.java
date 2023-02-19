package it.unipi.lsmsd.socialnews.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;

import static it.unipi.lsmsd.socialnews.service.util.Statistic.*;

public class StatisticPageDTO extends BaseDTO{
    ArrayNode mostActiveReaders;
    ObjectNode genderStatistic;
    ArrayNode nationalityStatistic;
    ArrayNode mostPopularReporters;
    

    public StatisticPageDTO(){ }

    public StatisticPageDTO(Map<String, Object> computedStatistics){
        this.mostActiveReaders = (ArrayNode) computedStatistics.getOrDefault(MOST_ACTIVE_READERS.toString(),null);
        this.genderStatistic = (ObjectNode) computedStatistics.getOrDefault(GENDER_STATISTIC.toString(),null);
        this.nationalityStatistic = (ArrayNode) computedStatistics.getOrDefault(NATIONALITY_STATISTIC.toString(),null);
        this.mostPopularReporters = (ArrayNode) computedStatistics.getOrDefault(MOST_POPULAR_REPORTERS.toString(),null);
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

    public ArrayNode getMostPopularReporters() {
        return mostPopularReporters;
    }

    public void setMostPopularReporters(ArrayNode mostPopularReporters) {
        this.mostPopularReporters = mostPopularReporters;
    }

    @Override
    public String toString() {
        return "StatisticPageDTO{" +
                "mostActiveReaders=" + mostActiveReaders +
                ", genderStatistic=" + genderStatistic +
                ", nationalityStatistic=" + nationalityStatistic +
                ", mostPopularReporter=" + mostPopularReporters +
                '}';
    }
}
