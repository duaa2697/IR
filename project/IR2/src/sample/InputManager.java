package sample;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class InputManager {
    private TreeMap<String, ArrayList<Integer>> tokens = new TreeMap<>();
    public File listOfFiles[];
    public List<Integer> max_frequency;
    private List<String> words ;
    private List<String> stop_words ;
    public List<String> words_i;
    IrData my_data ;
    public InputManager(String folderPath) {
        my_data=new IrData("root1","root","ir");
        max_frequency=new ArrayList<Integer>();
        words_i=new ArrayList<String>();
        stop_words = new ArrayList<String>();
        loadStopwords();
        listOfFiles = new File(folderPath).listFiles();
        words=new ArrayList<String>();
    }

    public TreeMap<String, ArrayList<Integer>> getTokens(){
        return tokens;
    }

    public void scanFiles() throws FileNotFoundException, SQLException {

        for(int i=0; i<listOfFiles.length; i++) {
            fetchWords(i);
            set_max_freq();
            my_data.ConnectToServer();
            my_data.selectDataBase();
            my_data.add_doc((i+1)+"",max_frequency.get(i));
            for (int k =0;k<words_i.size();k++){
                my_data.addWord(words_i.get(k),i+1);
            }
            words_i.clear();
        }
        my_data.inti_term();
        my_data.inti_doc_term();
    }

    public void fetchWords(int Doc_id) throws FileNotFoundException{
        Scanner inputFile = new Scanner(listOfFiles[Doc_id]);
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

                        String y= porter.stemWord(currentTokenModified);
                        y.replace(" ","");
                        currentTokenModified=y;
                        currentTokenModified=currentTokenModified.toUpperCase();
                        if(!stop_words.contains(currentTokenModified)){
                            System.out.println(currentTokenModified);
                            words.add(currentTokenModified);
                            words_i.add(currentTokenModified);
                        }

                    }
                }
            }
        }
    }
    private void set_max_freq(){
        int count=0;
        int max = 0;
        for (int i =0 ; i<words_i.size();i++){
            count=0;
            for (int j = 0 ;j<words_i.size();j++){
                if(words_i.get(i).equals(words_i.get(j))){
                    count++;
                }
            }
            if(count>max){
                max=count;
            }
        }
        max_frequency.add(max);
    }

   /* void fetchWords(int Doc_id) throws FileNotFoundException {
        Scanner inputFile = new Scanner(listOfFiles[Doc_id]);
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
                        //push tokens of entire collection into TreeMap here
                        if(tokens.containsKey(currentTokenModified)){
                            tokens.get(currentTokenModified).add(Doc_id);
                        }
                        else {
                            ArrayList <Integer> temp = new ArrayList<>();
                            temp.add(Doc_id);
                            tokens.put(currentTokenModified,temp);
                        }
                    }
                }
            }
        }
    }*/

    public void loadStopwords() {
        try {
            stop_words = Files.readAllLines(Paths.get("C:\\Users\\ASUS\\Desktop\\ir_m\\IR Homework\\stop words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ;
    }

}
