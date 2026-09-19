package com.smartnutrition.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 20)
    private String duration;

    @Column(nullable = false, length = 30)
    private String status;

    public Holiday() {}

    public Holiday(String name, LocalDate startDate, LocalDate endDate, String duration, String status) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Holiday h = new Holiday();
        public Builder name(String v) { h.name = v; return this; }
        public Builder startDate(LocalDate v) { h.startDate = v; return this; }
        public Builder endDate(LocalDate v) { h.endDate = v; return this; }
        public Builder duration(String v) { h.duration = v; return this; }
        public Builder status(String v) { h.status = v; return this; }
        public Holiday build() { return h; }
    }
}
