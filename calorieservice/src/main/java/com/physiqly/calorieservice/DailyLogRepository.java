package com.physiqly.calorieservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    /**
     * Finds a daily log for a specific user on a specific date.
     * This custom method will be automatically implemented by Spring Data JPA
     * based on its name.
     *
     * @param userId The ID of the user.
     * @param date The specific date of the log.
     * @return An Optional containing the DailyLog if found, otherwise empty.
     */
    Optional<DailyLog> findByUserIdAndDate(Long userId, LocalDate date);
}
