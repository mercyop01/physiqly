package com.physiqly.userservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; // <-- IMPORT THIS
import lombok.Data;

@Entity
@Table(name = "app_users") // <-- ADD THIS LINE to specify a safe table name
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password; 

}

