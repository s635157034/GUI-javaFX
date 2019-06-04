package Classifiers.MyUtils;

/**
 * Created by sun on 19-4-10.
 */

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class ProperSubsetCombination {

    private static Integer[] array;
    private static BitSet startBitSet; // 比特集合起始状态
    private static BitSet endBitSet; // 比特集合终止状态，用来控制循环
    private static Set<Set<Integer>> properSubset; // 真子集集合

    /**
     * 利用二进制方法，集合的个数和二进制位数相同，集合的二划分就可以用数字代替，数字转为byte后1为选取，0为不选
     *
     * @param itemSet 输入的集合
     * @return 集合的二划分的其中一个
     */
    public static Set<Set<Integer>> getProperSubset(Set<Integer> itemSet) {
        array = itemSet.toArray(new Integer[itemSet.size()]);
        Set<Set<Integer>> set = new HashSet<>();
        int len = array.length - 1;
        //n项数据，只需要返回一半（真子集）二划分所以从是2的n-2次方,由于只返回一半因此最高位永远是0，忽略不计顾用len-1
        for (int i = 1; i < 1 << len; i++) {
            set.add(intToSet(len, i));
        }
        return set;
    }

    /*   public static Set<Integer> intToSet(int n,int num){
            Set<Integer> set = new HashSet<>();
            for(int i=0;i<n;i++){
                if((num>>i)<<31!=0){
                    set.add(array[i]);
                }
            }
            return set;
        }*/
    public static Set<Integer> intToSet(int n, int num) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (num % 2 != 0) {
                set.add(array[i]);
            }
            num /= 2;
        }
        return set;
    }
}