import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class recall {
    Double score = 0.0;
    Double totalScore = 0.0;
    Map<Integer, Map<String, Integer>> relevance;
    Map<Integer, Map<String, Integer>> scores;
    int totalQueries = 0;
    Integer k = 0;
    //Relevance first integer is query id
        //Inner map string is docid
        //Second int is 1,2 or 0. 1,2 is relevant 0 is not relevant
    //Scores first int is query id
        //Inner map is a sorted map(Might need to sort it) where string is doc id, and int is rank
    public recall(Map<Integer, Map<String, Integer>> relevance, Map<Integer, Map<String, Integer>> scores, Integer K) {
        this.relevance = relevance;
        this.scores = scores;
        k = K;
        for (Integer x :this.scores.keySet()) {
            totalScore = totalScore + calcScoreIndividualQuery(x);
            totalQueries++;    
        }
        score = totalScore / totalQueries;
    } 
    public double calcScoreIndividualQuery(Integer queryId) {
        Map<String, Integer> rel = this.relevance.get(queryId);
        Map<String, Integer> scor = this.scores.get(queryId);
        scor = sortByValue(scor);
        double sum = 0.0;
        Iterator<String> test =  scor.keySet().iterator();
        for (int i = 0; i < k; i++) {
            if(!test.hasNext()) {
                break;
            }
            String cur = test.next();
            if(rel.containsKey(cur) && rel.get(cur) > 0){
                sum = sum + 1.0;
            }

        }
        Iterator<String> test2 =  rel.keySet().iterator();
        int counter = 0;
        while (test2.hasNext()) {
            if (rel.get(test2.next()) > 0) {
                counter++;
            }
        }
        return sum / counter;
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
