package Classifiers.RandomForest;

public class DataSet {
    public double[][] trainData;
    public double[][] oobData;

    public DataSet(double[][] trainData, double[][] oobData) {
        this.trainData = trainData;
        this.oobData = oobData;
    }
}