package it.unipi.lsmsd.socialnews.dao.model.mongodb;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Reporter extends User {
    String reporterId;
    String gender;
    String location;
    Date dateOfBirth;
    String cell;
    Byte[] picture;
    Integer numOfReport;
    List<Post> posts;

    public Reporter(){ }

    public Reporter(String email, String password, String fullName, String reporterId, String gender, String location, Date dateOfBirth, String cell, Byte[] picture, Integer numOfReport, List<Post> posts) {
        super(email, password, fullName);
        this.reporterId = reporterId;
        this.gender = gender;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.cell = cell;
        this.picture = picture;
        this.numOfReport = numOfReport;
        this.posts = posts;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
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

    public Byte[] getPicture() {
        return picture;
    }

    public void setPicture(Byte[] picture) {
        this.picture = picture;
    }

    public Integer getNumOfReport() {
        return numOfReport;
    }

    public void setNumOfReport(Integer numOfReport) {
        this.numOfReport = numOfReport;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "Reporter{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", reporterId='" + reporterId + '\'' +
                ", gender='" + gender + '\'' +
                ", location='" + location + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", cell='" + cell + '\'' +
                ", picture=" + Arrays.toString(picture) +
                ", numOfReport=" + numOfReport +
                ", posts=" + posts +
                '}';
    }
}
