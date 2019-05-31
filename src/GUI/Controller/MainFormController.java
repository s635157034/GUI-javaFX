package GUI.Controller;

import Classifiers.MyUtils.ClassifiersUtils;
import Classifiers.MyUtils.ModelAnalysis;
import Classifiers.MyUtils.PaintingByGraphViz;
import Classifiers.RandomForest.RandomForest;
import GUI.Model.*;
import GUI.MyUtils.GUIUtils;
import GUI.MyUtils.JFCUtils;
import GUI.MyUtils.MysqlConnection;
import GUI.MyUtils.StageManager;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfree.chart.fx.ChartViewer;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;



public class MainFormController implements Initializable {
    private Stage stage ;
    private DataBean trainDataBean;
    private DataBean testDataBean;
    private DataBean classifyDataBean;
    private RandomForest randomForest;
    private ArrayList<AttributeInfoLabel> attributeInfoLabels;
    private Thread randomForestThread;
    private Map<Integer,String> types;
    private Map<String, EMRItem> itemMap;
    private ModelAnalysis modelAnalysis;
    private Map<Integer,TreeItem<PredictInfoTableItem>> typeItems = new HashMap<>();
    @FXML private VBox rootVBox;
    @FXML private Tab B_Tab;
    @FXML private Tab C_Tab;
    @FXML private Tab D_Tab;
    @FXML private TextField A_filePath;
    @FXML private Label A_fileInfo_attributeNum;
    @FXML private Label A_fileInfo_instanceNum;
    @FXML private TableView<AttributeTableViewItem> A_fileInfoTableView;
    @FXML private TableColumn<AttributeTableViewItem, Integer> A_fileInfoTableView_col1;
    @FXML private TableColumn<AttributeTableViewItem, CheckBox> A_fileInfoTableView_col2;
    @FXML private TableColumn<AttributeTableViewItem, String> A_fileInfoTableView_col3;
    @FXML private TableColumn<AttributeTableViewItem, CheckBox> A_fileInfoTableView_col4;
    @FXML private Label A_attributeInfo_name;
    @FXML private Label A_attributeInfo_distinct;
    @FXML private Label A_attributeInfo_missing;
    @FXML private Label A_attributeInfo_unique;
    @FXML private BarChart<String, Long> A_attributeInfo_chart;
    @FXML private Label A_chart_average;
    @FXML private Label A_chart_min;
    @FXML private Label A_chart_middle;
    @FXML private Label A_chart_max;
    @FXML private TextField B_algorithmName;
    @FXML private TextField B_classiferNum;
    @FXML private TextField B_maxDepth;
    @FXML private TextField B_minGini;
    @FXML private TextField B_inputDataScale;
    @FXML private TextField B_attributeScale;
    @FXML private TextField B_randomSeed;
    @FXML private ChoiceBox<AttributeTableViewItem> B_chooseClassId;
    @FXML private Button B_startTrain;
    @FXML private Button B_startTest;
    @FXML private Button C_startPredict;
    @FXML private ProgressBar B_progressBar;
    @FXML private TextArea B_TextArea;
    @FXML private TextField B_testSetPath;
    @FXML private TextField C_dataFilePath;
    @FXML private Label D_Accurancy;
    @FXML private Label D_Precision;
    @FXML private Label D_Fmeasure;
    @FXML private Label D_Recall;
    @FXML private Label D_F2;
    @FXML private Label D_F0_5;
    @FXML private Label D_AUC;
    @FXML private Canvas D_ROC_Canvas;
    @FXML private Slider D_thresoldSlider;
    @FXML private TextField D_thresoldText;
    @FXML private ImageView D_imageView;
    @FXML private TreeTableColumn<PredictInfoTableItem, String> C_infoTable_idCol;
    @FXML private TreeTableColumn<PredictInfoTableItem, String> C_infoTable_nameCol;
    @FXML private TreeTableColumn<PredictInfoTableItem, String> C_infoTable_recommendCol;
    @FXML private TreeTableColumn<PredictInfoTableItem, String> C_infoTable_actualCol;
    @FXML private TreeTableView<PredictInfoTableItem> C_infoTable;
    @FXML private TextField C_showIdResult;
    @FXML private Spinner<Integer> C_chooseIdSpinner;
    @FXML private Spinner<Integer> D_classiferIdSpinner;
    @FXML private Button B_openTestSetFileButton;
    @FXML
    void connectDataBase(ActionEvent event) {
        if(types==null || itemMap==null){
            new Thread(new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        MysqlConnection.connect();
                        types=MysqlConnection.getType();
                        itemMap = MysqlConnection.getItemMap();
                        MysqlConnection.close();
                    }catch (Exception e){
                        Platform.runLater(()->{
                            new Alert(Alert.AlertType.ERROR, "连接数据库失败，请确认数据库配置正常").show();
                        });
                    }
                    return null;
                }
            }).start();
        }
        TreeItem<PredictInfoTableItem> root = new TreeItem<>(new PredictInfoTableItem("化验类型","","",""));
        C_infoTable.setRoot(root);
        C_infoTable.setShowRoot(false);
        for (Map.Entry<Integer, String> entry : types.entrySet()) {
            TreeItem<PredictInfoTableItem> tmp=new TreeItem<>(new PredictInfoTableItem(entry.getValue(),"","",""));
            typeItems.put(entry.getKey(),tmp);
            root.getChildren().add(tmp);
        }
    }

    //打开训练数据
    @FXML
    public void A_openfileClick(ActionEvent event) {
        stage = (Stage) rootVBox.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开文件");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv","*.csv"));
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            new Thread(new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        trainDataBean = GUIUtils.getDataFromFile(file.getPath());
                        Platform.runLater(()->{
                            A_filePath.setText(file.getPath());
                            A_fileInfo_attributeNum.setText(String.valueOf(trainDataBean.getAttruibuteNum()));
                            A_fileInfo_instanceNum.setText(String.valueOf(trainDataBean.getInstanceNum()));
                            //读入到tableview中
                            ObservableList observableList = A_fileInfoTableView.getItems();
                            observableList.clear();
                            for (int i = 0; i < trainDataBean.getAttruibuteNum(); i++) {
                                observableList.add(new AttributeTableViewItem(i + 1, trainDataBean.getHeaders(i)));
                            }
                            //读入到类别序号中
                            B_chooseClassId.setItems(observableList);
                            B_chooseClassId.getSelectionModel().select(observableList.size() - 1);
                            //创建所有属性的属性信息
                            attributeInfoLabels = new ArrayList<>();
                            for (int i = 0; i < trainDataBean.getAttruibuteNum(); i++) {
                                attributeInfoLabels.add(new AttributeInfoLabel(trainDataBean.getHeaders(i), trainDataBean.getAttruibute(i)));
                            }
                            B_Tab.setDisable(false);
                        });
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            }).start();
            //读取文件
        }
    }
    //选中所有属性
    @FXML
    void A_selectAllAttributes(ActionEvent event) {
        ObservableList<AttributeTableViewItem> observableList = A_fileInfoTableView.getItems();
        for (AttributeTableViewItem tmp : observableList) {
            tmp.setCheck(true);
        }
    }
    //不选所有属性
    @FXML
    void A_selectNoneAttributes(ActionEvent event) {
        ObservableList<AttributeTableViewItem> observableList = A_fileInfoTableView.getItems();
        for (AttributeTableViewItem tmp : observableList) {
            tmp.setCheck(false);
        }
    }
    //反选所有属性
    @FXML
    void A_deselectAllAtributes(ActionEvent event) {
        ObservableList<AttributeTableViewItem> observableList = A_fileInfoTableView.getItems();
        for (AttributeTableViewItem tmp : observableList) {
            if (tmp.getCheck()) {
                tmp.setCheck(false);
            } else {
                tmp.setCheck(true);
            }
        }
    }
    //显示所选属性的信息
    @FXML
    void showSelectAttributeInfo(MouseEvent event) {
        AttributeTableViewItem selectItem = A_fileInfoTableView.getSelectionModel().getSelectedItem();
        if (selectItem != null) {
            AttributeInfoLabel attributeInfoLabel = attributeInfoLabels.get(selectItem.getId());
            A_attributeInfo_name.setText(attributeInfoLabel.getName());
            A_attributeInfo_missing.setText(attributeInfoLabel.getMissingString());
            A_attributeInfo_distinct.setText(attributeInfoLabel.getDistinctString());
            A_attributeInfo_unique.setText(attributeInfoLabel.getUniqueString());
            XYChart.Series<String, Long> series = attributeInfoLabel.getSeries();
            A_attributeInfo_chart.getData().setAll(series);
            //String[] strs = attributeInfoLabel.getCategories();
            A_chart_average.setText(GUIUtils.df2.format(attributeInfoLabel.getAverage()));
            A_chart_min.setText(GUIUtils.df2.format(attributeInfoLabel.getMin()));
            A_chart_middle.setText(GUIUtils.df2.format(attributeInfoLabel.getMiddle()));
            A_chart_max.setText(GUIUtils.df2.format(attributeInfoLabel.getMax()));
        }
    }
    //开始训练(多线程实现，避免ui卡死）
    @FXML
    void B_startTrain(ActionEvent event) {
        B_startTrain.setDisable(true);
        try {
            int treeNum = Integer.valueOf(B_classiferNum.getText());
            int maxDepth = Integer.valueOf(B_maxDepth.getText());
            double minGini = Double.valueOf(B_minGini.getText());
            double inputDataScale = Double.valueOf(B_inputDataScale.getText());
            int attributeScale = Integer.valueOf(B_attributeScale.getText());
            long randomSeed = Long.valueOf(B_randomSeed.getText());
            int classId = B_chooseClassId.getSelectionModel().getSelectedIndex();
            ObservableList<AttributeTableViewItem> observableList = A_fileInfoTableView.getItems();
            int[] inputClass = new int[observableList.size()];
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (AttributeTableViewItem tmp : observableList) {
                if (tmp.getCheck() == false) {
                    arrayList.add(tmp.getId());
                }
                if (tmp.getDiscrete()) {
                    inputClass[tmp.getId()] = (int) attributeInfoLabels.get(tmp.getId()).getDistinct();
                }
            }
            if (arrayList.contains(classId) == false) {
                arrayList.add(classId);
            }
            int[] exception = ClassifiersUtils.getIntArrary(arrayList.toArray(new Integer[arrayList.size()]));
            randomForest = new RandomForest(treeNum, maxDepth, minGini, trainDataBean.getInput(), inputClass, exception, classId, inputDataScale, attributeScale, randomSeed);
            //javaFX的多线程方式
            randomForestBuild();
            D_classiferIdSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, treeNum - 1));
        } catch (NumberFormatException e) {
            System.out.println("所有参数仅能设置为数字");
        } catch (Exception e){
            System.out.println("错误");
        }
    }
    //停止训练线程
    @FXML
    void B_stopTrain() {
        randomForestThread.interrupt();
    }

    @FXML
    void openClassifierObject(ActionEvent event) {
        stage = (Stage) rootVBox.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开模型");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("随机森林模型", "*.rfModel"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            randomForest = GUIUtils.readClassiferObject(file.getPath());
            B_classiferNum.setText(String.valueOf(randomForest.getTreeNum()));
            B_maxDepth.setText(String.valueOf(randomForest.getTreeDepth()));
            B_minGini.setText(String.valueOf(randomForest.getMinGini()));
            B_inputDataScale.setText(String.valueOf(randomForest.getInputScale()));
            B_attributeScale.setText(String.valueOf(randomForest.getAttributeScale()));
            B_randomSeed.setText(String.valueOf(randomForest.getRandomSeed()));
            //如果模型对应则可以使用可视化分析和预测
            if (randomForest.getCartTrees().size() == randomForest.getTreeNum()) {
                B_openTestSetFileButton.setDisable(false);
                B_Tab.setDisable(false);
                C_Tab.setDisable(false);
                D_Tab.setDisable(false);
            }else{
                new Alert(Alert.AlertType.WARNING, "模型无效，需要重新训练").showAndWait();
            }
        }
    }

    @FXML
    void saveClassifierAsText(ActionEvent event) {
        stage = (Stage) rootVBox.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存模型");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("随机森林模型文本", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            if (randomForest != null) {
                GUIUtils.writeClassiferAsText(file.getPath(), randomForest);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("请训练后再进行保存");
                alert.show();
            }
        }
    }

    @FXML
    void saveClassifierObject(ActionEvent event) {
        stage = (Stage) rootVBox.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存模型");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("随机森林模型", "*.rfModel"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            if (randomForest != null) {
                GUIUtils.writeClassiferObject(file.getPath(), randomForest);
            } else {
                new Alert(Alert.AlertType.WARNING,"请训练后再进行保存").showAndWait();
            }
        }
    }

    @FXML
    void B_openTestSetFile(ActionEvent event) {
        stage = (Stage) rootVBox.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开训练集文件");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv", "*.csv"));
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            //读取文件
            B_testSetPath.setText(file.getPath());
            try {
                testDataBean = GUIUtils.getDataFromFile(file.getPath());
                B_startTest.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void C_openDataFile() {
        stage = (Stage) rootVBox.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开需要预测的文件");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv", "*.csv"));
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        File file = fileChooser.showOpenDialog(stage);
        connectDataBase(null);
        if (file != null) {
            //读取文件
            C_dataFilePath.setText(file.getPath());
            try {
                classifyDataBean = GUIUtils.getDataFromFile(file.getPath());
                C_startPredict.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void B_startTest() {
        double[] results=randomForest.verifyRate(testDataBean.getInput());
        modelAnalysis = new ModelAnalysis(testDataBean.getAttruibute(B_chooseClassId.getSelectionModel().getSelectedIndex()), results);
        setD_ModelInfo();
        GraphicsContext context = D_ROC_Canvas.getGraphicsContext2D();
        context.clearRect(0,0,200,200);
        double[][] tmp=modelAnalysis.getROCLine();

        int[] Matrix = modelAnalysis.getMatrix();

        System.out.println("\t\t房颤\t\t正常");
        System.out.println("房颤\t\t" + Matrix[0] + "\t\t" + Matrix[1]);
        System.out.println("正常\t\t" + Matrix[2] + "\t\t" + Matrix[3]);

        //绘制ROC曲线
        context.strokePolyline(tmp[0], tmp[1], tmp[0].length);
        context.strokePolyline(new double[]{0,tmp[0][0]}, new double[]{200,tmp[1][0]}, 2);
        context.restore();

        //设置默认阈值0.5
        D_thresoldSlider.setValue(modelAnalysis.getThresold());
        C_Tab.setDisable(false);
        D_Tab.setDisable(false);
    }

    private void setD_ModelInfo(){
        D_Accurancy.setText(GUIUtils.df6.format(modelAnalysis.getAccurancy()));
        D_Recall.setText(GUIUtils.df6.format(modelAnalysis.getRecall()));
        D_Precision.setText(GUIUtils.df6.format(modelAnalysis.getPrecision()));
        D_Fmeasure.setText(GUIUtils.df6.format(modelAnalysis.getF_measure()));
        D_F0_5.setText(GUIUtils.df6.format(modelAnalysis.getF0_5()));
        D_F2.setText(GUIUtils.df6.format(modelAnalysis.getF2()));
        D_AUC.setText(GUIUtils.df6.format(modelAnalysis.getAUC()));
    }

    @FXML
    void C_startPredict() {
        classifyDataBean.setInputClass(randomForest.verify(classifyDataBean.getInput()));
        new Thread(new Task() {

            @Override
            protected Object call() throws Exception {
                try {
                    GUIUtils.writePredictToCSV(C_dataFilePath.getText(), classifyDataBean.getInputClass());
                }catch (Exception e){
                    System.out.println(e);
                }
                return null;
            }
        }).start();

        C_chooseIdSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,classifyDataBean.getInput().length-1));
    }

    @FXML
    void showClassifer(){
        int id = D_classiferIdSpinner.getValue();
        try {
            FileInputStream fs = new FileInputStream("image/classifer-" + id + ".jpg");
            Image image = new Image(fs);
            D_imageView.setImage(image);
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void D_clickAnalysis(ActionEvent event) {
        //分析模型
        ChartViewer tmp =new ChartViewer(JFCUtils.creatChart(trainDataBean, randomForest));
        Stage stage = new Stage();
        stage.setTitle("模型评估");
        stage.setScene(new Scene(tmp,800,600));
        stage.show();
    }


    //利用Task实现javaFX多线程
    public void randomForestBuild() {
        randomForestThread = new Thread(new Task() {
            @Override
            protected Object call() throws Exception {
                System.out.println("开始训练");
                long startTime = System.currentTimeMillis();
                randomForest.build(B_progressBar);
                long endTime = System.currentTimeMillis();
                System.out.println("算法运行时间：" + (endTime - startTime) + "ms");
                PaintingByGraphViz.getTreePicture(randomForest.printRandomForest());
                Platform.runLater(()->{
                    C_Tab.setDisable(false);
                    D_Tab.setDisable(false);
                    B_startTrain.setDisable(false);
                    B_openTestSetFileButton.setDisable(false);
                });
                return null;
            }
        }, "RandomForest");
        randomForestThread.start();
    }

    @FXML
    void thresoldEnter(KeyEvent event) {
        if (event.getCode()== KeyCode.ENTER) {
            String str=D_thresoldText.getText();
            try {
                double tmp = Double.valueOf(str);
                if(tmp>1 ||tmp <0){
                    D_thresoldSlider.setValue(0.5);
                    new Alert(Alert.AlertType.ERROR, "仅能输入0-1的小数").showAndWait();
                }else {
                    str = GUIUtils.df2.format(tmp);
                    D_thresoldSlider.setValue(Double.valueOf(str));
                }
            }catch (NumberFormatException e){
                D_thresoldSlider.setValue(0.5);
                new Alert(Alert.AlertType.ERROR, "仅能输入数字").showAndWait();
            }

        }
    }

    @FXML
    void sliderScroll(ScrollEvent event) {
        double deltaY = event.getDeltaY();
        if (deltaY > 0) {// 向上滚动,放大图片
            D_thresoldSlider.increment();
        } else {// 向下滚动,缩小图片
            D_thresoldSlider.decrement();
        }
    }

    @FXML
    void findMaxAccurancy(ActionEvent event) {
        if (modelAnalysis != null) {
            modelAnalysis.changeAnalysis(modelAnalysis.findBestThresold(0.01));
            D_thresoldSlider.setValue(modelAnalysis.getThresold());
        }
    }

    @FXML
    void imageZoom(ScrollEvent event) {
        double deltaY = event.getDeltaY();
        if (deltaY > 0) {// 向上滚动,放大图片
            double height = D_imageView.getFitHeight()*1.05;
            D_imageView.setFitHeight(height);
        } else {// 向下滚动,缩小图片
            double height = D_imageView.getFitHeight() / 1.05;
            D_imageView.setFitHeight(height);
        }
    }

    //单独显示数据
    @FXML
    void C_getInfo(){
        int rowNum = C_chooseIdSpinner.getValue();
        double[] value = classifyDataBean.getInput(rowNum);//元组值
        String[] id = classifyDataBean.getHeaders();//缩写

        int predictClass=classifyDataBean.getInputClass()[rowNum];

        switch (predictClass){
            case 0:C_showIdResult.setText("正常");break;
            case 1:C_showIdResult.setText("可能患有房颤");break;
        }

        for(TreeItem<PredictInfoTableItem> tmp:typeItems.values()){
            tmp.getChildren().clear();
        }

        for (int i = 0; i < value.length; i++) {
            PredictInfoTableItem item = new PredictInfoTableItem(id[i], null, null, GUIUtils.df2.format(value[i]));
            EMRItem tmp=itemMap.get(item.getId());
            if(tmp!=null){
                item.setName(tmp.getName());
                item.setRecommend(tmp.getRecommend());
                item.setActual(item.getActual()+tmp.compare(value[i]));
                item.setParent(typeItems.get(tmp.getId()));
                TreeItem<PredictInfoTableItem> child = new TreeItem<>(item);
                item.getParent().getChildren().add(child);
            }else {
                TreeItem<PredictInfoTableItem> child = new TreeItem<>(item);
                typeItems.get(0).getChildren().add(child);
            }


        }
    }

    @FXML
    void clearTextArea() {
        B_TextArea.clear();
    }
    @FXML
    void showHelp(){
        B_TextArea.clear();
        B_TextArea.setText(GUIUtils.getPromptText());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StageManager.CONTROLLER.put("MainForm", this);
        A_fileInfoTableView_col1.setCellValueFactory(new PropertyValueFactory("num"));
        A_fileInfoTableView_col2.setCellValueFactory(new PropertyValueFactory("checkBox"));
        A_fileInfoTableView_col3.setCellValueFactory(new PropertyValueFactory("name"));
        A_fileInfoTableView_col4.setCellValueFactory(new PropertyValueFactory("isDiscrete"));

        C_infoTable_idCol.setCellValueFactory( (param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getId()));
        C_infoTable_nameCol.setCellValueFactory( (param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getName()));
        C_infoTable_recommendCol.setCellValueFactory( (param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getRecommend()));
        C_infoTable_actualCol.setCellValueFactory( (param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getActual()));

        D_classiferIdSpinner.valueProperty().addListener((observable,oldValue,newValue)->showClassifer());
        D_classiferIdSpinner.editorProperty().addListener((observable,oldValue,newValue)->{
            try {
                int value = Integer.valueOf(newValue.getText());
                if (value >= randomForest.getTreeNum() || value < 0) {
                    new Alert(Alert.AlertType.ERROR, "仅能输入0-" + (classifyDataBean.getInput().length-1) + "之间的数字").showAndWait();
                    D_classiferIdSpinner.getEditor().setText(oldValue.getText());
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "仅能输入数字").showAndWait();
                D_classiferIdSpinner.getEditor().setText(oldValue.getText());
            }
        });

        C_chooseIdSpinner.valueProperty().addListener((observable,oldValue,newValue)->C_getInfo());
        C_chooseIdSpinner.editorProperty().addListener((observable,oldValue,newValue)->{
            try {
                int value = Integer.valueOf(newValue.getText());
                if (value >= randomForest.getTreeNum() || value < 0) {
                    new Alert(Alert.AlertType.ERROR, "仅能输入0-" + (randomForest.getTreeNum()-1) + "之间的数字").showAndWait();
                    C_chooseIdSpinner.getEditor().setText(oldValue.getText());
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "仅能输入数字").showAndWait();
                C_chooseIdSpinner.getEditor().setText(oldValue.getText());
            }
        });
        D_thresoldSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            D_thresoldText.setText(GUIUtils.df2.format(newValue.doubleValue()));
            if(modelAnalysis!=null){
                modelAnalysis.changeAnalysis(GUIUtils.df2.format(newValue.doubleValue()));
                setD_ModelInfo();
            }
        });

        new Thread(new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    MysqlConnection.connect();
                    types=MysqlConnection.getType();
                    itemMap = MysqlConnection.getItemMap();
                    MysqlConnection.close();
                }catch (Exception e){
                    Platform.runLater(()->{
                        new Alert(Alert.AlertType.ERROR, "连接数据库失败，请手动连接").show();
                    });
                }
                return null;
            }
        }).start();


        OutputStream textAreaStream = new OutputStream() {
            StringBuffer stringBuffer = new StringBuffer();
            public void write(int b) {
                //利用stringbuffer缓存，检测到回车以后调用javafx线程更新输入框
                stringBuffer.append((char) b);
                if (stringBuffer.indexOf("\n")!=-1) {
                    String str = stringBuffer.toString();
                    stringBuffer.setLength(0);//清空字符串
                    Platform.runLater(()->B_TextArea.appendText(str));
                }
            }

            public void write(byte b[]) {
                stringBuffer.append(b);
                //stringBuffer.append(new String(b));
                if (stringBuffer.indexOf("\n")!=-1) {
                    String str = stringBuffer.toString();
                    stringBuffer.setLength(0);//清空字符串
                    Platform.runLater(()->B_TextArea.appendText(str));
                }
            }
            public void write(byte b[], int off, int len) throws IOException {
                stringBuffer.append(new String(b, off, len));
                if (stringBuffer.indexOf("\n")!=-1) {
                    String str = stringBuffer.toString();
                    stringBuffer.setLength(0);//清空字符串
                    Platform.runLater(()->B_TextArea.appendText(str));
                }
            }

        };
        PrintStream printStream = new PrintStream(textAreaStream);
        System.setOut(printStream);
    }

}
