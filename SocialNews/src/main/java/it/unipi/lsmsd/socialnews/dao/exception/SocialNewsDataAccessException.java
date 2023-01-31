package it.unipi.lsmsd.socialnews.dao.exception;

public class SocialNewsDataAccessException extends Exception{
    public SocialNewsDataAccessException(String errorMessage){
        super(errorMessage);
    }
}
