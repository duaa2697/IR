package sample;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Input_Query {
    List<String> words;
    private List<String> stop_words ;
    public File listOfFiles[];
    public File listOfFiles1[];
    IrData con;
    List<Float> sim_d ;
    List<String> res ;
    public Input_Query(String folderPath) throws FileNotFoundException {
        listOfFiles1 = new File("C:\\\\Users\\\\ASUS\\\\Desktop\\\\ir_m\\\\IR Homework\\\\corpus").listFiles();
        sim_d=new ArrayList<Float>();
        res=new ArrayList<String>();
        this.words =new ArrayList<String>();
        this.stop_words = new ArrayList<String>();
        loadStopwords();
        listOfFiles = new File(folderPath).listFiles();
        con=new IrData("root1","root","ir");
        con.ConnectToServer();
        con.selectDataBase();
        fetchWords(0);
    }
    private void fetchWords(int query_id) throws FileNotFoundException {
        Scanner inputFile = new Scanner(listOfFiles[query_id]);
        while(inputFile.hasNextLine()){
            String currentLine = inputFile.nextLine();
            if(currentLine!=null && !(currentLine.contains("<") && currentLine.contains(">"))){
                currentLine = currentLine.replaceAll("[-]", " ");
                StringTokenizer stringTokenizer = new StringTokenizer(currentLine);
                while(stringTokenizer.hasMoreTokens()){
                    String currentToken = stringTokenizer.nextToken().toUpperCase();
                    String currentTokenModified = currentToken.replaceAll("[^a-zA-Z0-9]", "");
                    if(currentTokenModified.equals(""))
                        continue;
                    else{
                        Stemmer porter = new Stemmer();
                        String y=porter.stemWord(currentTokenModified);
                        y.replace(" ","");
                        currentTokenModified=y;
                        currentTokenModified=currentTokenModified.toUpperCase();
                        if(!stop_words.contains(currentTokenModified)){
                            System.out.println(currentTokenModified);
                            words.add(currentTokenModified);
                        }
                    }
                }
            }
        }
    }
    public List<String> solve() throws SQLException {
        float sum=0,sim=0;
        float len_d=0,len_q=0;
        len_q=get_len_query();
        ResultSet res1,res2,res_l;
        for (int j=1;j<424;j++){
            String query="SELECT length FROM `doc` WHERE id=" +
                    j;
            res_l=con.ExecuteQuery(query);
            if(res_l.next()){
                len_d=res_l.getFloat("length");
            }
            sum=0;
            for (int i =0;i<words.size();i++){
                int id = con.get_idTerm(words.get(i));
                String word=words.get(i);
                String query1,query2;
                query1="SELECT w FROM `doc_term` WHERE doc_id=" +
                        j +
                        " and term_id=" +
                        id;
                query2="SELECT idfi FROM `term` WHERE id=" +
                        id;
                res1=con.ExecuteQuery(query1);
                res2=con.ExecuteQuery(query2);
                if(res1.next() && res2.next()){
                    sum+=res1.getFloat("w")*res2.getFloat("idfi");
                }
            }
            sim=sum/(len_d*len_q);
            sim_d.add(sim);
        }
        for(int i = 0 ; i < 40;i++){
            List <Float> temp = new ArrayList<Float>();
            temp.clear();
            temp.add(Collections.max(sim_d));
            /*if(Collections.max(sim_d)<0.5){
                break;
            }*/
            String d =listOfFiles1[Collections.indexOfSubList(sim_d,temp)].toString();
            d=d.substring(46,d.length()-4);
            res.add(d);
            sim_d.set(Collections.indexOfSubList(sim_d,temp), (float) 0);
        }
        System.out.println(" ");
        for (int i =0 ;i<res.size();i++){
            System.out.print(res.get(i)+" ");
        }





        return res ;
    }
    public float get_len_query() throws SQLException {
        boolean res ;
        float length=0 ;
        ResultSet res1 ;
        for(int i = 0 ; i < words.size();i++){
            int id = con.get_idTerm(words.get(i));
            String query2="SELECT `idfi` FROM `term` WHERE id=" +
                    id;
            res1=con.ExecuteQuery(query2);
            if(res1.next()){
                float idfi=res1.getFloat("idfi");
                idfi=idfi*idfi;
                length=length+idfi;
            }
        }
        length= (float) Math.sqrt(length);
        return length;
    }

    public void loadStopwords() {
        try {
            stop_words = Files.readAllLines(Paths.get("C:\\Users\\ASUS\\Desktop\\ir_m\\IR Homework\\stop words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ;
    }
}
