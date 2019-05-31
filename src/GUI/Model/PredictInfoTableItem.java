package GUI.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

public class PredictInfoTableItem {
    private StringProperty id;
    private StringProperty name;
    private StringProperty recommend;
    private StringProperty actual;
    private TreeItem<PredictInfoTableItem> parent;

    public PredictInfoTableItem(String id, String name, String recommend, String actual) {
        this.id=new SimpleStringProperty(id);
        this.name=new SimpleStringProperty(name);
        this.recommend=new SimpleStringProperty(recommend);
        this.actual=new SimpleStringProperty(actual);
    }


    public PredictInfoTableItem(String id, String name, String recommend, String actual,TreeItem<PredictInfoTableItem> parent) {
        this.id=new SimpleStringProperty(id);
        this.name=new SimpleStringProperty(name);
        this.recommend=new SimpleStringProperty(recommend);
        this.actual=new SimpleStringProperty(actual);
        this.parent = parent;
    }

    public TreeItem<PredictInfoTableItem> getParent() {
        return parent;
    }

    public void setParent(TreeItem<PredictInfoTableItem> parent) {
        this.parent = parent;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getRecommend() {
        return recommend.get();
    }

    public StringProperty recommendProperty() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend.set(recommend);
    }

    public String getActual() {
        return actual.get();
    }

    public StringProperty actualProperty() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual.set(actual);
    }
}
