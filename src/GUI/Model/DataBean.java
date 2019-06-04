package GUI.Model;

public class DataBean {
    public final static double MISSINGVALUE = -1;
    private String[] headers;
    private double[][] input;
    private int[] inputClass;
    private int classId;

    public DataBean() {
    }

    public DataBean(String[] headers, double[][] input) {
        this.headers = headers;
        this.input = input;
    }

    public DataBean(String[] headers, double[][] input, int[] inputClass, int classId) {
        this.headers = headers;
        this.input = input;
        this.inputClass = inputClass;
        this.classId = classId;
    }

    public double[] getAttruibute(int id) {
        double[] tmp = new double[this.getInstanceNum()];
        for (int i = 0; i < getInstanceNum(); i++) {
            tmp[i] = input[i][id];
        }
        return tmp;
    }

    public int getAttruibuteNum() {
        return headers.length;
    }

    public int getInstanceNum() {
        return input.length;
    }

    public String getHeaders(int i) {
        return headers[i];
    }

    public double[] getInput(int i) {
        return input[i];
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public double[][] getInput() {
        return input;
    }

    public void setInput(double[][] input) {
        this.input = input;
    }

    public int[] getInputClass() {
        return inputClass;
    }

    public void setInputClass(int[] inputClass) {
        this.inputClass = inputClass;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
