package model;

public class Opportunity {

    protected String name;
    protected String provider;


    public void display() {
        System.out.println(name + " - " + provider);
    }


    public String getName() {
        return name;
    }


    public String getProvider() {
        return provider;
    }
}
