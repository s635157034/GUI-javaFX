package Classifiers.RandomForest;


import Classifiers.Cart.CartTree;
import Classifiers.Cart.CartTreeNode;
import Classifiers.MyUtils.ClassifiersUtils;
import javafx.scene.control.ProgressBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomForest implements Serializable {
    public final static int SQRTN = -1;
    public final static int LOGN = -2;
    private int treeNum = 100;//树的个数
    private int treeDepth = 5;//单个树的最大深度
    private double minGini = 0;//小于该基尼系数时停止分类
    private double[][] input;//输入数据
    private int[] inputClass;//输入数据的每个属性类别，连续值为0，离散值为值的个数。
    private int[] exception;//不进行分类的属性
    private int inputClassId;//数据类别所在的属性号
    private double inputScale = -1;//测试数据占得比例
    private int attributeScale = -2;//属性的比例
    private long randomSeed = 1;
    private List<CartTree> cartTrees = new ArrayList<>();
    private boolean train = false;

    public RandomForest(double[][] input, int[] inputClass, int[] exception, int inputClassId) {
        this.input = input;
        this.inputClass = inputClass;
        this.exception = exception;
        this.inputClassId = inputClassId;
    }

    public RandomForest(double[][] input, int[] inputClass, int[] exception, int inputClassId, int attributeScale) {
        this.input = input;
        this.inputClass = inputClass;
        this.exception = exception;
        this.inputClassId = inputClassId;
        this.attributeScale = attributeScale;
    }

    public RandomForest(double[][] input, int[] inputClass, int[] exception, int inputClassId, double inputScale, int attributeScale) {
        this.input = input;
        this.inputClass = inputClass;
        this.exception = exception;
        this.inputClassId = inputClassId;
        this.inputScale = inputScale;
        this.attributeScale = attributeScale;
    }

    public RandomForest(int treeNum, int treeDepth, double minGini, double[][] input, int[] inputClass, int[] exception, int inputClassId, double inputScale, int attributeScale, long randomSeed) {
        this.treeNum = treeNum;
        this.treeDepth = treeDepth;
        this.minGini = minGini;
        this.input = input;
        this.inputClass = inputClass;
        this.exception = exception;
        this.inputClassId = inputClassId;
        this.inputScale = inputScale;
        this.attributeScale = attributeScale;
        this.randomSeed = randomSeed;
    }

    public AtomicInteger count;

    public void build() {
        train = false;
        Random random = new Random(randomSeed);
        Bootstrap.static_random = random;
        for (int i = 0; i < treeNum; i++) {
            buildForest();
        }
        train = true;
    }

    public void build(ProgressBar B_progressBar) throws InterruptedException {
        train = false;
        Random random = new Random(randomSeed);
        Bootstrap.static_random = random;
        for (int i = 0; i < treeNum ; i++) {
            if(Thread.currentThread().isInterrupted()){
                throw new InterruptedException();
            }
            buildForest();
            B_progressBar.setProgress(i / (double) treeNum);
        }
        B_progressBar.setProgress(1);
        train = true;
    }

    public int getAttributeNum() {
        switch (attributeScale) {
            case SQRTN:
                return (int) Math.ceil(Math.sqrt(inputClass.length - exception.length));
            case LOGN:
                //属性值为1的时候也能=1，如果使用ceil则等于0
                return (int) Math.floor(Math.log(inputClass.length - exception.length)/Math.log(2) + 1);
            default:
                return attributeScale;
        }
    }

    public int verify(double[] data) {
        int[] result = new int[treeNum];
        for (int i = 0; i < treeNum; i++) {
            result[i] = cartTrees.get(i).verify(data);
        }
        return ClassifiersUtils.getMostResult(result);
    }
    public double verifyRate(double[] data) {
        int[] result = new int[treeNum];
        for (int i = 0; i < treeNum; i++) {
            result[i] = cartTrees.get(i).verify(data);
        }
        return ClassifiersUtils.getRateResult(result);
    }

    public int[] verify(double[][] data) {
        int[] tmpResult = new int[treeNum];
        int[] result = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < treeNum; j++) {
                tmpResult[j] = cartTrees.get(j).verify(data[i]);
            }
            result[i] = ClassifiersUtils.getMostResult(tmpResult);
        }
        return result;
    }
    public double[] verifyRate(double[][] data) {
        int[] tmpResult = new int[treeNum];
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < treeNum; j++) {
                tmpResult[j] = cartTrees.get(j).verify(data[i]);
            }
            result[i] = ClassifiersUtils.getRateResult(tmpResult);
        }
        return result;
    }

    public String[] printRandomForest(){
        String[] strs = new String[treeNum];
        for (int i = 0; i < treeNum; i++) {
            strs[i] = cartTrees.get(i).printCartTree();
        }
        return strs;
    }
    public String printRandomForest(int i){
        String str = cartTrees.get(i).printCartTree();
        return str;
    }

    public TreeSet<AttributeWeight> getTopAttribute(String[] headers){
        double[] weights = calculateAttributeWeight();
        TreeSet<AttributeWeight> attributeWeights = new TreeSet<>();
        for (int i = 0; i < weights.length; i++) {
            if(i==inputClassId)
                continue;
            attributeWeights.add(new AttributeWeight(headers[i],i, weights[i]));
        }
        return attributeWeights;
    }

    public TreeSet<AttributeWeight> getTopAttribute(){
        String[] strings = new String[inputClass.length];
        return getTopAttribute(strings);
    }

    public double[] calculateAttributeWeight(){
        double[] attriWeight = new double[inputClass.length];
        for (CartTree cartTree:cartTrees) {
            for (CartTreeNode cartTreeNode : cartTree.getCartTreeNodes()) {
                if(!cartTreeNode.isLeaf()){
                    CartTreeNode left,right;
                    left = cartTree.getCartTreeNodes().get(cartTreeNode.getLeftChild());
                    right = cartTree.getCartTreeNodes().get(cartTreeNode.getRightChild());
                    int attributeId = left.getAttriId();
                    double size = getSize(cartTreeNode);//用double避免下面计算需要转成浮点型
                    double decreaseGini = cartTreeNode.getGini() - (left.getGini()*(getSize(left)/size) + right.getGini()*(getSize(right)/size));
                    attriWeight[attributeId] += decreaseGini;
                }
            }
        }
        double sum=0;
        for (int i = 0; i < attriWeight.length; i++) {
            sum += attriWeight[i];
        }
        for (int i = 0; i < attriWeight.length; i++) {
            attriWeight[i] /= sum;
        }
        return attriWeight;
    }

    private int getSize(CartTreeNode cartTreeNode){
        return cartTreeNode.getEnd() - cartTreeNode.getStart();
    }



    public long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public double getInputScale() {
        return inputScale;
    }

    public void setInputScale(double inputScale) {
        this.inputScale = inputScale;
    }

    public int getAttributeScale() {
        return attributeScale;
    }

    public void setAttributeScale(int attributeScale) {
        this.attributeScale = attributeScale;
    }

    public int getTreeNum() {
        return treeNum;
    }

    public void setTreeNum(int treeNum) {
        this.treeNum = treeNum;
    }

    public int getTreeDepth() {
        return treeDepth;
    }

    public void setTreeDepth(int treeDepth) {
        this.treeDepth = treeDepth;
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

    public int[] getExceptions() {
        return exception;
    }

    public void setExceptions(int[] exception) {
        this.exception = exception;
    }

    public int getInputClassId() {
        return inputClassId;
    }

    public void setInputClassId(int inputClassId) {
        this.inputClassId = inputClassId;
    }

    public double getMinGini() {
        return minGini;
    }

    public void setMinGini(double minGini) {
        this.minGini = minGini;
    }

    public List<CartTree> getCartTrees() {
        return cartTrees;
    }

    public void setCartTrees(List<CartTree> cartTrees) {
        this.cartTrees = cartTrees;
    }

    public boolean isTrain() {
        return train;
    }

    public void setTrain(boolean train) {
        this.train = train;
    }

    private void buildForest(){
        DataSet data;
        if (inputScale <= 0) {
            data = Bootstrap.bootStrap(input);
        } else {
            int dataSize = (int) Math.ceil(input.length * inputScale);
            data = Bootstrap.bootStrap(input, dataSize);
        }
        int[] dataException = Bootstrap.randomAttributtes(inputClass.length, getAttributeNum(), exception);
        CartTree cartTree = new CartTree(minGini, treeDepth, data.trainData, inputClass, inputClassId);
        cartTree.buildCartTree(0, data.trainData[0].length - 1, dataException);
        cartTrees.add(cartTree);
    }
}
