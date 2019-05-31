package GUI.MyUtils;

import Classifiers.RandomForest.AttributeWeight;
import Classifiers.RandomForest.RandomForest;
import GUI.Model.DataBean;
import javafx.embed.swing.SwingNode;
import javafx.scene.image.Image;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.TreeSet;

public class JFCUtils {

    public static SwingNode getSwingNode(JFreeChart chart) {
        SwingNode node=new SwingNode();
        ChartPanel c=new ChartPanel(chart);
        node.setContent(c);
        return node;
    }

    public static ChartViewer getChartView(JFreeChart chart) {
        ChartViewer chartViewer = new ChartViewer(chart);
        return chartViewer;
    }

    public static Image getImage(JFreeChart chart){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsJPEG(out,chart,800,600);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream in=new ByteArrayInputStream(out.toByteArray());
        Image image = new Image(in);
        return image;
    }

    public static JFreeChart creatChart(DataBean dataBean, RandomForest rf){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        TreeSet<AttributeWeight> treeSet = rf.getTopAttribute(dataBean.getHeaders());
        for (int i = 0; i < 25; i++) {
            AttributeWeight tmp = treeSet.pollFirst();
            dataset.addValue(tmp.getWeight(), "综合评分", tmp.getName());
        }
        JFreeChart chart = ChartFactory.createBarChart("参数分析","属性",null,dataset);
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        chart.getLegend().setFrame(BlockBorder.NONE);

        TextTitle textTitle = chart.getTitle();
        textTitle.setFont(new Font("黑体", Font.PLAIN, 20));
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        numberAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        numberAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        return chart;
    }

}
