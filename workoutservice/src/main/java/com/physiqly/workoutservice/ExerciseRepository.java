package com.physiqly.workoutservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    // By extending JpaRepository, we get a lot of powerful database
    // methods for free, like save(), findById(), findAll(), delete(), etc.
    // We can add custom query methods here later if we need them.
}

