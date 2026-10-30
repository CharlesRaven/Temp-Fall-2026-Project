package edu.fscj.cen3024c.auth;


public class CurrentUserResponse {
    private Integer userID;

    private String name;

    private String email;

    private String role;

    public CurrentUserResponse(Integer userID, String name, String email, String role) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Integer getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
