package com.physiqly.goalservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * Finds a goal for a specific user.
     * We use Optional in case a user hasn't set a goal yet.
     * @param userId The ID of the user.
     * @return An Optional containing the Goal if found, otherwise empty.
     */
    Optional<Goal> findByUserId(Long userId);
}


