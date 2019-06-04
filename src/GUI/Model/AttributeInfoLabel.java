package GUI.Model;

import GUI.MyUtils.GUIUtils;
import javafx.scene.chart.XYChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class AttributeInfoLabel {
    public static int DIVIDENUM = 20;
    private String name;
    private long total;
    private long distinct;//值的可能个数
    private long unique = 0;//值只对应一个元组的个数，独一无二的值
    private long missing = 0;
    private Map<Double, AtomicLong> map = new TreeMap<>();
    private Map<Double, AtomicLong> divideMap = new TreeMap<>();
    private double max;
    private double min;
    private double middle;
    private double average;
    double sum = 0;
    private boolean isDiscrete;

    public AttributeInfoLabel(String name, double[] values) {
        this.name = name;
        this.total = values.length;
        ArrayList<Double> arrayList = new ArrayList<>(values.length);
        for (double value : values) {
            sum += value;
            arrayList.add(value);
            if (map.containsKey(value)) {
                map.get(value).incrementAndGet();
            } else {
                map.put(value, new AtomicLong(1));
            }
        }
        average = sum / total;
        distinct = map.keySet().size();
        for (AtomicLong tmp : map.values()) {
            if (tmp.longValue() == 1) {
                unique++;
            }
        }
        if (map.containsKey((double) -1)) {
            missing = map.get((double) -1).longValue();
            if (missing == 1) {
                unique--;
            }
        }
        Collections.sort(arrayList);
        max = arrayList.get(arrayList.size() - 1);
        if (arrayList.get(0) == -1) {
            min = arrayList.get(1);
        } else {
            min = arrayList.get(0);
        }
        if (arrayList.size() % 2 == 0) {
            middle = (arrayList.get(arrayList.size() / 2) + arrayList.get(arrayList.size() / 2 + 1)) / 2;
        } else {
            middle = arrayList.get(arrayList.size() / 2 + 1);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getDistinct() {
        return distinct;
    }

    public void setDistinct(long distinct) {
        this.distinct = distinct;
    }

    public long getUnique() {
        return unique;
    }

    public void setUnique(long unique) {
        this.unique = unique;
    }

    public long getMissing() {
        return missing;
    }

    public void setMissing(long missing) {
        this.missing = missing;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public Map<Double, AtomicLong> getMap() {
        return map;
    }

    public String getMissingString() {
        double rate = missing / (double) total * 100;
        return missing + "(" + new BigDecimal(rate).setScale(5, BigDecimal.ROUND_HALF_UP) + "%)";
    }

    public String getDistinctString() {
        double rate = distinct / (double) total * 100;
        return String.valueOf(distinct);
    }

    public String getUniqueString() {
        double rate = unique / (double) total;
        return unique + "(" + new BigDecimal(rate).setScale(5, BigDecimal.ROUND_HALF_UP) + "%)";
    }

    public void divideData() {
        divideData(DIVIDENUM);
    }

    public void divideData(int num) {
        if (num * 2 < distinct) {
            if (num / 2 == 1)
                num++;

            double[] divide = new double[num];
            for (int i = 0; i < num; i++) {
                divide[i] = (max - min) / num * i + min;
            }
           /* double left = average - min;
            double right = max - average;
            for (int i = 0; i < num; i++) {
                if (i < num/2) {
                    divide[i] = i * left / num+min;
                } else {
                    divide[i] = i * right / num+min;
                }
            }*/
            for (int i = 0; i < num; i++) {
                divideMap.put(divide[i], new AtomicLong(0));
            }
            for (Map.Entry<Double, AtomicLong> tmp : map.entrySet()) {
                boolean flag = false;
                for (int i = 0; i < num - 1; i++) {
                    //是否在前num-1组
                    if (tmp.getKey() >= divide[i] && tmp.getKey() < divide[i + 1]) {
                        divideMap.get(divide[i]).addAndGet(tmp.getValue().longValue());
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    divideMap.get(divide[num - 1]).addAndGet(tmp.getValue().longValue());
                }
            }
        } else {
            divideMap = map;
        }


    }

    public XYChart.Series<String, Long> getSeries() {
        XYChart.Series<String, Long> series = new XYChart.Series<>();
        divideData();
        for (Map.Entry<Double, AtomicLong> tmp : divideMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(GUIUtils.df2.format(tmp.getKey()), tmp.getValue().longValue()));
        }
        return series;
    }


    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMiddle() {
        return middle;
    }

    public void setMiddle(double middle) {
        this.middle = middle;
    }

    public Map<Double, AtomicLong> getDivideMap() {
        return divideMap;
    }

}
