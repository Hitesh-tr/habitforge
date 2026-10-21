package com.habitforge.habitservice.repository;

import com.habitforge.habitservice.entity.Habit;
import com.habitforge.habitservice.enums.HabitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HabitRepository extends JpaRepository<Habit, UUID> {

    List<Habit> findAllByStatusOrderByCreatedAtDesc(HabitStatus status);
}