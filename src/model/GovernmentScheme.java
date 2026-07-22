package model;

public class GovernmentScheme extends Opportunity {

    private String department;
    private String benefit;
    private double maxIncome;
    private String category;

    public GovernmentScheme(String name, String provider,
                            String department, String benefit,
                            double maxIncome, String category) {

        this.name = name;
        this.provider = provider;
        this.department = department;
        this.benefit = benefit;
        this.maxIncome = maxIncome;
        this.category = category;
    }

    public String getDepartment() {
        return department;
    }

    public String getBenefit() {
        return benefit;
    }

    public double getMaxIncome() {
        return maxIncome;
    }

    public String getCategory() {
        return category;
    }
}

