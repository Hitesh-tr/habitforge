package com.habitforge.habitservice.service;

import com.habitforge.habitservice.dto.CreateHabitRequest;
import com.habitforge.habitservice.dto.HabitResponse;
import com.habitforge.habitservice.entity.Habit;
import com.habitforge.habitservice.enums.HabitStatus;
import com.habitforge.habitservice.enums.ScheduleType;
import com.habitforge.habitservice.exception.HabitNotFoundException;
import com.habitforge.habitservice.exception.InvalidHabitRequestException;
import com.habitforge.habitservice.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class HabitService {

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    @Transactional
    public HabitResponse createHabit(CreateHabitRequest request) {
        Set<DayOfWeek> weekdays = request.weekdays() == null
            ? Set.of()
            : Set.copyOf(request.weekdays());

        validateSchedule(request.scheduleType(), weekdays);

        Habit habit = new Habit(
            request.name().trim(),
            request.description(),
            request.scheduleType(),
            weekdays
        );

        Habit savedHabit = habitRepository.save(habit);

        return toResponse(savedHabit);
    }

    @Transactional(readOnly = true)
    public List<HabitResponse> getActiveHabits() {
        return habitRepository
            .findAllByStatusOrderByCreatedAtDesc(HabitStatus.ACTIVE)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public HabitResponse getHabitById(UUID habitId) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new HabitNotFoundException(habitId));

        return toResponse(habit);
    }

    private void validateSchedule(
        ScheduleType scheduleType,
        Set<DayOfWeek> weekdays) {

        if (scheduleType == ScheduleType.DAILY && !weekdays.isEmpty()) {
            throw new InvalidHabitRequestException(
                "Daily habits must not specify weekdays"
            );
        }

        if (scheduleType == ScheduleType.SELECTED_WEEKDAYS
            && weekdays.isEmpty()) {
            throw new InvalidHabitRequestException(
                "At least one weekday is required for SELECTED_WEEKDAYS"
            );
        }
    }

    private HabitResponse toResponse(Habit habit) {
        return new HabitResponse(
            habit.getId(),
            habit.getName(),
            habit.getDescription(),
            habit.getScheduleType(),
            habit.getWeekdays(),
            habit.getStatus(),
            habit.getCreatedAt(),
            habit.getUpdatedAt()
        );
    }
}