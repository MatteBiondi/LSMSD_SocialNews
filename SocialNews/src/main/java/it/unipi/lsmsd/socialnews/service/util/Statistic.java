package it.unipi.lsmsd.socialnews.service.util;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Locale;

public enum Statistic {
    MOST_ACTIVE_READERS("mostActiveReaders"),
    GENDER_STATISTIC("genderStatistic"),
    NATIONALITY_STATISTIC("nationalityStatistic"),
    MOST_POPULAR_REPORTERS("mostPopularReporters");

    private static Integer defaultWindowSize;
    private static Integer defaultLastN;
    private static TemporalUnit defaultUnitOfTime;

    private final String key;
    private Integer windowSize;
    private Integer lastN;
    private TemporalUnit unitOfTime;

    public static void configure(Integer defaultWindowSize, Integer defaultLastN, String defaultUnitOfTime){
        Statistic.defaultWindowSize = defaultWindowSize;
        Statistic.defaultLastN = defaultLastN;
        Statistic.defaultUnitOfTime = parseUnitOfTime(defaultUnitOfTime);
    }

    private static TemporalUnit parseUnitOfTime(String value){
        List<String> supportedUnitOfTime = List.of("HOURS","DAYS","WEEKS","MONTHS","YEARS");
        for(String unit: supportedUnitOfTime){
            if(value.toLowerCase(Locale.ROOT).equals(unit.toLowerCase(Locale.ROOT)) ||
                    value.toLowerCase(Locale.ROOT).equals(unit.toLowerCase(Locale.ROOT).substring(0, unit.length()-1)))
                return ChronoUnit.valueOf(unit);
        }
        throw new IllegalArgumentException("Not a supported unit of time");
    }

    public static Statistic fromString(String value){
        if(value.toLowerCase(Locale.ROOT).equals(MOST_ACTIVE_READERS.toString().toLowerCase(Locale.ROOT)))
            return MOST_ACTIVE_READERS;
        else if(value.toLowerCase(Locale.ROOT).equals(GENDER_STATISTIC.toString().toLowerCase(Locale.ROOT)))
            return GENDER_STATISTIC;
        else if(value.toLowerCase(Locale.ROOT).equals(NATIONALITY_STATISTIC.toString().toLowerCase(Locale.ROOT)))
            return NATIONALITY_STATISTIC;
        else if(value.toLowerCase(Locale.ROOT).equals(MOST_POPULAR_REPORTERS.toString().toLowerCase(Locale.ROOT)))
            return MOST_POPULAR_REPORTERS;
        else throw new IllegalArgumentException("Not a statistic");

    }

    public static Statistic getStatistic(Statistic s, Integer windowSize, Integer lastN, String unitOfTime){
        s.setWindowSize(windowSize);
        s.setLastN(lastN);
        s.setUnitOfTime(parseUnitOfTime(unitOfTime));
        return s;
    }

    public static Statistic getStatistic(Statistic s, Integer lastN, String unitOfTime){
        s.setLastN(lastN);
        s.setUnitOfTime(parseUnitOfTime(unitOfTime));
        return s;
    }

    public static Statistic getStatistic(Statistic s){
        return s;
    }

    Statistic(String key){
        this.key = key;
    }

    public Integer getWindowSize() {
        return windowSize != null ? windowSize:defaultWindowSize;
    }

    private void setWindowSize(Integer windowSize) {
        this.windowSize = windowSize;
    }

    public Integer getLastN() {
        return lastN != null? lastN:defaultLastN;
    }

    private void setLastN(Integer lastN) {
        this.lastN = lastN;
    }

    public TemporalUnit getUnitOfTime() {
        return unitOfTime != null ? unitOfTime:defaultUnitOfTime;
    }

    private void setUnitOfTime(TemporalUnit unitOfTime) {
        this.unitOfTime = unitOfTime;
    }

    @Override
    public String toString(){
        return key;
    }
}
