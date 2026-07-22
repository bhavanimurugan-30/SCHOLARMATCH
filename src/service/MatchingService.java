package service;

import model.Student;
import model.Scholarship;
import model.GovernmentScheme;

import abstraction.EligibilityChecker;

import java.util.ArrayList;


public class MatchingService implements EligibilityChecker {


    // Abstraction interface method

    @Override
    public void checkEligibility(Student student) {

        System.out.println("Checking eligibility for : "
                + student.getName());

    }



    // Scholarship Eligibility Check

    public void checkScholarshipEligibility(
            Student student,
            ArrayList<Scholarship> scholarships) {


        System.out.println("\n===== Eligible Scholarships =====");


        for(Scholarship s : scholarships) {


            if(student.getCgpa() >= s.getMinCgpa()
                    && student.getAnnualIncome() <= s.getMaxIncome()
                    && (s.getCategory().equalsIgnoreCase("ALL")
                    || s.getCategory().equalsIgnoreCase(student.getCommunity()))) {


                System.out.println("Eligible : " + s.getName());

            }

        }

    }





    // Government Scheme Eligibility Check

    public void checkSchemeEligibility(
            Student student,
            ArrayList<GovernmentScheme> schemes) {


        System.out.println("\n===== Eligible Government Schemes =====");


        for(GovernmentScheme g : schemes) {


            if(student.getAnnualIncome() <= g.getMaxIncome()
                    && (g.getCategory().equalsIgnoreCase("ALL")
                    || g.getCategory().equalsIgnoreCase(student.getCommunity()))) {


                System.out.println("Eligible : " + g.getName());

            }

        }

    }


}