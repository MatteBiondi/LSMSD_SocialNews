package it.unipi.lsmsd.socialnews.dto;

public class ReaderDTO extends UserDTO{
    public String gender;
    public String country;

    public ReaderDTO() { }

    public ReaderDTO(String email, String password, String firstName, String lastName, String gender, String country) {
        super(email, password, firstName, lastName);
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
        return "ReaderDTO{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
