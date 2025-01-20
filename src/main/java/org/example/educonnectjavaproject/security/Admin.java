package org.example.educonnectjavaproject.security;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@Entity

@AllArgsConstructor

public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length=45,nullable=false)
    private String name;
    @Column(length=45,nullable=false)
    private String surname;
    @Column(length=45,nullable=false)
    private String username;
    @Column(length=100,nullable=false)
    private String password;

    public Admin() {
    }

    public Admin(String name, String surname, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
