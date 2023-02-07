package it.unipi.lsmsd.socialnews.service.util;

import java.time.temporal.TemporalUnit;

public enum Statistic {
    MOST_ACTIVE_READERS("mostActiveReaders"),
    GENDER_STATISTIC("genderStatistic"),
    NATIONALITY_STATISTIC("nationalityStatistic"),
    HOTTEST_MOMENTS_OF_DAY("hottestMomentsOfDay");

    private final String key;
    private Integer windowSize;
    private Integer lastN;
    private TemporalUnit unitOfTime;

    Statistic(String key){
        this.key = key;
    }

    public Integer getWindowSize() {
        return windowSize;
    }

    private void setWindowSize(Integer windowSize) {
        this.windowSize = windowSize;
    }

    public Integer getLastN() {
        return lastN;
    }

    private void setLastN(Integer lastN) {
        this.lastN = lastN;
    }

    public TemporalUnit getUnitOfTime() {
        return unitOfTime;
    }

    private void setUnitOfTime(TemporalUnit unitOfTime) {
        this.unitOfTime = unitOfTime;
    }

    public static Statistic getStatistic(Statistic s, Integer windowSize, Integer lastN, TemporalUnit unitOfTime){
        s.setWindowSize(windowSize);
        s.setLastN(lastN);
        s.setUnitOfTime(unitOfTime);
        return s;
    }

    public static Statistic getStatistic(Statistic s, Integer lastN, TemporalUnit unitOfTime){
        s.setLastN(lastN);
        s.setUnitOfTime(unitOfTime);
        return s;
    }

    public static Statistic getStatistic(Statistic s){
        return s;
    }

    @Override
    public String toString(){
        return key;
    }
}
