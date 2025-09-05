package com.physiqly.goalservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "http://localhost:5173") // Allow requests from our React frontend
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    // Endpoint to get a user's current goal
    @GetMapping("/{userId}")
    public ResponseEntity<Goal> getGoalByUserId(@PathVariable Long userId) {
        Optional<Goal> goalOptional = goalRepository.findByUserId(userId);
        return goalOptional.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.noContent().build()); // Return 204 No Content if not found
    }

    // Endpoint to create or update a goal
    @PostMapping
    public ResponseEntity<Goal> createOrUpdateGoal(@RequestBody Goal newGoalDetails) {
        // Check if a goal for this user already exists
        Goal goal = goalRepository.findByUserId(newGoalDetails.getUserId())
                                   .orElse(newGoalDetails); // If not, use the new goal

        // Update the existing goal with new details
        goal.setCurrentWeight(newGoalDetails.getCurrentWeight());
        goal.setTargetWeight(newGoalDetails.getTargetWeight());
        goal.setHeight(newGoalDetails.getHeight());
        goal.setTargetDate(newGoalDetails.getTargetDate());

        // --- This is our core business logic ---
        calculatePlan(goal);
        Goal savedGoal = goalRepository.save(goal);
        return ResponseEntity.ok(savedGoal);
    }

    /**
     * Calculates the daily calories and macronutrients based on the user's goal.
     * This is a simplified calculation. A real app would be more complex.
     */
    private void calculatePlan(Goal goal) {
        // Basic Basal Metabolic Rate (BMR) calculation (Mifflin-St Jeor)
        // Note: We are missing gender and age, so this is a simplification.
        double bmr = (10 * goal.getCurrentWeight()) + (6.25 * goal.getHeight()) - (5 * 30) + 5; // Assuming age 30, male

        // Adjust for activity level (assuming lightly active)
        double tdee = bmr * 1.375;

        // Calculate total calorie deficit or surplus needed
        double totalCalorieChange = (goal.getTargetWeight() - goal.getCurrentWeight()) * 7700; // 7700 kcal per kg of fat/muscle

        // Calculate number of days to reach the goal
        long days = ChronoUnit.DAYS.between(java.time.LocalDate.now(), goal.getTargetDate());
        if (days <= 0) {
            days = 1; // Avoid division by zero
        }

        // Daily calorie adjustment
        double dailyCalorieAdjustment = totalCalorieChange / days;

        int finalDailyCalories = (int) (tdee + dailyCalorieAdjustment);

        // Set the calculated values on the goal object
        goal.setCalculatedDailyCalories(finalDailyCalories);

        // Simple macronutrient split (e.g., 40% carbs, 30% protein, 30% fat)
        goal.setCalculatedCarbsGrams((int) ((finalDailyCalories * 0.40) / 4));
        goal.setCalculatedProteinGrams((int) ((finalDailyCalories * 0.30) / 4));
        goal.setCalculatedFatGrams((int) ((finalDailyCalories * 0.30) / 9));
    }
}

