package it.unipi.lsmsd.socialnews.dto;

public class AdminDTO extends UserDTO {
    Boolean isAdmin;

    public AdminDTO(){ }

    public AdminDTO(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
        this.isAdmin = true;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "AdminDTO{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
