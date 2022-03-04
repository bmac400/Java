import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        //Build Relevance Map
        Map<Integer, Map<String, Integer>> relevance = new HashMap<Integer, Map<String, Integer>>();
        File qrel = new File("qrels");
        BufferedReader br = new BufferedReader(new FileReader(qrel));
        String line;
        while((line = br.readLine())!= null) {
            String[] lineSplit = line.split(" ");
            Integer queryId = Integer.parseInt(lineSplit[0]);
            if(relevance.containsKey(queryId)) {
                relevance.get(queryId).put(lineSplit[2], Integer.parseInt(lineSplit[3]));
            } else {
                Map<String, Integer> newMap = new HashMap<String, Integer>();
                newMap.put(lineSplit[2], Integer.parseInt(lineSplit[3]));
                relevance.put(queryId, newMap);
            }

        }
        //Build score map
        String[] fileNames = new String[4];
        fileNames[0] = "stress.trecrun";
        fileNames[1] = "bm25.trecrun";
        fileNames[2] = "sdm.trecrun";
        fileNames[3] = "ql.trecrun";
        File output = new File("output.metrics");
        BufferedWriter  writer = new BufferedWriter(new FileWriter(output));
        for(int i = 0; i < fileNames.length; i++) {
            Map<Integer, Map<String,Integer>> scoreMap = buildScoresMap(fileNames[i]);
            MRR test = new MRR(relevance, scoreMap);
            String testOut = String.format("%-15s %-10s %-12s \n", fileNames[i],"MRR",test.getScore());
            mAPk test2 = new mAPk(relevance,scoreMap, 5);
            String test2Out = String.format("%-15s %-10s %-12s \n", fileNames[i],"P@5",test2.getScore());
            mAPk test3 = new mAPk(relevance,scoreMap, 20);
            String test3Out = String.format("%-15s %-10s %-12s \n", fileNames[i],"P@20",test3.getScore());
            NDCG test4 = new NDCG(relevance, scoreMap, 10);
            String test4Out = String.format("%-15s %-10s %-12s \n", fileNames[i],"NDCG@10",test4.getScore());
            recall test5 = new recall(relevance, scoreMap, 20);
            String test5Out = String.format("%-15s %-10s %-12s \n", fileNames[i],"Recall@20",test5.getScore());
            fatK test6 = new fatK(relevance, scoreMap, 20);
            String test6Out = String.format("%-15s %-10s %-12s \n", fileNames[i],"F1@20",test6.getScore());
            MeanAP test7 = new MeanAP(relevance, scoreMap);
            String test7Out = String.format("%-15s %-10s %-12s \n", fileNames[i],"MAP",test7.getScore());
            writer.write(testOut);
            writer.write(test2Out);
            writer.write(test3Out);
            writer.write(test4Out);
            writer.write(test5Out);
            writer.write(test6Out);
            writer.write(test7Out);
        }
        writer.close();
        //Horribly Inneficient way to get data for graph
        File recall = new File("recall");
        BufferedWriter  writer2 = new BufferedWriter(new FileWriter(recall));
        File prec = new File("prec");
        BufferedWriter  writer3 = new BufferedWriter(new FileWriter(prec));
        for (int j = 1; j < 4; j++){
            Map<Integer, Map<String,Integer>> scores = buildScoresMap(fileNames[j]);
            for(int i = 1; i <= 20; i++) {
                mAPk oops = new mAPk(relevance, scores, i);
                writer2.write(oops.calcScoreIndividualQuery(450) + "\n");
                recall oops2 = new recall(relevance, scores, i);
                writer3.write(oops2.calcScoreIndividualQuery(450) + "\n");

            }
            writer2.write("End of" + fileNames[j]+"\n");
            writer3.write("End of" + fileNames[j]+"\n");
        }
        writer2.close();
        writer3.close();
    }
    public static Map<Integer, Map<String, Integer>> buildScoresMap (String fileName) throws Exception {
        Map<Integer, Map<String, Integer>> scores = new HashMap<Integer, Map<String, Integer>>();
        File scor = new File(fileName);
        BufferedReader sbr = new BufferedReader(new FileReader(scor));
        String sline;
        while((sline = sbr.readLine())!= null) {
            String[] lineSplit = sline.split(" ");
            Integer queryId = Integer.parseInt(lineSplit[0]);
            if(scores.containsKey(queryId)) {
                scores.get(queryId).put(lineSplit[2], Integer.parseInt(lineSplit[3]));
            } else {
                Map<String, Integer> newMap = new HashMap<String, Integer>();
                newMap.put(lineSplit[2], Integer.parseInt(lineSplit[3]));
                scores.put(queryId, newMap);
            }
        }
        sbr.close();
        return scores;
    }
}
