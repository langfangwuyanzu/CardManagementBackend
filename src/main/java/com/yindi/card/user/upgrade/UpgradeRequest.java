package com.yindi.card.user.upgrade;

import jakarta.validation.constraints.NotBlank;

public class UpgradeRequest {
    @NotBlank
    private String newLevel;        // Upgraded card level
    @NotBlank
    private String trainingUndertaken;
    @NotBlank
    private String trainingProvider;
    @NotBlank
    private String trainingDate;    // format：MM/yyyy

    // getters & setters
    public String getNewLevel() {
        return newLevel;
    }
    public void setNewLevel(String newLevel) {
        this.newLevel = newLevel;
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
