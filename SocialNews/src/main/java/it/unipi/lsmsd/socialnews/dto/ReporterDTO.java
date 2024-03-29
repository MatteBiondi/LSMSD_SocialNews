package it.unipi.lsmsd.socialnews.dto;

import java.util.Date;

public class ReporterDTO extends UserDTO{
    String gender;
    String location;
    Date dateOfBirth;
    String cell;
    String picture;
    Integer numOfReport;

    public ReporterDTO(){ }

    public ReporterDTO(String email, String password, String firstName, String lastName, String gender, String location,
                       Date dateOfBirth, String cell, String picture) {
        super(email, password,firstName, lastName);
        this.gender = gender;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.cell = cell;
        this.picture = picture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getNumOfReport() {
        return numOfReport;
    }

    public void setNumOfReport(Integer numOfReport) {
        this.numOfReport = numOfReport;
    }

    @Override
    public String toString() {
        return "ReporterDTO{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", location='" + location + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", cell='" + cell + '\'' +
                ", picture='" + picture + '\'' +
                ", numOfReport=" + numOfReport +
                '}';
    }
}
