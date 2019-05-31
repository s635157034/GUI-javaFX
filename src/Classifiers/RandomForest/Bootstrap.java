package Classifiers.RandomForest;

import Classifiers.MyUtils.ClassifiersUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by sun on 19-4-17.
 */
public class Bootstrap {
    public static Random static_random=new Random(System.nanoTime());

    public static DataSet bootStrap(double[][] data) {
        DataSet dataSet = chooseData(data, bagging(data.length));
        dataSet.trainData = ClassifiersUtils.flip(dataSet.trainData);
        return dataSet;
    }

    public static DataSet bootStrap(double[][] data, int dataSize) {
        DataSet dataSet = chooseData(data, bagging(data.length,dataSize));
        dataSet.trainData = ClassifiersUtils.flip(dataSet.trainData);
        return dataSet;
    }


    public static int[] bagging(int input, int size) {
        Random random = new Random(static_random.nextLong());
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < size; ) {
            if (result.add(random.nextInt(input))) {
                i++;
            }
        }
        return ClassifiersUtils.getIntArrary(result);
    }

    public static int[] bagging(int input) {
        Random random = new Random(static_random.nextLong());
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < input; i++) {
            result.add(random.nextInt(input));
        }
        return ClassifiersUtils.getIntArrary(result);
    }


    /**
     * @param attriNum
     * @param size
     * @param exception
     * @return 返回例外属性
     */
    public static int[] randomAttributtes(int attriNum, int size, int[] exception) {
        HashSet<Integer> set = new HashSet<>();
        if (attriNum > (size + exception.length)) {
            Random random = new Random(static_random.nextLong());
            while (set.size() < size) {
                int tmp = random.nextInt(attriNum);
                if (ClassifiersUtils.isContain(tmp, exception)) {
                    continue;
                } else {
                    set.add(tmp);
                }
            }
            HashSet<Integer> tmp = new HashSet<>();
            for (int i = 0; i < attriNum; i++) {
                tmp.add(i);
            }
            Iterator<Integer> it = set.iterator();
            while (it.hasNext()) {
                tmp.remove(it.next());
            }
            return ClassifiersUtils.getIntArrary(tmp);
            //属性不足时，使用全部属性
        } else {
            return exception;
        }
    }

    public static DataSet chooseData(double[][] data, int[] nums) {
        double[][] trainData = new double[nums.length][];
        double[][] oobData = new double[data.length-nums.length][];
        int a = 0,b=0;
        for (int i = 0; i < data.length; i++) {
            if(ClassifiersUtils.isContain(i,nums)){
                trainData[a] = data[i];
                a++;
            }else {
                oobData[b] = data[i];
                b++;
            }
        }
        return new DataSet(trainData,oobData);
    }



}
