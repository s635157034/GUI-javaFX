package GUI.MyUtils;

import GUI.Model.EMRItem;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MysqlConnection {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/EMR?serverTimezone=GMT%2B8";

    static final String USER = "test";
    static final String PASS = "123456";

    static Connection conn = null;
    static Statement stmt = null;

    public static void connect() {
        if(conn==null) {
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                stmt=conn.createStatement();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ResultSet excuteQuery(String sql){
        try {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(){
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, EMRItem> getItemMap(){
        Map<String, EMRItem> map = new HashMap<>(150);
        String sql = "select item,abbreviation,lower,higher,test_type_id from test_item";
        ResultSet rs = excuteQuery(sql);
        try {
            while (rs.next()) {
                EMRItem item = new EMRItem(rs.getString("item"), rs.getString("abbreviation"),
                        rs.getDouble("lower"), rs.getDouble("higher"), rs.getInt("test_type_id"));
                map.put(item.getAbbr(), item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<Integer,String> getType(){
        //ArrayList<String> strs=new ArrayList<>();
        Map<Integer, String> map = new HashMap<>();
        String sql = "select id,name from test_type";
        ResultSet rs = excuteQuery(sql);
        try {
            while (rs.next()) {
                map.put(rs.getInt("id"),rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
