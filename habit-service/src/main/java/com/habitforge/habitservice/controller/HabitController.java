package com.habitforge.habitservice.controller;

import com.habitforge.habitservice.dto.CreateHabitRequest;
import com.habitforge.habitservice.dto.HabitResponse;
import com.habitforge.habitservice.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(
        @Valid @RequestBody CreateHabitRequest request) {

        HabitResponse response = habitService.createHabit(request);

        return ResponseEntity
            .created(URI.create("/api/v1/habits/" + response.id()))
            .body(response);
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> getActiveHabits() {
        return ResponseEntity.ok(habitService.getActiveHabits());
    }

    @GetMapping("/{habitId}")
    public ResponseEntity<HabitResponse> getHabitById(
        @PathVariable UUID habitId) {

        return ResponseEntity.ok(habitService.getHabitById(habitId));
    }
}