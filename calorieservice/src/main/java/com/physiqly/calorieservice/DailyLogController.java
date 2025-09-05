package com.physiqly.calorieservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "http://localhost:5173") // Allow requests from our React frontend
public class DailyLogController {

    @Autowired
    private DailyLogRepository dailyLogRepository;

    /**
     * Gets the daily log for a specific user for today's date.
     * If no log exists for today, it creates a new one with default goals.
     */
    @GetMapping("/{userId}/today")
    public ResponseEntity<DailyLog> getTodaysLog(@PathVariable Long userId) {
        LocalDate today = LocalDate.now();
        DailyLog log = dailyLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> {
                    // If no log exists for today, create a new one
                    DailyLog newLog = new DailyLog();
                    newLog.setUserId(userId);
                    newLog.setDate(today);
                    newLog.setCalorieGoal(2500); // Default goal
                    return dailyLogRepository.save(newLog);
                });
        return ResponseEntity.ok(log);
    }

    /**
     * Creates or updates a daily log.
     * The entire log object is sent from the frontend and saved.
     */
    @PostMapping
    public ResponseEntity<DailyLog> updateLog(@RequestBody DailyLog dailyLog) {
        // The save method handles both creating a new record and updating an existing one
        DailyLog savedLog = dailyLogRepository.save(dailyLog);
        return ResponseEntity.ok(savedLog);
    }
}
