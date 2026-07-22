package service;

import model.Student;
import model.Scholarship;
import model.GovernmentScheme;
import java.util.ArrayList;


public class MatchingService {


    public void checkScholarshipEligibility(
            Student student,
            ArrayList<Scholarship> scholarships) {


        System.out.println("\n===== Eligible Scholarships =====");


        for(Scholarship s : scholarships) {


            if(student.getCgpa() >= s.getMinCgpa()
                    && student.getAnnualIncome() <= s.getMaxIncome()
                    && (s.getCategory().equals("ALL")
                    || s.getCategory().equals(student.getCommunity()))) {


                System.out.println("Eligible : " + s.getName());

            }

        }

    }



    // 👇 INGA add pannanum (class kulla)

    public void checkSchemeEligibility(
            Student student,
            ArrayList<GovernmentScheme> schemes){


        System.out.println("\n===== Eligible Government Schemes =====");


        for(GovernmentScheme g : schemes){


            if(student.getAnnualIncome() <= g.getMaxIncome()
                    && (g.getCategory().equals("ALL")
                    || g.getCategory().equals(student.getCommunity()))) {


                System.out.println("Eligible : " + g.getName());

            }

        }

    }


}
