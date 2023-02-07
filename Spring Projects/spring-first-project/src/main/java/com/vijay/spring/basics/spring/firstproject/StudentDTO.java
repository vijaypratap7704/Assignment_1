package com.vijay.spring.basics.spring.firstproject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="student")
public class StudentDTO {

    private Integer rollno;
    private String name;
    private List<Subject> subjects;

    public StudentDTO() {}

    public Integer getRollno() {
        return rollno;
    }

    public void setRollno(Integer rollno) {
        this.rollno = rollno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}