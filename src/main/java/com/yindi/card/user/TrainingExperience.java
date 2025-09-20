package com.yindi.card.user;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "training_experiences")
public class TrainingExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainingName;
    private String trainingProvider;
    private LocalDate dateOfTraining;

    // 👇 反向关联到 User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")   // 外键列 user_id
    private User user;

    // ===== getter / setter =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getTrainingProvider() {
        return trainingProvider;
    }

    public void setTrainingProvider(String trainingProvider) {
        this.trainingProvider = trainingProvider;
    }

    public LocalDate getDateOfTraining() {
        return dateOfTraining;
    }

    public void setDateOfTraining(LocalDate dateOfTraining) {
        this.dateOfTraining = dateOfTraining;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
