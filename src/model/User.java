package model;

public class User {

    private String name;
    private String gender;
    private int age;


    public User(String name, String gender, int age) {

        this.name = name;
        this.gender = gender;
        this.age = age;

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


    public void displayUser() {

        System.out.println("Name : " + name);
        System.out.println("Gender : " + gender);
        System.out.println("Age : " + age);

    }

}