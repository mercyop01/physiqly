package com.physiqly.workoutservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data // Lombok annotation to automatically create getters, setters, constructors, etc.
@Entity // Marks this class as a JPA entity (a blueprint for a database table)
@Table(name = "exercises") // Explicitly names the database table to avoid reserved keywords
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "Push Up", "Squat"
    private String description; // A brief on how to perform the exercise
    private String targetMuscle; // e.g., "Chest", "Legs", "Biceps"
    private String equipment; // e.g., "Dumbbells", "Bodyweight", "Barbell"
    private String difficulty; // e.g., "Beginner", "Intermediate", "Advanced"
}

