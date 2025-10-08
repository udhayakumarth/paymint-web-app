package com.example.springapp.controller;



import com.example.springapp.BaseResponceDto;
import com.example.springapp.config.auth.JWTGenerator;
import com.example.springapp.goals.Goal;
import com.example.springapp.goals.GoalsService;
import com.example.springapp.user.UserEntity;
import com.example.springapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class GoalsController {

    @Autowired
    private GoalsService goalsService;

    @Autowired
    JWTGenerator jwtGenerator;

    @Autowired
    UserRepository userRepository;

    //API EndPoint for creating a Goal
    @PostMapping("/api/goals")
    public ResponseEntity<BaseResponceDto> createGoal(@RequestHeader(value = "Authorization", defaultValue = "") String token,@RequestBody Goal goal) {
        UserEntity user = userRepository.findByEmail(jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token))).orElseThrow();
        goal.setUser(user);
        Goal createdGoal = goalsService.createGoal(goal);
        return ResponseEntity.ok(new BaseResponceDto("success"));
    }

    //API EndPoint for Updating the existing a Goal
    @PutMapping("/api/goals/{id}")
    public ResponseEntity<BaseResponceDto> updateGoal(@PathVariable("id") Long id, @RequestBody Goal goal) {
        Goal updatedGoal = goalsService.updateGoal(id, goal);
        return ResponseEntity.ok(new BaseResponceDto("success",updatedGoal));
    }

    //API EndPoint for Deleting the existing Goal
    @DeleteMapping("/api/goals/{id}")
    public ResponseEntity<BaseResponceDto> deleteGoal(@PathVariable("id") Long id) {
        goalsService.deleteGoal(id);
        return ResponseEntity.ok(new BaseResponceDto("success"));
    }

    //API EndPoint for fetching a particular existing Goal
    @GetMapping("/api/goals/{id}")
    public ResponseEntity<Goal> getGoal(@PathVariable("id") Long id) {
        Optional<Goal> goal = goalsService.getGoal(id);
        if (goal.isPresent()) {
            return new ResponseEntity<>(goal.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //API EndPoint for fetching all the existing goals
    @GetMapping("/api/goals")
    public ResponseEntity<BaseResponceDto> getAllGoals(@RequestHeader(value = "Authorization", defaultValue = "") String token) {
        UserEntity user = userRepository.findByEmail(jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token))).orElseThrow();
        List<Goal> goals = goalsService.getAllGoalsByUser(user);
        return ResponseEntity.ok(new BaseResponceDto("success",goals));
    }

    @GetMapping("/goals")
    public ResponseEntity<List<Goal>> getAllTestGoals(){
        List<Goal> goals = new ArrayList<>();
        return ResponseEntity.ok(goals);
    }

    //Test Case
    @GetMapping("/goals/{id}")
    public ResponseEntity<List<Goal>> getTestGoalsById(@PathVariable Integer id){
        List<Goal> goals = new ArrayList<>();
        return ResponseEntity.ok(goals);
    }

}
