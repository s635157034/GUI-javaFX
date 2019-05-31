package GUI.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class AttributeTableViewItem {
    private IntegerProperty num;
    private StringProperty name;
    private CheckBox checkBox=new CheckBox();
    private CheckBox isDiscrete = new CheckBox();

    public AttributeTableViewItem(int num, String name) {
        this.num = new SimpleIntegerProperty(num);
        this.name = new SimpleStringProperty(name);
    }

    public int getNum() {
        return num.get();
    }

    public int getId() {
        return num.get() - 1;
    }

    public IntegerProperty numProperty() {
        return num;
    }

    public void setNum(int num) {
        this.num.set(num);
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

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public boolean getCheck(){
        return checkBox.isSelected();
    }

    public void setCheck(boolean flag){
        checkBox.setSelected(flag);
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public CheckBox getIsDiscrete() {
        return isDiscrete;
    }

    public boolean getDiscrete() {
        return isDiscrete.isSelected();
    }

    public void setDiscrete(boolean flag) {
        isDiscrete.setSelected(flag);
    }

    public void setIsDiscrete(CheckBox isDiscrete) {
        this.isDiscrete = isDiscrete;
    }

    @Override
    public String toString() {
        return "(" + num.getValue() + ")" + name.getValue();
    }
}
