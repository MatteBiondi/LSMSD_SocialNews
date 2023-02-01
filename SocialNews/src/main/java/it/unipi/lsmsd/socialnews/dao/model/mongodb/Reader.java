package it.unipi.lsmsd.socialnews.dao.model.mongodb;

import java.util.Arrays;

public class Reader extends User {
    public String gender;
    public String country;
    public Byte[] picture;

    public Reader() { super(); }

    public Reader(String email, String password, String fullName, String gender, String country, Byte[] picture) {
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
        return "Reader{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                ", picture=" + Arrays.toString(picture) +
                '}';
    }
}
