package com.habitforge.habitservice.dto;

import com.habitforge.habitservice.enums.HabitStatus;
import com.habitforge.habitservice.enums.ScheduleType;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record HabitResponse(
    UUID id,
    String name,
    String description,
    ScheduleType scheduleType,
    Set<DayOfWeek> weekdays,
    HabitStatus status,
    Instant createdAt,
    Instant updatedAt
) {}