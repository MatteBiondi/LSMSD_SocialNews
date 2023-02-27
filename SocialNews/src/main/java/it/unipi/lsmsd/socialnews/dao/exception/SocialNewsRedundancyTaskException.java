package it.unipi.lsmsd.socialnews.dao.exception;

public class SocialNewsRedundancyTaskException extends Exception{
    public SocialNewsRedundancyTaskException(String errorMessage){
        super(errorMessage);
    }
}