import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class mAPk {
    Double score = 0.0;
    Double totalScore = 0.0;
    Map<Integer, Map<String, Integer>> relevance;
    Map<Integer, Map<String, Integer>> scores;
    int totalQueries = 0;
    int k = 5;
    public mAPk(Map<Integer, Map<String, Integer>> relevance, Map<Integer, Map<String, Integer>> scores, int K) {
        this.relevance = relevance;
        this.scores = scores;
        this.k = K;
        for (Integer x:this.scores.keySet()) {
            totalScore = totalScore + calcScoreIndividualQuery(x);
            totalQueries++;

        }
        score = totalScore / totalQueries;
    } 
    //Average Precision of query, QueryID
    public double calcScoreIndividualQuery(Integer queryId) {
        
        Map<String, Integer> rel = this.relevance.get(queryId);
        Map<String, Integer> scor = this.scores.get(queryId);
        
        double relDoc = 0.0;
        scor = sortByValue(scor);
        double count = 0.0;
        Iterator<String> test =  scor.keySet().iterator();
        for (int i = 0; i < k; i++) {
            String test2;
            if(test.hasNext()) {
                test2 = test.next();
            }
            else {
                break;
            }
            if(rel.containsKey(test2)) {
                if(rel.get(test2) > 0) {
                    relDoc = relDoc + 1.0;
                    
                }
            }
        }
        return relDoc / k;

    }
    public double getScore() {
        return score;
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
