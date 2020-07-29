package sample;


import java.io.FileNotFoundException;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException, FileNotFoundException {
        //x.sss();
        Input_Query v = new Input_Query("C:\\Users\\ASUS\\Desktop\\query");
        v.solve();
    }
}