package it.unipi.lsmsd.socialnews.model.mongodb;

public class Admin extends User {
    Boolean isAdmin;

    public Admin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Admin(String email, String password, String fullName, Boolean isAdmin) {
        super(email, password, fullName);
        this.isAdmin = isAdmin;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
