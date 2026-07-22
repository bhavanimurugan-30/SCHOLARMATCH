package service;

import model.Scholarship;
import java.util.ArrayList;

public class ScholarshipService {

    ArrayList<Scholarship> scholarships = new ArrayList<>();

    public ArrayList<Scholarship> getScholarships(){

        return scholarships;

    }


    public void addScholarship(Scholarship scholarship) {

        scholarships.add(scholarship);

        System.out.println("Scholarship Added Successfully");

    }


    public void displayScholarships() {

        if(scholarships.isEmpty()) {

            System.out.println("No Scholarships Available");

        }
        else {

            System.out.println("\n===== Scholarship List =====");

            for(Scholarship s : scholarships) {

                System.out.println("Name : " + s.getName());
                System.out.println("Provider : " + s.getProvider());
                System.out.println("Amount : " + s.getAmount());
                System.out.println("Minimum CGPA : " + s.getMinCgpa());
                System.out.println("Category : " + s.getCategory());

                System.out.println("-------------------------");
            }
        }
    }
}
