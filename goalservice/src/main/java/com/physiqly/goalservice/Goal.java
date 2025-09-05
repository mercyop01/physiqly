package com.physiqly.goalservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "app_goals") // Use a safe table name
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // To link this goal to a user
    private double currentWeight;
    private double targetWeight;
    private double height; // in cm
    private LocalDate targetDate;

    // Fields to store the calculated plan
    private Integer calculatedDailyCalories;
    private Integer calculatedProteinGrams;
    private Integer calculatedCarbsGrams;
    private Integer calculatedFatGrams;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public Integer getCalculatedDailyCalories() {
        return calculatedDailyCalories;
    }

    public void setCalculatedDailyCalories(Integer calculatedDailyCalories) {
        this.calculatedDailyCalories = calculatedDailyCalories;
    }

    public Integer getCalculatedProteinGrams() {
        return calculatedProteinGrams;
    }

    public void setCalculatedProteinGrams(Integer calculatedProteinGrams) {
        this.calculatedProteinGrams = calculatedProteinGrams;
    }

    public Integer getCalculatedCarbsGrams() {
        return calculatedCarbsGrams;
    }

    public void setCalculatedCarbsGrams(Integer calculatedCarbsGrams) {
        this.calculatedCarbsGrams = calculatedCarbsGrams;
    }

    public Integer getCalculatedFatGrams() {
        return calculatedFatGrams;
    }

    public void setCalculatedFatGrams(Integer calculatedFatGrams) {
        this.calculatedFatGrams = calculatedFatGrams;
    }
}


