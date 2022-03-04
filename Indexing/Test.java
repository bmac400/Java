import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.naming.spi.DirStateFactory.Result;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test {
    public static void main(String args[]) throws FileNotFoundException, IOException, ParseException {
        int docID = 0;
        InvertedIndex index = new InvertedIndex();
        Map<Integer,String> intToSceneID = new HashMap<Integer,String>();
        Map<Integer,String> intToDocID = new HashMap<Integer,String>();
        //Opening File
        JSONParser parser = new JSONParser();
        String filename = "C:/Users/bryan/VSCode/Indexing/shakespeare-scenes.json.gz";
        Path source = Paths.get(filename);
        GZIPInputStream gis = new GZIPInputStream(new FileInputStream(source.toFile()));
        InputStreamReader tes = new InputStreamReader(gis, "UTF-8");
        BufferedReader buf = new BufferedReader(tes);
        JSONObject obj = (JSONObject) parser.parse(buf);
        //Going through json
        int TotalWords = 0;
        int maxScene = 0;
        int maxSceneId = 0;
        int minScene = 2146500;
        int minSceneID = 0;
        JSONArray test2 = (JSONArray)obj.get("corpus");
        String currentDocID = "";
        for(int i = 0; i < test2.size(); i++) {
            JSONObject temp = (JSONObject)test2.get(i);
            if (!temp.get("playId").equals(currentDocID)){
                docID++;
                intToDocID.put(docID, (String) temp.get("playId"));
                currentDocID = (String) temp.get("playId");
            }
            Long sceneIdNum = (Long)temp.get("sceneNum"); 
            intToSceneID.put(sceneIdNum.intValue(), (String)temp.get("sceneId"));

            String text = (String)temp.get("text");
            String[] words = text.split(" ");
            int wordPos = 1;
            for(String x: words){
                if("".equals(x)) {
                    continue;
                } else {
                    
                    index.add(x, docID, sceneIdNum.intValue(), wordPos);
                    
                    wordPos++;
                }
            }
            if(wordPos > maxScene) {
                maxScene = wordPos;
                maxSceneId = sceneIdNum.intValue();
            }
            if(wordPos < minScene) {
                minScene = wordPos;
                minSceneID = sceneIdNum.intValue();
            }
            TotalWords += wordPos;
        }

        //Find scenes where thee or thou are more common than you
        Map<List<Integer>, Integer> thee = index.getFrequencyByDocumentandScene("thee");
        Map<List<Integer>, Integer> thou = index.getFrequencyByDocumentandScene("thou");
        Map<List<Integer>, Integer> you = index.getFrequencyByDocumentandScene("you");
        //Union of thee and thou
        
        
        Set<List<Integer>>test = new HashSet<List<Integer>>(thee.keySet());
        BufferedWriter writer0 = new BufferedWriter(new FileWriter("terms0.txt"));

        test.addAll(thou.keySet());
        List<List<Integer>> list2 = new ArrayList<List<Integer>>(test);
        for(List<Integer> y : list2){
            int theeCount = 0;
            if(thee.containsKey(y)){
                theeCount = thee.get(y);
            }
            int thouCount = 0;
            if(thou.containsKey(y)) {
                thouCount = thou.get(y);
            }
            int youCount = 0;
            if(you.containsKey(y)){
                youCount = you.get(y);
            }
            if(thouCount+theeCount > youCount) {
                writer0.write("DocumentID: " + intToDocID.get(y.get(0)) +" Scene: " +  intToSceneID.get(y.get(1)) + "\n");
            }


        }
        writer0.close();
        //Get union of thee or thou map
        //Compare to you
        //Find scenes where place names venice, rome, or denmark are mentioned

        Map<List<Integer>, Integer> map1 = index.getFrequencyByDocumentandScene("venice");
        Map<List<Integer>, Integer> map2= index.getFrequencyByDocumentandScene("rome");
        Map<List<Integer>, Integer> map3 =index.getFrequencyByDocumentandScene("denmark");
        Map<List<Integer>, Integer> result = new HashMap<List<Integer>,Integer>(map1);
        result.putAll(map1);
        result.putAll(map2);
        result.putAll(map3);
        Set<List<Integer>> keys6 = result.keySet();
        BufferedWriter writer = new BufferedWriter(new FileWriter("terms1.txt"));
        for(List<Integer> key : keys6){
            //System.out.println("DocumentID: " + intToDocID.get(key.get(0)));
           
            String output = "DocumentID: " + intToDocID.get(key.get(0))+ "\n";
            writer.write(output);

        }
        writer.close();
        //Find plays where soldier are mentioned
        Map<Integer,Integer> temp5 = index.getFrequencyByDocument("soldier");
        Set<Integer> keys5 = temp5.keySet();
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("terms3.txt"));
        for(Integer key : keys5){
            writer2.write("DocumentID: " + intToDocID.get(key) +" Value: " + temp5.get(key) + "\n");
            //System.out.println("DocumentID: " + intToDocID.get(key) +" Value: " + temp5.get(key));
        }
        writer2.close();
        //Find plays where the word soldier is mentioned
        BufferedWriter writer3 = new BufferedWriter(new FileWriter("terms2.txt"));
        Map<Integer,Integer> temp4 = index.getFrequencyByDocument("soldier");
        Set<Integer> keys4 = temp4.keySet();
        for(Integer key : keys4){
            writer3.write("DocumentID: " + intToDocID.get(key) +" Value: " + temp4.get(key) + "\n");
            //System.out.println("DocumentID: " + intToDocID.get(key) +" Value: " + temp4.get(key));
        }
        writer3.close();

        //Find scenes where poor yorick is mentioned
        Map<List<Integer>,Integer> temp3 = index.findPhrase("poor yorick");
        Set<List<Integer>> keys3 = temp3.keySet();
        BufferedWriter writer4 = new BufferedWriter(new FileWriter("phrase0.txt"));
        for(List<Integer> key : keys3){
            writer4.write("DocumentID: " + intToDocID.get(key.get(0)) +" Scene: " +  intToSceneID.get(key.get(1)) +" Value: " + temp3.get(key) + "\n");
            //System.out.println("DocumentID: " + intToDocID.get(key.get(0)) +" Scene: " +  intToSceneID.get(key.get(1)) +" Value: " + temp3.get(key));
        }
        writer4.close();
        //Find scenes where "wherefore are thou romeo" is mentioned
        Map<List<Integer>,Integer> temp2 = index.findPhrase("wherefore art thou romeo");
        BufferedWriter writer5 = new BufferedWriter(new FileWriter("phrase1.txt"));
        Set<List<Integer>> keys2 = temp2.keySet();
        for(List<Integer> key : keys2){
            //System.out.println("DocumentID: " + intToDocID.get(key.get(0)) +" Scene: " +  intToSceneID.get(key.get(1)) +" Value: " + temp2.get(key));
            writer5.write("DocumentID: " + intToDocID.get(key.get(0)) +" Scene: " +  intToSceneID.get(key.get(1)) +" Value: " + temp2.get(key) + "\n");
        }
        writer5.close();
        //Find scenes where "let slip" is mentioned
        Map<List<Integer>,Integer> temp = index.findPhrase("let slip");
        BufferedWriter writer6 = new BufferedWriter(new FileWriter("phrase2.txt"));
        Set<List<Integer>> keys = temp.keySet();
        for(List<Integer> key2 : keys){
            writer6.write("DocumentID: " + intToDocID.get(key2.get(0)) +" Scene: " +  intToSceneID.get(key2.get(1)) +" Value: " + temp.get(key2)+"\n");
            //System.out.println("DocumentID: " + intToDocID.get(key2.get(0)) +" Scene: " +  intToSceneID.get(key2.get(1)) +" Value: " + temp.get(key2));
        }
        writer6.close();



        

        //testing();

    }
    //Just a test function
    public static void testing() {
        Posting test = new Posting(30,30,30);
        InvertedIndex test2 = new InvertedIndex();
        test2.add("term", 3, 2,3);
        test2.add("term", 4, 2,3);
        test2.add("term", 4, 3,3);
        test2.getPostings("term");
        System.out.println(test2.getFrequency("term"));
        System.out.println(test2.getNumberOfTerms());
        Map<List<Integer>,Integer> temp = test2.getFrequencyByDocumentandScene("fuck");
        Set<List<Integer>> keys = temp.keySet();
        for(List<Integer> key : keys){
            System.out.println("DocumentID: " + key.get(0) +" Scene: " +  key.get(1) +" Value: " + temp.get(key));
        }
    }


    public static <K> Map<K, Integer> sortMap(Map<K, Integer> mapToSort){
        List<Entry<K, Integer>> list = new ArrayList<>(mapToSort.entrySet());
        list.sort(Entry.comparingByValue());
        Map<K, Integer> result = new LinkedHashMap<>();
        for (Entry<K, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
