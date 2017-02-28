package com.anat0l;


import java.util.Date;

public class Person {
    private Long id;
    private String name;
    private int age;
    private int admin;
    private Date createdDate;

    public Person(String name, int age, int admin) {
        this.name = name;
        this.age = age;
        this.admin = admin;
    }

    public Person() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public int getAdmin() {
        return admin;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
