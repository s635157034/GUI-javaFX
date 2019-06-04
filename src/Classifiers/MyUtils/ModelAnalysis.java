package Classifiers.MyUtils;

import javafx.scene.chart.XYChart;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class ModelAnalysis {
    private int[] realClass;
    private int[] predictClass;
    private double[] predictClassRate;
    private double Accurancy;
    private double Precision;
    private double Recall;
    private double F_measure;
    private double F2;
    private double F0_5;
    private double thresold = 0.5;
    private boolean isRate = true;
    private double[][] ROCData;
    private double AUC;

    public ModelAnalysis(int[] realClass, int[] predictClass) {
        this.realClass = realClass;
        this.predictClass = predictClass;
        isRate = false;
        analysis();
    }

    public ModelAnalysis(int[] realClass, double[] predictClassRate) {
        this.realClass = realClass;
        this.predictClassRate = predictClassRate;
        this.predictClass = new int[predictClassRate.length];
        analysis();
    }

    public ModelAnalysis(int[] realClass, double[] predictClassRate, double thresold) {
        this.realClass = realClass;
        this.predictClassRate = predictClassRate;
        this.predictClass = new int[predictClassRate.length];
        this.thresold = thresold;
        analysis();
    }

    public ModelAnalysis(double[] realClass, int[] preidictClass) {
        this.predictClass = preidictClass;
        this.realClass = new int[realClass.length];
        for (int i = 0; i < realClass.length; i++) {
            this.realClass[i] = (int) realClass[i];
            ;
        }
        isRate = false;
        analysis();
    }


    public ModelAnalysis(double[] realClass, double[] predictClassRate) {
        this.predictClassRate = predictClassRate;
        this.predictClass = new int[predictClassRate.length];
        this.realClass = new int[realClass.length];
        for (int i = 0; i < realClass.length; i++) {
            this.realClass[i] = (int) realClass[i];
            ;
        }
        analysis();
        analysisROC();
    }

    public ModelAnalysis(double[] realClass, double[] predictClassRate, double thresold) {
        this.predictClassRate = predictClassRate;
        this.predictClass = new int[predictClassRate.length];
        this.thresold = thresold;
        this.realClass = new int[realClass.length];
        for (int i = 0; i < realClass.length; i++) {
            this.realClass[i] = (int) realClass[i];
            ;
        }
        this.thresold = thresold;
        analysis();
        analysisROC();
    }

    private void analysis() {
        int TP = 0, TN = 0, FP = 0, FN = 0;
        int[] tmp = getMatrix();
        TP = tmp[0];
        FP = tmp[1];
        FN = tmp[2];
        TN = tmp[3];

        Accurancy = (TP + TN) / (double) (TP + TN + FP + FN);
        if (TP + FN == 0) {
            Recall = 0;
        } else {
            Recall = TP / (double) (TP + FN);
        }
        if (TP + FP == 0) {
            Precision = 0;
        } else {
            Precision = TP / (double) (TP + FP);
        }
        if (Precision + Recall == 0) {
            F_measure = 0;
            F2 = 0;
            F0_5 = 0;
        } else {
            F_measure = (2 * Precision * Recall) / (Precision + Recall);
            F2 = (5 * Precision * Recall) / (4 * Precision + Recall);
            F0_5 = (1.25 * Precision * Recall) / (0.25 * Precision + Recall);
        }
    }

    public int[] getMatrix() {
        int TP = 0, TN = 0, FP = 0, FN = 0;
        if (isRate) {
            for (int i = 0; i < predictClassRate.length; i++) {
                predictClass[i] = getIntWithThresold(predictClassRate[i], thresold);
            }
        }
        for (int i = 0; i < realClass.length; i++) {
            //positive
            if (realClass[i] == 1) {
                if (predictClass[i] == 1) {
                    TP++;
                } else {
                    FN++;
                }
            } else {//negative
                if (predictClass[i] == 1) {
                    FP++;
                } else {
                    TN++;
                }
            }
        }
        return new int[]{TP, FP, FN, TN};
    }

    public double findBestThresold(double gap) {
        double maxAccurancy = Accurancy;
        double maxThresold = thresold;
        double i = 1;
        while (i >= 0) {
            thresold = i;
            analysis();
            if (maxAccurancy < Accurancy) {
                maxAccurancy = Accurancy;
                maxThresold = thresold;
            }
            i -= gap;
        }
        return maxThresold;
    }

    public void changeAnalysis(String thresold) {
        this.thresold = Double.valueOf(thresold);
        analysis();
    }

    private void analysisROC() {
        int count = 0;
        Set<Double> predict = new TreeSet<>(Comparator.reverseOrder());
        for (double tmp : predictClassRate) {
            predict.add(tmp);
        }
        ROCData = new double[predict.size()][2];
        for (double tmp : predict) {
            int TP = 0, TN = 0, FP = 0, FN = 0;
            double TPR = 0, FPR = 0;
            int[] predicts = new int[realClass.length];
            for (int i = 0; i < predictClassRate.length; i++) {
                predicts[i] = getIntWithThresold(predictClassRate[i], tmp);
            }
            for (int i = 0; i < predictClassRate.length; i++) {
                //positive
                if (realClass[i] == 1) {
                    if (predicts[i] == 1) {
                        TP++;
                    } else {
                        FN++;
                    }
                } else {//negative
                    if (predicts[i] == 1) {
                        FP++;
                    } else {
                        TN++;
                    }
                }
            }
            if (TP + FN == 0) {
                TPR = 0;
            } else {
                TPR = (TP) / (double) (TP + FN);
            }
            if (FP + TN == 0) {
                FPR = 0;
            } else {
                FPR = (FP) / (double) (FP + TN);
            }
            ROCData[count][0] = FPR;
            ROCData[count][1] = TPR;
            count++;
        }
        calculateAUC();
    }

    private void calculateAUC() {
        double tmp = 0;
        tmp += (ROCData[0][0] * ROCData[0][1]) / 2;
        for (int i = 1; i < ROCData.length; i++) {
            tmp += (ROCData[i][0] - ROCData[i - 1][0]) * (ROCData[i][1] - ROCData[i - 1][1]) / 2 + (ROCData[i][0] - ROCData[i - 1][0]) * ROCData[i - 1][1];
        }
        tmp += (1 - ROCData[ROCData.length - 1][0]) * (1 - ROCData[ROCData.length - 1][1]) / 2 + (1 - ROCData[ROCData.length - 1][0]) * ROCData[ROCData.length - 1][1];
        AUC = tmp;
    }

    private int getIntWithThresold(double a, double thresold) {
        if (a >= thresold) {
            return 1;
        } else {
            return 0;
        }
    }

    public void changeAnalysis(double thresold) {
        this.thresold = thresold;
        analysis();
    }

    public XYChart.Series<String, Double> getSeries() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        for (int i = 0; i < ROCData.length; i++) {
            series.getData().add(new XYChart.Data<>(decimalFormat.format(ROCData[i][0]), ROCData[i][1]));
        }
        return series;
    }

    public double[][] getROCLine() {
        double[][] tmp = ClassifiersUtils.flip(ROCData);
        for (int i = 0; i < tmp[0].length; i++) {
            tmp[0][i] *= 200;
            tmp[1][i] = 200 * (1 - tmp[1][i]);
        }
        return tmp;
    }

    public double[][] getROCData() {
        return ROCData;
    }

    public void setROCData(double[][] ROCData) {
        this.ROCData = ROCData;
    }

    public double getAUC() {
        return AUC;
    }

    public void setAUC(double AUC) {
        this.AUC = AUC;
    }

    public int[] getRealClass() {
        return realClass;
    }

    public int[] getPredictClass() {
        return predictClass;
    }

    public double getAccurancy() {
        return Accurancy;
    }

    public void setAccurancy(double accurancy) {
        Accurancy = accurancy;
    }

    public double getPrecision() {
        return Precision;
    }

    public void setPrecision(double precision) {
        Precision = precision;
    }

    public double getRecall() {
        return Recall;
    }

    public void setRecall(double recall) {
        Recall = recall;
    }

    public double getF_measure() {
        return F_measure;
    }

    public void setF_measure(double f_measure) {
        F_measure = f_measure;
    }

    public double getF2() {
        return F2;
    }

    public void setF2(double f2) {
        F2 = f2;
    }

    public double getF0_5() {
        return F0_5;
    }

    public void setF0_5(double f0_5) {
        F0_5 = f0_5;
    }

    public void setRealClass(int[] realClass) {
        this.realClass = realClass;
    }

    public void setPredictClass(int[] predictClass) {
        this.predictClass = predictClass;
    }

    public double[] getPredictClassRate() {
        return predictClassRate;
    }

    public void setPredictClassRate(double[] predictClassRate) {
        this.predictClassRate = predictClassRate;
    }

    public double getThresold() {
        return thresold;
    }

    public void setThresold(double thresold) {
        this.thresold = thresold;
    }

    public boolean isRate() {
        return isRate;
    }

    public void setRate(boolean rate) {
        isRate = rate;
    }


}
