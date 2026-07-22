package service;

import model.GovernmentScheme;
import java.util.ArrayList;

public class GovernmentSchemeService {

    ArrayList<GovernmentScheme> schemes = new ArrayList<>();
    public ArrayList<GovernmentScheme> getSchemes(){

        return schemes;

    }


    public void addScheme(GovernmentScheme scheme){

        schemes.add(scheme);

        System.out.println("Government Scheme Added Successfully");

    }


    public void displaySchemes(){

        if(schemes.isEmpty()){

            System.out.println("No Government Schemes Available");

        }
        else{

            System.out.println("\n===== Government Schemes =====");

            for(GovernmentScheme g : schemes){

                System.out.println("Name : " + g.getName());
                System.out.println("Department : " + g.getDepartment());
                System.out.println("Benefit : " + g.getBenefit());
                System.out.println("Category : " + g.getCategory());

                System.out.println("-------------------------");

            }
        }
    }
}