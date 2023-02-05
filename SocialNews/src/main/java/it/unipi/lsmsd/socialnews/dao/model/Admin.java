package it.unipi.lsmsd.socialnews.dao.model;

public class Admin extends User {
    Boolean isAdmin;

    public Admin(){ isAdmin=true; }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
