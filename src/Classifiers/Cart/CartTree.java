package Classifiers.Cart;


import Classifiers.MyUtils.ClassifiersUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sun on 19-4-10.
 */
public class CartTree implements Serializable {
    public static final int continuous = 0;//连续值类别为0
    private double minGini = 0;
    private int maxDepth = -1;
    private double[][] data;
    private int[] dataClass;//连续值=0，离散值等于取值个数
    private int dataClassId;
    private List<CartTreeNode> cartTreeNodes = new ArrayList<>();

    private int[] excp;

    public CartTree() {
    }

    public CartTree(double[][] data, int[] dataClass, int dataClassId) {
        this.data = data;
        this.dataClass = dataClass;
        this.dataClassId = dataClassId;
    }

    public CartTree(double maxGini, double[][] data, int[] dataClass, int dataClassId) {
        this.minGini = maxGini;
        this.data = data;
        this.dataClass = dataClass;
        this.dataClassId = dataClassId;
    }

    public CartTree(int maxDepth, double[][] data, int[] dataClass, int dataClassId) {
        this.maxDepth = maxDepth;
        this.data = data;
        this.dataClass = dataClass;
        this.dataClassId = dataClassId;
    }

    public CartTree(double maxGini, int maxDepth, double[][] data, int[] dataClass, int dataClassId) {
        this.minGini = maxGini;
        this.maxDepth = maxDepth;
        this.data = data;
        this.dataClass = dataClass;
        this.dataClassId = dataClassId;
    }


    /**
     * @param start     起始位置
     * @param end       终止位置（包含）
     * @param exception 排除在外的属性序号组
     */
    public void buildCartTree(int start, int end, int[] exception) {
        excp = exception;
        CartTreeNode root = new CartTreeNodeRoot(start, end, Gini.calculateTotalGini(data[dataClassId], start, end));
        cartTreeNodes.add(root);
        int[] child = buildCartTree(start, end, exception, 1);
        if(child[0]==-1 ||child[1]==-1){
            root.setLeaf(true);
        }
        root.setLeftChild(child[0]);
        root.setRightChild(child[1]);
        buildClassifer();
    }

    private int[] buildCartTree(int start, int end, int[] exception, int depth) {
        int middle = -1;
        int[] childResult = new int[]{-1, -1};
        GiniResult result = Gini.findMinGiniAttribute(data, dataClass, start, end, dataClassId, exception);
        //属性已经分完
        if (result != null) {
            //如果最佳分裂的属性是连续值
            if (result.getAttriClass() == continuous) {
                middle = sortData(result.getAttriId(), start, end, result.getThreshold()[0]);
            } else {//如果是离散值
                middle = sortData(result.getAttriId(), start, end, result.getThreshold());
            }
            //没有分裂
            if (middle == -1) {
                //cartTreeNodes.get(cartTreeNodes.size() - 1).setLeaf(true);
                return new int[]{-1, -1};
            } else {
                CartTreeNode cartTreeNodeL = null;
                CartTreeNode cartTreeNodeR = null;
                //将已经分裂的属性加入到例外数组中
                int[] newException = new int[exception.length + 1];
                System.arraycopy(exception, 0, newException, 0, exception.length);
                newException[exception.length] = result.getAttriId();

                double leftGini = Gini.calculateTotalGini(data[dataClassId], start, middle);
                double rightGini = Gini.calculateTotalGini(data[dataClassId], middle + 1, end);
                //根据分类的值建立子树
                if (result.getAttriClass() == continuous) {
                    cartTreeNodeL = new CartTreeNodeLess(result.getAttriId(), result.getThreshold()[0], start, middle, depth, leftGini);
                    cartTreeNodeR = new CartTreeNodeMore(result.getAttriId(), result.getThreshold()[0], middle + 1, end, depth, rightGini);
                } else {
                    cartTreeNodeL = new CartTreeNodeDiscrete(result.getAttriId(), result.getThreshold(), start, middle, depth, leftGini);
                    cartTreeNodeR = new CartTreeNodeDiscrete(result.getAttriId(), ClassifiersUtils.getRemainingThreshold(dataClass[result.getAttriId()],
                            result.getThreshold()), middle + 1, end, depth, rightGini);
                }
                //将子树添加到数组里，并且获取左右子树的位置
                cartTreeNodes.add(cartTreeNodeL);
                cartTreeNodes.add(cartTreeNodeR);
                childResult[0] = cartTreeNodes.indexOf(cartTreeNodeL);
                childResult[1] = cartTreeNodes.indexOf(cartTreeNodeR);
                //到达最大深度时，该节点设置为叶子节点，否则继续分裂。
                if ((maxDepth == -1 || depth < maxDepth)) {
                    if (leftGini > minGini) {
                        int[] child = buildCartTree(start, middle, newException, depth + 1);
                        if (child[0] == -1 & child[1] == -1) {
                            cartTreeNodeL.setLeaf(true);
                        } else {
                            cartTreeNodeL.setLeftChild(child[0]);
                            cartTreeNodeL.setRightChild(child[1]);
                        }
                    } else {
                        cartTreeNodeL.setLeaf(true);
                    }
                    if (rightGini > minGini) {
                        int[] child = buildCartTree(middle + 1, end, newException, depth + 1);
                        if (child[0] == -1 & child[1] == -1) {
                            cartTreeNodeR.setLeaf(true);
                        } else {
                            cartTreeNodeR.setLeftChild(child[0]);
                            cartTreeNodeR.setRightChild(child[1]);
                        }
                    } else {
                        cartTreeNodeR.setLeaf(true);
                    }
                } else {
                    cartTreeNodeL.setLeaf(true);
                    cartTreeNodeR.setLeaf(true);
                }
            }
        } else {
            //cartTreeNodes.get(cartTreeNodes.size() - 1).setLeaf(true);
            return new int[]{-1, -1};
        }
        return childResult;
    }

    public void buildClassifer() {
        Iterator<CartTreeNode> it = cartTreeNodes.iterator();
        CartTreeNode tmp;
        while (it.hasNext()) {
            tmp = it.next();
            if (tmp.isLeaf()) {
                int t = 0, f = 0;
                for (int i = tmp.getStart(); i <= tmp.getEnd(); i++) {
                    if (data[dataClassId][i] == 1) {
                        t++;
                    } else {
                        f++;
                    }
                }
                if (t >= f) {
                    tmp.setNodeClass(1);
                } else {
                    tmp.setNodeClass(0);
                }
            }
        }
    }


    public int verify(double[] data) {
        //根节点，至多遍历到2个子树，超过则某属性为-1（空）
        int i = 0;
        CartTreeNode node;
        while (true) {
           node=cartTreeNodes.get(i);
            //是否符合该节点的阈值
            if (node.isContain(data)) {
                //如果是叶子节点直接返回结果
                if (node.isLeaf()) {
                    return node.getNodeClass();
                }
                //是否在左子树上
                CartTreeNode tmp = cartTreeNodes.get(node.getLeftChild());
                boolean flag=tmp.isContain(data);
                if (flag) {
                    i = node.getLeftChild();
                } else {
                    i = node.getRightChild();
                }
            } else {
                break;
            }
        }
        return -1;
    }

    public int[] verify(double[][] data) {
        int[] results = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            results[i] = verify(data[i]);
        }
        return results;
    }


    public void pruning(){
        for (CartTreeNode node : cartTreeNodes) {

        }
    }

    /**
     * @param attrId    分裂属性的id
     * @param start     数组的起始位置
     * @param end       数组的终止位置
     * @param threshold 阈值
     * @return 返回分界点
     */
    public int sortData(int attrId, int start, int end, double threshold) {
        int left = start, right = end;
        int countL = 0, countR = 0;
        while (left < right) {
            while (data[attrId][left] <= threshold && left < right) {
                left++;
                countL++;
            }
            while (data[attrId][right] > threshold && left < right) {
                right--;
                countR++;
            }
            if (left < right) {
                ClassifiersUtils.swapArray(data, left, right);
            }
        }
        if (data[attrId][left] <= threshold) {
            countL++;
        } else {
            countR++;
        }
        //分成两部分时
        if (countL > 0 && countR > 0)
            return left - 1;
        else//只有一部分，说明到根节点不会在继续分类
            return -1;
    }

    /**
     * @param attrId    分裂属性的id
     * @param start     数组的起始位置
     * @param end       数组的终止位置
     * @param threshold 阈值
     * @return 返回分界点
     */
    public int sortData(int attrId, int start, int end, double[] threshold) {
        int left = start, right = end;
        int countL = 0, countR = 0;
        while (left < right) {
            while (ClassifiersUtils.isContain(data[attrId][left], threshold) && left < right) {
                left++;
                countL++;
            }
            while (!ClassifiersUtils.isContain(data[attrId][right], threshold) && left < right) {
                right--;
                countR++;
            }
            if (left < right) {
                ClassifiersUtils.swapArray(data, left, right);
            }
        }
        if (ClassifiersUtils.isContain(data[attrId][left], threshold)) {
            countL++;
        } else {
            countR++;
        }
        //分成两部分时
        if (countL > 0 && countR > 0)
            return left - 1;
        else//只有一部分，说明到根节点不会在继续分类
            return -1;
    }


    public String printCartTree(){
        StringBuffer nodesInfo = new StringBuffer();
        StringBuffer nodesConnection = new StringBuffer();
        nodesInfo.append("node [shape = box];");
        for (int i = 0; i < cartTreeNodes.size(); i++) {
            CartTreeNode tmp = cartTreeNodes.get(i);
            nodesInfo.append("CART" + i + " [ label =\"" + tmp.printNode() + "\"]\n");
            if (tmp.isLeaf() == false) {
                nodesConnection.append("CART" + i + "-> CART" + tmp.getLeftChild()+";\n");
                nodesConnection.append("CART" + i + "-> CART" + tmp.getRightChild()+";\n");
            }
        }
        return nodesInfo.append("\n\n").append(nodesConnection).toString();
    }


    public static int getContinuous() {
        return continuous;
    }

    public double getMinGini() {
        return minGini;
    }

    public void setMinGini(double minGini) {
        this.minGini = minGini;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public int[] getDataClass() {
        return dataClass;
    }

    public void setDataClass(int[] dataClass) {
        this.dataClass = dataClass;
    }

    public int getDataClassId() {
        return dataClassId;
    }

    public void setDataClassId(int dataClassId) {
        this.dataClassId = dataClassId;
    }

    public List<CartTreeNode> getCartTreeNodes() {
        return cartTreeNodes;
    }


}
