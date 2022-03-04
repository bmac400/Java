import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NDCG {
    Double score = 0.0;
    Double totalScore = 0.0;
    Map<Integer, Map<String, Integer>> relevance;
    Map<Integer, Map<String, Integer>> scores;
    int totalQueries = 0;
    int k = 5;
    public NDCG(Map<Integer, Map<String, Integer>> relevance, Map<Integer, Map<String, Integer>> scores, int K) {
        this.relevance = relevance;
        this.scores = scores;
        this.k = K;
        //IDCG
        //DCG
        for (Integer x:this.scores.keySet()) {
            totalScore = totalScore + calcScoreIndividualQuery(x);
            totalQueries++;
        }
        score = totalScore / totalQueries;
    } 
    private double calcScoreIndividualQuery(Integer queryId) {
        //IDCG
        Double idcg = 0.0;
        Map<String, Integer> sortRelev = sortByValue(relevance.get(queryId));
        List<String> temp2 = new ArrayList(sortRelev.keySet());
        Collections.sort(temp2, Collections.reverseOrder());
        Set<String> resultSet2 = new LinkedHashSet<String>(temp2);
        Iterator<String> test =  resultSet2.iterator();
        String current = test.next();
        idcg = sortRelev.get(current) + 0.0;
        for (int i = 1; i < k; i++) {
            current = test.next();
            idcg = idcg + sortRelev.get(current) / (Math.log(i+1) / Math.log(2));
        }
        Map<String, Integer> sortScore = sortByValue(scores.get(queryId));
        Double dcg = 0.0;
        List<String> temp = new ArrayList<String>(sortScore.keySet());
        Collections.sort(temp, Collections.reverseOrder());
        Set<String> resultSet = new LinkedHashSet<String>(temp);
        Iterator<String> scoreIT = resultSet.iterator();
        String currentScore = scoreIT.next();
        if (sortRelev.containsKey(currentScore)) {
            dcg = sortRelev.get(currentScore) + 0.0;
        }
        //If idcg == 0 return 0.0 so no divide by zero issues
        if (idcg == 0.0) {
            return 0.0;
        }
        //Get NDCG
        for (int i = 1; i < k; i++) {
            if(!scoreIT.hasNext()) {
                break;
            }
            currentScore = scoreIT.next();
            if(sortRelev.containsKey(currentScore)) {
                dcg = dcg + sortRelev.get(currentScore) / (Math.log(i+1) / Math.log(2));
        
            }
        }
        return dcg / idcg;
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
