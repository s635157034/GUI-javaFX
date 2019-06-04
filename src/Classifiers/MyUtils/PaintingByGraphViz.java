package Classifiers.MyUtils;

import GUI.MyUtils.GUIUtils;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class PaintingByGraphViz {
    public static int threadNum = 10;
    public static boolean flag = false;
    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void createDotGraph(String dotFormat, String fileName, String dotName) {
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());
        gv.add(dotFormat);
        gv.addln(gv.end_graph());
        String type = "jpg";  //输出图文件的格式，以.jpg为例
        gv.increaseDpi();
        File out = new File(fileName + "." + type);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), dotName, type), out);
    }

    public static void getTreePicture(String dotFormat) {
        createDotGraph(dotFormat, "image/classifer", "dot/classifer");
    }

    public static void getTreePicture(String[] dotFormat) {
        File file = new File(GUIUtils.getAboslutePath() + "/image");
        File file1 = new File(GUIUtils.getAboslutePath() + "/dot");
        File file2 = new File(GUIUtils.getAboslutePath() + "/tmpDir");
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
        if (!file1.exists() && !file1.isDirectory()) {
            file1.mkdir();
        }
        if (!file2.exists() && !file2.isDirectory()) {
            file2.mkdir();
        }

        int size = dotFormat.length / threadNum;
        if (size == 0) {
            for (int j = 0; j < dotFormat.length; j++) {
                createDotGraph(dotFormat[j], "image/classifer-" + j, "dot/classifer-" + j);
            }
        } else {
            int lastSize = 0;
            for (int i = 0; i < threadNum; i++) {
                if (i == 9) {
                    lastSize += dotFormat.length % threadNum;
                }
                int finalSize = size;
                int finalI = i;
                int finalLastSize = lastSize;
                new Thread(() -> {
                    for (int j = 0; j < finalSize; j++) {
                        int tmp = finalI * finalSize + j + finalLastSize;
                        createDotGraph(dotFormat[tmp], "image/classifer-" + tmp, "dot/classifer-" + tmp);
                    }
                }).start();
            }
        }
    }

    public static void getTreePicture(String[] dotFormat, ProgressBar bar, int total) throws InterruptedException {
        flag = false;
        File file = new File(GUIUtils.getAboslutePath() + "/image");
        File file1 = new File(GUIUtils.getAboslutePath() + "/dot");
        File file2 = new File(GUIUtils.getAboslutePath() + "/tmpDir");
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
        if (!file1.exists() && !file1.isDirectory()) {
            file1.mkdir();
        }
        if (!file2.exists() && !file2.isDirectory()) {
            file2.mkdir();
        }
        atomicInteger.set(0);
        int size = dotFormat.length / threadNum;
        if (size == 0) {
            for (int j = 0; j < dotFormat.length; j++) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                createDotGraph(dotFormat[j], "image/classifer-" + j, "dot/classifer-" + j);
                bar.setProgress(atomicInteger.incrementAndGet() / (double) total);
            }
        } else {
            int lastSize = 0;
            for (int i = 0; i < threadNum; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                if (i == 9) {
                    lastSize += dotFormat.length % threadNum;
                }
                int finalSize = size;
                int finalI = i;
                int finalLastSize = lastSize;
                new Thread(() -> {
                    try {
                        for (int j = 0; j < finalSize; j++) {
                            if (flag) {
                                throw new InterruptedException();
                            }
                            int tmp = finalI * finalSize + j + finalLastSize;
                            createDotGraph(dotFormat[tmp], "image/classifer-" + tmp, "dot/classifer-" + tmp);
                            bar.setProgress(atomicInteger.incrementAndGet() / (double) total);
                        }
                    } catch (Exception e) {
                        System.out.println(Thread.currentThread().getName() + ":终止成功");
                    }
                }, "graph-" + i).start();
            }
        }
    }
}
