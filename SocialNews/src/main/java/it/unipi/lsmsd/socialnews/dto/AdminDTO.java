package it.unipi.lsmsd.socialnews.dto;

public class AdminDTO extends UserDTO {
    Boolean isAdmin;

    public AdminDTO(){ }

    public AdminDTO(String email, String password, String fullName) {
        super(email, password, fullName);
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
