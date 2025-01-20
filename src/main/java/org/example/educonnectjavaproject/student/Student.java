package org.example.educonnectjavaproject.student;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;

import java.sql.Date;

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

    @Column(length=1000, nullable=false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "group_id")
   private GroupInfo groupInfo;


    @Column(length = 100, nullable = false)
    private String image;




    public Student() {
    }



    public Student(String name, String surname, Date birthDay, int fee, String email, String password, GroupInfo groupInfo, String image) {
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.fee = fee;
        this.email = email;
        this.password = password;
        this.groupInfo = groupInfo;
        this.image = image;
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


}
