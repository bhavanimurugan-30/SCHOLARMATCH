package model;

public class Student extends User {

    private int student_id;
    private String community;
    private double annualIncome;
    private double cgpa;
    private String college;
    private String course;
    private static int studentCount = 0;


    public Student(int student_id,
                   String name,
                   String gender,
                   int age,
                   String community,
                   double annualIncome,
                   double cgpa,
                   String college,
                   String course) {


        super(name, gender, age);


        this.student_id = student_id;
        this.community = community;
        this.annualIncome = annualIncome;
        this.cgpa = cgpa;
        this.college = college;
        this.course = course;
        studentCount++;

    }



    public int getStudent_id() {

        return student_id;

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



    public void setCommunity(String community) {

        this.community = community;

    }


    public void setAnnualIncome(double annualIncome) {

        this.annualIncome = annualIncome;

    }


    public void setCgpa(double cgpa) {

        this.cgpa = cgpa;

    }


    public void setCollege(String college) {

        this.college = college;

    }


    public void setCourse(String course) {

        this.course = course;

    }


    @Override
    public void displayUser(){

        System.out.println("\n===== Student Information =====");

        System.out.println("Student ID : " + student_id);
        System.out.println("Name : " + getName());
        System.out.println("Course : " + course);
        System.out.println("College : " + college);

    }

    public void displayStudent() {


        System.out.println("\n===== Student Details =====");

        System.out.println("Student ID : " + student_id);
        System.out.println("Name : " + getName());
        System.out.println("Gender : " + getGender());
        System.out.println("Age : " + getAge());
        System.out.println("Community : " + community);
        System.out.println("Annual Income : " + annualIncome);
        System.out.println("CGPA : " + cgpa);
        System.out.println("College : " + college);
        System.out.println("Course : " + course);

    }
    public static int getStudentCount(){

        return studentCount;

    }

}