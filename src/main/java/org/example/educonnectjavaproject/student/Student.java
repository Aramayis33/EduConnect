package org.example.educonnectjavaproject.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.example.educonnectjavaproject.exercises.Exercise;
import org.example.educonnectjavaproject.feedback.Feedback;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.ratings.Rating;
import org.example.educonnectjavaproject.security.LogInHistory;
import org.example.educonnectjavaproject.summary.SummaryResult;

import java.sql.Date;
import java.util.List;

@Entity
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(length=45,  nullable=false)
    private String name;

    @Column(length=45, nullable=false)
    private String surname;
   private Date birthDay;
   private int fee;

    @Column(length=100, nullable=false)
    private String email;

    @Column(length=1000)
    private String password;

    @ManyToOne
    @JoinColumn(name = "group_id")
   private GroupInfo groupInfo;


    @Column(length = 100, nullable = false)
    private String image;

    private int isActivated;

    private int isBlocked;


    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SummaryResult> summaryResults;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Rating> ratings;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Exercise> exercises;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<LogInHistory> logInHistories;


    public Student() {
    }



    public Student(String name, String surname, Date birthDay, int fee, String email, String password, GroupInfo groupInfo, String image, int isActivated, int isBlocked) {
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.fee = fee;
        this.email = email;
        this.password = password;
        this.groupInfo = groupInfo;
        this.image = image;
        this.isActivated = isActivated;
        this.isBlocked = isBlocked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(int isActivated) {
        this.isActivated = isActivated;
    }

    public int getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(int isBlocked) {
        this.isBlocked = isBlocked;
    }

//    public List<LogInHistory> getLogInHistories() {
//        return logInHistories;
//    }
//
//    public void setLogInHistories(List<LogInHistory> logInHistories) {
//        this.logInHistories = logInHistories;
//    }
//
//    public List<SummaryResult> getSummaryResults() {
//        return summaryResults;
//    }
//
//    public void setSummaryResults(List<SummaryResult> summaryResults) {
//        this.summaryResults = summaryResults;
//    }
//
//    public List<Feedback> getFeedbacks() {
//        return feedbacks;
//    }
//
//    public void setFeedbacks(List<Feedback> feedbacks) {
//        this.feedbacks = feedbacks;
//    }
//
//    public List<Rating> getRatings() {
//        return ratings;
//    }
//
//    public void setRatings(List<Rating> ratings) {
//        this.ratings = ratings;
//    }
//
//    public List<Exercise> getExercises() {
//        return exercises;
//    }
//
//    public void setExercises(List<Exercise> exercises) {
//        this.exercises = exercises;
//    }
}
