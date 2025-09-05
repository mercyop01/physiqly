package com.physiqly.workoutservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "http://localhost:5173") // Allows requests from our React frontend
public class ExerciseController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    // Endpoint to create a new exercise
    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody Exercise exercise) {
        Exercise savedExercise = exerciseRepository.save(exercise);
        return new ResponseEntity<>(savedExercise, HttpStatus.CREATED);
    }

    // Endpoint to get all exercises
    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return new ResponseEntity<>(exercises, HttpStatus.OK);
    }
}
