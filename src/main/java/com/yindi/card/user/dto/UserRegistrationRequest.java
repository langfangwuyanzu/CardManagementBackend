package com.yindi.card.user.dto;

import java.time.LocalDate;
import java.util.List;

public class UserRegistrationRequest {
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String cardLevel;
    private String streetAddress;
    private String suburb;
    private String state;
    private String postcode;
    private String email;
    private String verifyCode;
    private String photoUrl;
    private List<ExperienceDto> experiences;

    public static class ExperienceDto {
        private String trainingName;
        private String trainingProvider;
        private LocalDate dateOfTraining;

        public String getTrainingName() { return trainingName; }
        public void setTrainingName(String trainingName) { this.trainingName = trainingName; }
        public String getTrainingProvider() { return trainingProvider; }
        public void setTrainingProvider(String trainingProvider) { this.trainingProvider = trainingProvider; }
        public LocalDate getDateOfTraining() { return dateOfTraining; }
        public void setDateOfTraining(LocalDate dateOfTraining) { this.dateOfTraining = dateOfTraining; }
    }

    // getters & setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Integer getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(Integer yearOfBirth) { this.yearOfBirth = yearOfBirth; }
    public String getCardLevel() { return cardLevel; }
    public void setCardLevel(String cardLevel) { this.cardLevel = cardLevel; }
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getSuburb() { return suburb; }
    public void setSuburb(String suburb) { this.suburb = suburb; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getVerifyCode() { return verifyCode; }
    public void setVerifyCode(String verifyCode) { this.verifyCode = verifyCode; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public List<ExperienceDto> getExperiences() { return experiences; }
    public void setExperiences(List<ExperienceDto> experiences) { this.experiences = experiences; }
}
