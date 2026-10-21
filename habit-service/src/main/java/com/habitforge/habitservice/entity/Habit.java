package com.habitforge.habitservice.entity;

import com.habitforge.habitservice.enums.HabitStatus;
import com.habitforge.habitservice.enums.ScheduleType;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 30)
    private ScheduleType scheduleType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "habit_weekdays",
        joinColumns = @JoinColumn(name = "habit_id")
    )
    @Column(name = "weekday", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> weekdays = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HabitStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Habit() {
        // Required by JPA
    }

    public Habit(
        String name,
        String description,
        ScheduleType scheduleType,
        Set<DayOfWeek> weekdays) {
        this.name = name;
        this.description = description;
        this.scheduleType = scheduleType;
        this.weekdays = weekdays == null
            ? new HashSet<>()
            : new HashSet<>(weekdays);
        this.status = HabitStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public Set<DayOfWeek> getWeekdays() {
        return Set.copyOf(weekdays);
    }

    public HabitStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}