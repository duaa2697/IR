package sample;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.util.Base64;

public class IrData extends MySqlCon{

    public IrData(String userName, String password, String databaseName) {
        super(userName, password, databaseName);
    }

    public boolean createDocTable() {
        String query = "create table Doc("
                + "id int primary key AUTO_INCREMENT,"
                + "name varchar(255),"
                + "max_frequency int not null,"
                + "length float not null"
                + ")";
        return ExecuteDMLQuery(query);
    }
    public boolean inti_term() throws SQLException {
        ResultSet res ,res1,res2;
        String query = "SELECT DISTINCT name FROM `word`";
        res = ExecuteQuery(query);
        String temp ;
        int n_doc=0;
        String query3="SELECT COUNT(*) FROM `doc`";
        res2=ExecuteQuery(query3);
        if(res2.next()){
            n_doc=res2.getInt("COUNT(*)");
        }
        while ((res.next())){
            int dfi=0;
            float idfi=0;
            temp=res.getString("name");
            String query2 ="SELECT COUNT(DISTINCT doc_id) FROM `word` WHERE name ='" +
                    temp +
                    "'";
            res1=ExecuteQuery(query2);
            if(res1.next()){
                dfi= res1.getInt("COUNT(DISTINCT doc_id)");
                idfi= (float) (Math.log(n_doc*1.0/dfi)/Math.log(2));
            }
            String query1= "INSERT INTO `term`(`name`, `dfi`, `idfi`) VALUES ('" +
                    temp +
                    "'," +
                    dfi +
                    "," +
                    idfi +
                    ")";
            ExecuteDMLQuery(query1);
        }
        return true;
    }
    public boolean inti_doc_term() throws SQLException {
        ResultSet res,res1,res2,res3 ;
        String query="SELECT DISTINCT name,doc_id FROM `word`";
        res=ExecuteQuery(query);
        while (res.next()){
            String name =res.getString("name");
            int doc_id=res.getInt("doc_id");
            int term_id=get_idTerm(name);
            int frequency=0;
            String query1="SELECT COUNT(name) FROM `word` WHERE name='" +
                    name +
                    "' AND doc_id=" +
                    doc_id;
            res1=ExecuteQuery(query1);
            if(res1.next()){
                frequency=res1.getInt("COUNT(name)");
            }
            String query2 ="SELECT max_frequency FROM `doc` WHERE id=" +
                    doc_id;
            res2=ExecuteQuery(query2);
            int max_frequency =1;
            if(res2.next()){
                max_frequency=res2.getInt("max_frequency");
            }
            float tf= (float) (frequency*1.0/max_frequency);
            float idfi=1;
            String query3="SELECT idfi FROM `term` WHERE id =" +
                    term_id;
            res3=ExecuteQuery(query3);
            if(res3.next()){
                idfi=res3.getFloat("idfi");
            }
            float w=tf*idfi;
            String query4="INSERT INTO `doc_term`( `doc_id`, `term_id`, `frequency`, `tf`, `w`) VALUES (" +
                    doc_id +
                    "," +
                    term_id +
                    "," +
                    frequency +
                    "," +
                    tf +
                    "," +
                    w +
                    ")";
            ExecuteDMLQuery(query4);
        }
        return true;
    }
    public void sss(){
        createDocTable();
        creatWordTable();
        creatTermsTable();
        creatDoc_TermTable();
    }
    public boolean add_doc(String name , int max_frequency){
        boolean res ;
        String query = "INSERT INTO `doc`(`name`,`max_frequency`,`length`) VALUES ('" +
                 name+
                "',"+
                max_frequency+
                "," +
                0 +
                ")";
        res=ExecuteDMLQuery(query);
        return  res ;
    }
    public boolean set_len_doc(int id) throws SQLException {
        boolean res ;
        float length=0 ;
        ResultSet res1 ;
        String query2="SELECT `w` FROM `doc_term` WHERE doc_id=" +
                id;
        res1=ExecuteQuery(query2);
        while (res1.next()){
            float temp = res1.getFloat("w");
            temp=temp*temp;
            length=length+temp;
        }
        length= (float) Math.sqrt(length);
        String query="UPDATE `doc` SET `length`=" +
                length +
                "  WHERE id=" +
                id;
        res=ExecuteDMLQuery(query);
        return res ;
    }
    public int get_idTerm(String name) throws SQLException {
        ResultSet res ;
        String query = "SELECT id FROM `term` WHERE name='" +
                name +
                "'";
        int id=-1;
        res= ExecuteQuery(query);
        if(res.next()){
            id=res.getInt("id");
        }
        return id;
    }

    public boolean ConnectToServer() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, userName, password);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean creatTermsTable(){
        String query ="create table term("
                + "id int primary key AUTO_INCREMENT,"
                + "name varchar(255) not null,"
                + "dfi int not null,"
                + "idfi float not null"
                + ")";
        return ExecuteDMLQuery(query);
    }

    public boolean creatDoc_TermTable(){
        String query = "create table Doc_Term("
                + "id int primary key AUTO_INCREMENT,"
                + "doc_id int not null,"
                + "term_id int not null,"
                + "frequency int not null,"
                + "tf float not null,"
                + "w float not null,"
                + "foreign key(doc_id) references doc(id),"
                + "foreign key(term_id) references term(id))";

        return ExecuteDMLQuery(query);
    }

    public boolean creatWordTable(){
        String query = "create table word("
                + "id int primary key AUTO_INCREMENT,"
                + "doc_id int not null,"
                + "name varchar(255) not null,"
                + "foreign key(doc_id) references doc(id)"
                + ")";
        return ExecuteDMLQuery(query);
    }
    public boolean addWord(String word,int doc_id){
        String query = "insert into word (name,doc_id) values('"
                +word
                +"',"
                +doc_id
                +")";
        return ExecuteDMLQuery(query);
    }

    private boolean creatTransferTable(){
        String query = "create table transfer("
                + "id int primary key AUTO_INCREMENT,"
                + "send_id int not null,"
                + "receive_id int not null,"
                + "id_trans int unique not null,"
                + "amount float not null,"
                + "description varchar(255) not null,"
                + "foreign key(send_id) references users(id),"
                + "foreign key(receive_id) references users(id)"
                + ")";
        return ExecuteDMLQuery(query);
    }

    private boolean ConnectToDataBase() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url + DatabaseName, userName, password);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean Connect() {
        if (!firstConnection())
            if (!ConnectToDataBase()) {
                return false;
            }
        return true;
    }

    private boolean firstConnection() {
        try {
            if (ConnectToServer()) {
                connection.setAutoCommit(false);
                if (!createDataBase() ||
                        !createTables()) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    ExecuteQuery("use master;" +
                            " ALTER DATABASE " + DatabaseName + " SET SINGLE_USER WITH ROLLBACK IMMEDIATE; " +
                            "DROP DATABASE " + DatabaseName + ";");
                    return false;
                }
                connection.commit();
                connection.setAutoCommit(true);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    private boolean createDataBase() {
        try {
            Statement stmt = connection.createStatement();
            String query = "create database " + DatabaseName;
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean createTables() {
        if (!selectDataBase() || !creatTransferTable() ) {
            return false;
        }
        return true;
    }

    public boolean selectDataBase() {
        try {
            Statement stmt = connection.createStatement();
            String query = "use " + DatabaseName;
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

}
