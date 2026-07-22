package menu;

import model.Student;
import model.Scholarship;
import model.GovernmentScheme;

import service.StudentService;
import service.ScholarshipService;
import service.GovernmentSchemeService;
import service.MatchingService;

import java.util.Scanner;


public class Menu {

    Scanner sc = new Scanner(System.in);

    StudentService service = new StudentService();
    ScholarshipService scholarshipService = new ScholarshipService();
    GovernmentSchemeService schemeService = new GovernmentSchemeService();
    MatchingService matchingService = new MatchingService();


    public void displayMenu() {


        // Sample Scholarships

        Scholarship s1 = new Scholarship(
                "Merit Scholarship",
                "ScholarMatch Trust",
                25000,
                8.0,
                300000,
                "ALL"
        );

        scholarshipService.addScholarship(s1);


        Scholarship s2 = new Scholarship(
                "Engineering Scholarship",
                "CIT Foundation",
                50000,
                8.5,
                500000,
                "GENERAL"
        );

        scholarshipService.addScholarship(s2);



        // Sample Government Schemes

        GovernmentScheme g1 = new GovernmentScheme(
                "OBC Welfare Scheme",
                "State Government",
                "OBC students financial support",
                "Financial Assistance",
                200000,
                "MBC"
        );

        schemeService.addScheme(g1);



        GovernmentScheme g2 = new GovernmentScheme(
                "Student Education Support",
                "Government of India",
                "Higher education support",
                "Fee Assistance",
                400000,
                "ALL"
        );

        schemeService.addScheme(g2);



        int choice;


        do {


            System.out.println("\n===== ScholarMatch =====");
            System.out.println("1. Add Student");
            System.out.println("2. View Scholarships");
            System.out.println("3. View Government Schemes");
            System.out.println("4. Check Eligibility");
            System.out.println("5. Exit");
            System.out.println("6. View Students");


            System.out.print("Enter your choice: ");

            choice = sc.nextInt();



            switch(choice) {


                case 1:


                    System.out.print("Enter Student ID: ");
                    int id = sc.nextInt();

                    sc.nextLine();


                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();


                    System.out.print("Enter Gender: ");
                    String gender = sc.nextLine();


                    System.out.print("Enter Age: ");
                    int age = sc.nextInt();

                    sc.nextLine();


                    System.out.print("Enter Community: ");
                    String community = sc.nextLine();


                    System.out.print("Enter Annual Income: ");
                    double income = sc.nextDouble();


                    System.out.print("Enter CGPA: ");
                    double cgpa = sc.nextDouble();

                    sc.nextLine();


                    System.out.print("Enter College: ");
                    String college = sc.nextLine();


                    System.out.print("Enter Course: ");
                    String course = sc.nextLine();



                    Student student = new Student(
                            id,
                            name,
                            gender,
                            age,
                            community,
                            income,
                            cgpa,
                            college,
                            course
                    );


                    service.addStudent(student);


                    break;



                case 2:


                    scholarshipService.displayScholarships();


                    break;



                case 3:


                    schemeService.displaySchemes();


                    break;



                case 4:


                    if(service.getStudents().isEmpty()) {


                        System.out.println("Please Add Student First");


                    }
                    else {


                        Student student1 = service.getStudents().get(0);


                        matchingService.checkScholarshipEligibility(
                                student1,
                                scholarshipService.getScholarships()
                        );


                        matchingService.checkSchemeEligibility(
                                student1,
                                schemeService.getSchemes()
                        );


                    }


                    break;



                case 5:


                    System.out.println("Thank You!");


                    break;



                case 6:


                    service.displayStudents();


                    break;



                default:


                    System.out.println("Invalid Choice");


            }


        } while(choice != 5);


    }

}