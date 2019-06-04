package GUI.Model;

import GUI.MyUtils.GUIUtils;

public class EMRItem {
    private String name;
    private String abbr;//缩写
    private String recommend;//推荐值
    private double lower;
    private double higher;
    private int id;

    public EMRItem(String name, String abbr, String recommend, int id) {
        this.name = name;
        this.abbr = abbr;
        this.recommend = recommend;
        this.id = id;
    }

    public EMRItem(String name, String abbr, double lower, double higher, int id) {
        this.name = name;
        this.abbr = abbr;
        this.id = id;
        this.lower = lower;
        this.higher = higher;
        if (lower != higher) {
            this.recommend = GUIUtils.df2.format(lower) + "-" + GUIUtils.df2.format(higher);
        } else if (lower == -1 && higher == -1) {
            this.recommend = null;
        } else {
            this.recommend = String.valueOf(lower);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLower() {
        return lower;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    public double getHigher() {
        return higher;
    }

    public void setHigher(double higher) {
        this.higher = higher;
    }

    public String compare(double a) {
        if (a >= lower && a <= higher) {
            return "";
        } else if (a > higher) {
            return "  ↑";
        } else {
            return "  ↓";
        }
    }

}
