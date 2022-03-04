import java.util.Map;

public class fatK {
    Double score = 0.0;
    Double totalScore = 0.0;
    Map<Integer, Map<String, Integer>> relevance;
    Map<Integer, Map<String, Integer>> scores;
    int totalQueries = 0;
    int k = 5;
    public fatK(Map<Integer, Map<String, Integer>> relevance, Map<Integer, Map<String, Integer>> scores, int K) {
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
    //Formula is 2rp / R+P
    private double calcScoreIndividualQuery(Integer queryId) {
        mAPk prec = new mAPk(relevance, scores, k);
        recall rec = new recall(relevance, scores, k);
        Double p = prec.calcScoreIndividualQuery(queryId);
        Double r = rec.calcScoreIndividualQuery(queryId);
        if (r + p == 0) {
            return 0.0;
        }
        return 2 * r * p / (r + p);

    }
    public double getScore() {
        return this.score;
    }
}
