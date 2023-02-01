package it.unipi.lsmsd.socialnews.dto;

import java.util.Arrays;

public class ReaderDTO extends UserDTO{
    public String gender;
    public String country;
    public Byte[] picture;

    public ReaderDTO() { }

    public ReaderDTO(String email, String password, String fullName, String gender, String country, Byte[] picture) {
        super(email, password, fullName);
        this.gender = gender;
        this.country = country;
        this.picture = picture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Byte[] getPicture() {
        return picture;
    }

    public void setPicture(Byte[] picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "ReaderDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                ", picture=" + Arrays.toString(picture) +
                ", id='" + id + '\'' +
                '}';
    }
}
