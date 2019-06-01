package GUI.View;

import GUI.Controller.MainFormController;
import GUI.MyUtils.StageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;

public class MainForm extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FileInputStream fs = new FileInputStream("icon/icon.png");
        Image image = new Image(fs);
        fs.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
        Parent root = loader.load();
        MainFormController controller = loader.getController();
        controller.setDragListener(primaryStage);
        primaryStage.setTitle("辅助诊断工具");
        Scene scene = new Scene(root, 600, 525);
        scene.getStylesheets().add(getClass().getResource("MainForm.css").toExternalForm());
        primaryStage.setScene(scene);
        StageManager.STAGE.put("MainForm", primaryStage);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setOnCloseRequest(event ->System.exit(0));
        primaryStage.getIcons().add(image);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
