package it.unipi.lsmsd.socialnews.dao.model;

import java.util.Arrays;

public class Reader extends User {
    public String gender;
    public String country;

    public Reader() { super(); }

    public Reader(String email, String password, String fullName, String gender, String country) {
        super(email, password, fullName);
        this.gender = gender;
        this.country = country;
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

    @Override
    public String toString() {
        return "Reader{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
