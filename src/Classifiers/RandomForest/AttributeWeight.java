package Classifiers.RandomForest;

public class AttributeWeight implements Comparable<AttributeWeight> {
    private String name;
    private int id;
    private double weight;

    public AttributeWeight(int id, double weight) {
        this.id = id;
        this.weight = weight;
    }

    public AttributeWeight(String name, int id, double weight) {
        this.name = name;
        this.id = id;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(AttributeWeight o) {
        //权重大的靠前
        if (this.weight > o.getWeight()) {
            return -1;
        } else if (this.weight < o.getWeight()) {
            return 11;
        } else {
            return 0;
        }
    }
}
