package com.yindi.card.user;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="first_name", nullable=false)
    private String firstName;

    @Column(name="last_name", nullable=false)
    private String lastName;

    // 如无需持久化密码，可改为 @Transient
    private String password;

    @Column(name="year_of_birth", nullable=false)
    private Integer yearOfBirth;

    @Column(name="card_level", nullable=false, length=50)
    private String cardLevel;

    @Column(name="street_address", nullable=false)
    private String streetAddress;

    @Column(name="suburb", nullable=false)
    private String suburb;

    @Column(name="state", nullable=false)
    private String state;

    @Column(name="postcode", nullable=false)
    private String postcode;

    @Column(name="email", nullable=false, unique=true)
    private String email;

    @Column(name="photo_url")
    private String photoUrl;

    @Column(name="role")
    private String role;

    @Column(name="expire_date")
    private LocalDate expireDate;

    // ✅ 新增审批状态字段
    @Column(name = "status")
    private String status; // PENDING / APPROVED / REJECTED

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;


    // 卡片发放状态（NONE, REQUESTED, APPROVED, ACTIVATED, REVOKED, EXPIRED）
    @Column(name = "card_issue_status")
    @Enumerated(EnumType.STRING)
    private CardIssueStatus cardIssueStatus;  // 你可以定义一个枚举类见下方

    // 卡号
    @Column(name = "card_number", unique = true)
    private String cardNumber;

    // 卡片激活时间（对应数据库 card_activated_at）
    @Column(name = "card_activated_at")
    private LocalDateTime cardActivatedAt;


    // ✅ 只保留这一份 experiences
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserTrainingExperience> experiences = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // 方便维护双向关系
    public void addExperience(UserTrainingExperience e){
        e.setUser(this);
        this.experiences.add(e);
    }

    // ===== Getters / Setters =====
    public Long getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getExpireDate() { return expireDate; }
    public void setExpireDate(LocalDate expireDate) { this.expireDate = expireDate; }

    // 审批状态
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // 审批时间
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    // 审批人
    public Long getApprovedBy() {
        return approvedBy;
    }
    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    // 拒绝原因
    public String getRejectionReason() {
        return rejectionReason;
    }
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    // 激活时间
    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }
    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }

    public CardIssueStatus getCardIssueStatus() {
        return cardIssueStatus;
    }

    public void setCardIssueStatus(CardIssueStatus cardIssueStatus) {
        this.cardIssueStatus = cardIssueStatus;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDateTime getCardActivatedAt() {
        return cardActivatedAt;
    }

    public void setCardActivatedAt(LocalDateTime cardActivatedAt) {
        this.cardActivatedAt = cardActivatedAt;
    }


    // 是否激活
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }


    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<UserTrainingExperience> getExperiences() { return experiences; }
    public void setExperiences(List<UserTrainingExperience> experiences) { this.experiences = experiences; }
}

