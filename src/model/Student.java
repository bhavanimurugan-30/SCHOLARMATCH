package model;

public class Student {

    private int studentId;
    private String name;
    private String gender;
    private int age;
    private String community;
    private double annualIncome;
    private double cgpa;
    private String college;
    private String course;

    // Constructor
    public Student(int studentId, String name, String gender, int age,
                   String community, double annualIncome, double cgpa,
                   String college, String course) {

        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.community = community;
        this.annualIncome = annualIncome;
        this.cgpa = cgpa;
        this.college = college;
        this.course = course;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getCommunity() {
        return community;
    }

    public double getAnnualIncome() {
        return annualIncome;
    }

    public double getCgpa() {
        return cgpa;
    }

    public String getCollege() {
        return college;
    }

    public String getCourse() {
        return course;
    }
}