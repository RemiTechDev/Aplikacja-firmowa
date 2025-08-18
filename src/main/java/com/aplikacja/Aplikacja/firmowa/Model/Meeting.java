package com.aplikacja.Aplikacja.firmowa.Model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MeetingStatus status = MeetingStatus.PLANOWANE;

    /** Do kogo spotkanie jest przypisane (właściciel) */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Kto utworzył spotkanie (twórca) */
    @ManyToOne
    @JoinColumn(name = "created_by_id") // nullable=true dla zgodności z istniejącymi danymi
    private User createdBy;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingComment> comments = new ArrayList<>();

    // === Konstruktory ===
    public Meeting() {}

    public Meeting(String title,
                   String description,
                   LocalDateTime dateTime,
                   String location,
                   MeetingStatus status,
                   User user) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.status = status;
        this.user = user;
    }

    // === Gettery i Settery ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public MeetingStatus getStatus() { return status; }
    public void setStatus(MeetingStatus status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public List<MeetingComment> getComments() { return comments; }
    public void setComments(List<MeetingComment> comments) { this.comments = comments; }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", dateTime=" + dateTime +
                ", user=" + (user != null ? user.getLogin() : "null") +
                ", createdBy=" + (createdBy != null ? createdBy.getLogin() : "null") +
                '}';
    }
}
