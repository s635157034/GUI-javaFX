package Classifiers;

import Classifiers.Cart.CartTree;
import Classifiers.MyUtils.ClassifiersUtils;
import Classifiers.MyUtils.ModelAnalysis;
import Classifiers.RandomForest.RandomForest;
import GUI.Model.DataBean;
import GUI.MyUtils.GUIUtils;

import java.util.Collections;

/**
 * Created by sun on 19-4-9.
 */
public class main {
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        //String inputPath="/media/work/毕业设计/Java/RandForest1.0/data/预处理后数据.csv";
        //String inputPath = "/media/work/毕业设计/Java/RandForest1.0/data/rffangchan.csv";
        String inputPath = "C:\\Users\\Mrsun\\Desktop\\train.csv";
        double[][] input = getDataFromCsv(inputPath);
        System.out.println("程序运行时间：" + (System.currentTimeMillis() - startTime) + "ms");
        int col = input[0].length;
        int[] inputClass = new int[col];
        //inputClass = ClassifiersUtils.getInputClass(input);
        RandomForest randomForest = new RandomForest(input, inputClass, new int[]{col - 1}, col - 1);
        randomForest.setTreeNum(100);
        randomForest.setTreeDepth(-1);
        randomForest.setAttributeScale(-1);
        randomForest.build();
        String testPath = "C:\\Users\\Mrsun\\Desktop\\test.csv";
        DataBean dataBean = GUIUtils.getDataFromFile(testPath);
        double[][] test = dataBean.getInput();
        int[] result = randomForest.verify(test);
        ModelAnalysis modelAnalysis = new ModelAnalysis(dataBean.getAttruibute(col - 1), randomForest.verifyRate(dataBean.getInput()));
        double accuracy = ClassifiersUtils.calculateAccuracy(test, col - 1, result);
        System.out.println(accuracy);
        System.out.println(modelAnalysis.getAccurancy());
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
        //PaintingByGraphViz.getTreePicture(randomForest.printRandomForest());
    }


    public static double[][] getDataFromCsv(String inputPath) throws Exception {
        return GUIUtils.getDataFromFile(inputPath).getInput();
    }

    public static void mainb(String[] args) {
        double[][] input = new double[5][];
        input[0] = new double[]{0, 1, 2, 3, 0};
        input[1] = new double[]{0, 1, 2, 3, 0};
        input[2] = new double[]{0, 1, 2, 3, 0};
        input[3] = new double[]{0, 1, 2, 3, 1};
        input[4] = new double[]{0, 1, 2, 3, 1};
        int[] inputClass = new int[]{0, 2, 0, 0, 2};
        input = ClassifiersUtils.flip(input);
        ClassifiersUtils.printArray(input);
//        System.out.println(Gini.calculateTotalGini(input[4],0,4));


        CartTree cartTree = new CartTree();
        cartTree.setData(input);
        cartTree.setDataClass(inputClass);
        cartTree.setDataClassId(4);
        int[] exception = new int[]{4};
        cartTree.buildCartTree(0, 4, exception);
        Collections.sort(cartTree.getCartTreeNodes());
        System.out.println(cartTree.toString());

/*        CartTree cartTree=new CartTree();
        cartTree.data=input;
        cartTree.dataClass=inputClass;
        utils.printArray(input);
        System.out.println();
        int a=cartTree.sortData(0,0,5,new double[]{0,1});
        utils.printArray(input);
        System.out.println(a);


        utils.flip(input);
        Gini gini=new Gini();
        GiniResult giniResult=gini.findMinGiniAttribute(input,inputClass,0,4,4,new int[0]);
        System.out.println(giniResult);


        utils.swapArray(input,2,3);
        System.out.println(Arrays.toString(input[0]));
        System.out.println(Arrays.toString(input[1]));
        System.out.println(Arrays.toString(input[2]));
        System.out.println(Arrays.toString(input[3]));
        System.out.println(Arrays.toString(input[4]));*/

    }

    public static void mainc(String[] args) {
        double[][] input = new double[4][];
        input[0] = new double[]{0, 1, 2, 3, 0};
        input[1] = new double[]{0, 1, 2, 3, 0};
        input[2] = new double[]{0, 1, 2, 3, 0};
        input[3] = new double[]{0, 1, 2, 3, 1};
        ClassifiersUtils.flip(input);
    }

}
