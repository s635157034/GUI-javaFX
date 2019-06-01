package GUI.Controller;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


public class DragImageListener implements EventHandler<MouseEvent> {
    private static final double RATE=0.1;
    private double x = 0;
    private double y = 0;
    private double xOffset = 0;
    private double yOffset = 0;
    private ImageView node;
    private ScrollPane pane;
    private double scale = 0;

    @Override
    public void handle(MouseEvent event) {
        //event.consume();
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            //程序内scene的位置
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            x = pane.getHvalue();
            y = pane.getVvalue();
            Image image = node.getImage();
            scale = image.getWidth() / image.getHeight();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            double deltaX = (event.getSceneX() - xOffset);
            double deltaY = (event.getSceneY() - yOffset);
            pane.setVvalue(y -  deltaY/ pane.getHeight());
            pane.setHvalue(x - deltaX / pane.getWidth());
            if(pane.getHvalue()==0){
                x = 0;
                xOffset = event.getSceneX();
            }else if(pane.getHvalue()==1){
                x = 1;
                xOffset = event.getSceneX();
            }
            if(pane.getVvalue()==0){
                yOffset = event.getSceneY();
                y = 0;
            } else if (pane.getVvalue() == 1) {
                y=1;
                yOffset = event.getSceneY();
            }
        }
    }

    public DragImageListener(ImageView node, ScrollPane pane) {
        this.node = node;
        this.pane = pane;
        node.setOnMousePressed(this);
        node.setOnMouseDragged(this);

    }
}
