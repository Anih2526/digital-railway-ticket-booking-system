package com.railway.model;

public class User {

    private int userId;
    private String fullName;
    private String email;
    private String password;
    private String role;

    public User(int userId, String fullName, String email,
                String password, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
