package Classifiers.Cart;


import Classifiers.MyUtils.ClassifiersUtils;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by sun on 19-4-10.
 */
public abstract class CartTreeNode implements Comparable<CartTreeNode> {
    private int attriId;//节点的序号
    private boolean isLeaf = false;//节点是否为叶节点
    private int nodeClass = -1;//节点的分类
    private String threshold;//节点的阈值，用字符串存储
    private int start;//节点的起始位置
    private int end;//终止位置
    private int leftChild = -1;
    private int rightChild = -1;
    private int depth;
    private double gini;

    public CartTreeNode(int attriId, String threshold, int start, int end, int depth, double gini) {
        this.attriId = attriId;
        this.threshold = threshold;
        this.start = start;
        this.end = end;
        this.depth = depth;
        this.gini = gini;
    }
    //输出为node格式
    public String printNode() {
        String newline = "\\n";
        DecimalFormat decimalFormat = new DecimalFormat("#.######");
        StringBuffer label = new StringBuffer();
        if(attriId!=-1){
            label.append("AttributeID:" + attriId + newline);
        }
        if (threshold != null) {
            label.append("Threshold" + threshold + newline);
        }
        label.append("Gini:" + decimalFormat.format(gini) + newline);
        if (isLeaf) {
            label.append("Nodeclass:" + nodeClass + newline);
        }
        return label.toString();
    }

    public int getAttriId() {
        return attriId;
    }

    public void setAttriId(int attriId) {
        this.attriId = attriId;
    }

    public int getNodeClass() {
        return nodeClass;
    }

    public void setNodeClass(int attriClass) {
        this.nodeClass = attriClass;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public double getGini() {
        return gini;
    }

    public void setGini(double gini) {
        this.gini = gini;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(int leftChild) {
        this.leftChild = leftChild;
    }

    public int getRightChild() {
        return rightChild;
    }

    public void setRightChild(int rightChild) {
        this.rightChild = rightChild;
    }

    public abstract boolean isContain(double[] a);

    @Override
    public int compareTo(CartTreeNode o) {
        if (depth < o.getDepth()) {
            return -1;
        } else if (depth > o.getDepth()) {
            return 1;
        } else {
            if (start < o.getStart()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}

class CartTreeNodeLess extends CartTreeNode {
    double thresholds;

    public CartTreeNodeLess(int attriId, double threshold, int start, int end, int depth, double gini) {
        super(attriId, "<=" + threshold, start, end, depth, gini);
        DecimalFormat decimalFormat = new DecimalFormat("#.######");
        this.setThreshold("<="+decimalFormat.format(threshold));
        thresholds=threshold;
    }

    @Override
    public boolean isContain(double[] a) {
        if (a[this.getAttriId()] >= 0 && a[this.getAttriId()] <= thresholds) {
            return true;
        } else {
            return false;
        }
    }
}

class CartTreeNodeMore extends CartTreeNode {
    double thresholds;

    public CartTreeNodeMore(int attriId, double threshold, int start, int end, int depth, double gini) {
        super(attriId, ">" + threshold, start, end, depth, gini);
        DecimalFormat decimalFormat = new DecimalFormat("#.######");
        this.setThreshold(">"+decimalFormat.format(threshold));
        thresholds=threshold;
    }

    @Override
    public boolean isContain(double[] a) {
        if (a[this.getAttriId()] > thresholds) {
            return true;
        } else {
            return false;
        }
    }
}

class CartTreeNodeDiscrete extends CartTreeNode {
    double[] thresholds;

    public CartTreeNodeDiscrete(int attriId, double[] threshold, int start, int end, int depth, double gini) {
        super(attriId, Arrays.toString(threshold), start, end, depth, gini);
        thresholds = threshold;
    }

    @Override
    public boolean isContain(double[] a) {
        return ClassifiersUtils.isContain(a[this.getAttriId()], thresholds);
    }
}

class CartTreeNodeRoot extends CartTreeNode {

    public CartTreeNodeRoot(int start, int end, double gini) {
        super(-1, null, start, end, 0, gini);
    }


    @Override
    public boolean isContain(double[] a) {
        return true;
    }
}