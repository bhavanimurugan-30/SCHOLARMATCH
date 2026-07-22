package model;

public class Scholarship extends Opportunity {

    private double amount;
    private double minCgpa;
    private double maxIncome;
    private String category;

    public Scholarship(String name, String provider,
                       double amount, double minCgpa,
                       double maxIncome, String category) {

        this.name = name;
        this.provider = provider;
        this.amount = amount;
        this.minCgpa = minCgpa;
        this.maxIncome = maxIncome;
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public double getMinCgpa() {
        return minCgpa;
    }

    public double getMaxIncome() {
        return maxIncome;
    }

    public String getCategory() {
        return category;
    }
}
