package it.unipi.lsmsd.socialnews.dto;

public abstract class UserDTO extends BaseDTO{
    protected String id;
    protected String email;
    protected String password;
    protected String fullName;

    public UserDTO() { }

    public UserDTO(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.fullName = String.format("%s %s", firstName, lastName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setFullName(String firstName, String lastName) {
        this.fullName = String.format("%s %s", firstName, lastName);
    }


    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
