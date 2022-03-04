import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MeanAP {
    Double score = 0.0;
    Double totalScore = 0.0;
    Map<Integer, Map<String, Integer>> relevance;
    Map<Integer, Map<String, Integer>> scores;
    int totalQueries = 0;
    public MeanAP(Map<Integer, Map<String, Integer>> relevance, Map<Integer, Map<String, Integer>> scores) {
        this.relevance = relevance;
        this.scores = scores;
        for (Integer x :this.scores.keySet()) {
            totalScore = totalScore + calcScoreIndividualQuery(x);
            totalQueries++;    
        }
        score = totalScore / totalQueries;
    }
    private Double calcScoreIndividualQuery(Integer queryId) {
        Map<String, Integer> rel = this.relevance.get(queryId);
        Map<String, Integer> scor = this.scores.get(queryId);
        double ap = 0.0;
        double relDoc = 0.0;
        scor = sortByValue(scor);
        double count = 0.0;
        Iterator<String> test =  scor.keySet().iterator();
        for (int i = 0; i < scor.size(); i++) {
            String test2 = test.next();
            count++;
            if(rel.containsKey(test2)) {
                if(rel.get(test2) > 0) {
                    relDoc = relDoc + 1.0;
                    ap = ap + relDoc / (count);
                }
            }

        }
        Iterator<String> test2 =  rel.keySet().iterator();
        int counter = 0;
        while (test2.hasNext()) {
            if (rel.get(test2.next()) > 0) {
                counter++;
            }
        }
        if (relDoc == 0) {
            return 0.0;
        }
        return ap / counter;
    } 
    public double getScore() {
        return this.score;
    }
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
