package service;

import model.Student;
import java.util.ArrayList;
import service.MatchingService;

public class StudentService {

    ArrayList<Student> students = new ArrayList<>();
    MatchingService MatchingService = new MatchingService();

    public void addStudent(Student student) {

        students.add(student);

        System.out.println("Student Added Successfully");

    }

    public void displayStudents() {

        if (students.isEmpty()) {

            System.out.println("No Students Available");

        } else {

            for (Student s : students) {

                System.out.println("\n===== Student Details =====");

                System.out.println("Student ID : " + s.getStudentId());
                System.out.println("Name : " + s.getName());
                System.out.println("Gender : " + s.getGender());
                System.out.println("Age : " + s.getAge());
                System.out.println("Community : " + s.getCommunity());
                System.out.println("Annual Income : " + s.getAnnualIncome());
                System.out.println("CGPA : " + s.getCgpa());
                System.out.println("College : " + s.getCollege());
                System.out.println("Course : " + s.getCourse());

            }
        }
    }
    public ArrayList<Student> getStudents(){

        return students;

    }
}
