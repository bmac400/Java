import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MRR {
    Double score = 0.0;
    Double totalScore = 0.0;
    Map<Integer, Map<String, Integer>> relevance;
    Map<Integer, Map<String, Integer>> scores;
    int totalQueries = 0;
    //Relevance first integer is query id
        //Inner map string is docid
        //Second int is 1 or 0. 1 is relevant 0 is not relevant
    //Scores first int is query id
        //Inner map is a sorted map(Might need to sort it) where string is doc id, and int is rank
    public MRR(Map<Integer, Map<String, Integer>> relevance, Map<Integer, Map<String, Integer>> scores) {
        this.relevance = relevance;
        this.scores = scores;
        for (Integer x :this.scores.keySet()) {
            totalScore = totalScore + calcScoreIndividualQuery(x);
            totalQueries++;    
        }
        score = totalScore / totalQueries;
    } 
    private double calcScoreIndividualQuery(Integer queryId) {
        Map<String, Integer> rel = this.relevance.get(queryId);
        Map<String, Integer> scor = this.scores.get(queryId);
        scor = sortByValue(scor);
        int count = 0;
        for (String x:scor.keySet()){
            count++;

            if(rel.containsKey(x)) {
                if(rel.get(x) > 0) {
                    return 1.0/count;
                }
            }
        }

        return 0.0;

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
