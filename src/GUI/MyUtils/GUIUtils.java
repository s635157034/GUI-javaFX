package GUI.MyUtils;

import Classifiers.RandomForest.RandomForest;
import GUI.Model.DataBean;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class GUIUtils {
    public static DecimalFormat df2 = new DecimalFormat("#.##");
    public static DecimalFormat df6 = new DecimalFormat("#.######");
    public static File file = new File("");
    public static String getAboslutePath(){
        return file.getAbsolutePath();
    }

    public static String getPromptText(){
        return "分类器数量：随机森林训练次数，产生指定数量的分类器。\n" +
                "\n" +
                "最大深度：定义每个分类器的最大深度，-1为不限定。\n" +
                "\n" +
                "最小基尼系数：训练过程中基尼系数小于最小值即停止该节点的分裂。\n" +
                "\n" +
                "训练数据比例：-1为.632自助法，可以自定义每次训练使用多少数据\n" +
                "\n" +
                "属性个数：每次抽样时选取的属性数量，-1为√，-2为log2N。\n" +
                "\n" +
                "随机因子：改变随机因子来控制整个过程的随机数，相同随机因子的结果相同。\n";
    }


    public static DataBean getDataFromFile(String path) throws Exception {
        return getDataFromFile(path, true);
    }

    public static DataBean getDataFromFile(String path, boolean isHeader) throws Exception {
        BufferedReader br;
        String[] headers;
        ArrayList<String[]> list = new ArrayList<>(1000);
        br = new BufferedReader(new FileReader(path));
        if (isHeader) {
            headers = br.readLine().split(",");
        } else {
            headers = null;
        }
        String line = null;
        while ((line = br.readLine()) != null) {
            list.add(line.split(","));
        }
        int row = list.size();
        int col = list.get(0).length;
        double[][] input = new double[row][col];
        for (int i = 0; i < row; i++) {
            String[] strs = list.get(i);
            for (int j = 0; j < col; j++) {
                //空值时将值设为-1
                if (strs[j].equals("")) {
                    input[i][j] = -1;
                } else {
                    input[i][j] = Double.valueOf(strs[j]);
                }
            }
        }
        br.close();
        return new DataBean(headers, input);
    }


    public static boolean writePredictToCSV(String source,int[] predicts) throws Exception{
        return writePredictToCSV(source, predicts, true);
    }

    public static boolean writePredictToCSV(String source,int[] predicts,boolean isHeader) throws Exception {
        BufferedReader br=new BufferedReader(new FileReader(source));
        PrintWriter pw=new PrintWriter(source.substring(0,source.lastIndexOf(".csv"))+"-预测后.csv");
        String line;
        if (isHeader) {
            pw.println(br.readLine() + ",Predict");
        }
        int i=0;
        while ((line = br.readLine()) != null) {
           pw.println(line+","+predicts[i]);
           i++;
        }
        br.close();
        pw.flush();
        pw.close();
        return false;
    }

    public static boolean writeClassiferObject(String path, RandomForest randomForest) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(randomForest);
            out.close();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static RandomForest readClassiferObject(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            RandomForest rf = (RandomForest) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return rf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean writeClassiferAsText(String path, RandomForest randomForest) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            // TODO: 2019-05-13 实现文本保存随机森林模型
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
