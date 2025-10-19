package org.example.models;

public class User {
    private final int id;
    private final String username;
    private final String password;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
        this.password = null;
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }


    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}
