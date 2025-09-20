package com.yindi.card.user;

import jakarta.persistence.*;

@Entity
@Table(name = "user_training_experience")
public class UserTrainingExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "training_undertaken", nullable = false)
    private String trainingUndertaken;

    @Column(name = "training_provider", nullable = false)
    private String trainingProvider;

    @Column(name = "training_date", length = 10)
    private String trainingDate; // 存 mm/yyyy

    // ========= Getter / Setter =========

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTrainingUndertaken() {
        return trainingUndertaken;
    }

    public void setTrainingUndertaken(String trainingUndertaken) {
        this.trainingUndertaken = trainingUndertaken;
    }

    public String getTrainingProvider() {
        return trainingProvider;
    }

    public void setTrainingProvider(String trainingProvider) {
        this.trainingProvider = trainingProvider;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }
}
