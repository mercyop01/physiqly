package com.physiqly.calorieservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "daily_logs")
@Data // Lombok annotation to automatically create getters, setters, constructors, etc.
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This is the link back to the user in the user-service.
    // We only store the ID, not the whole user object.
    private Long userId;

    private LocalDate date;

    private int calorieGoal;
    private int breakfastCalories;
    private int lunchCalories;
    private int eveningSnackCalories;
    private int dinnerCalories;

    // A helper method to calculate total calories consumed
    public int getTotalCaloriesConsumed() {
        return breakfastCalories + lunchCalories + eveningSnackCalories + dinnerCalories;
    }
}
