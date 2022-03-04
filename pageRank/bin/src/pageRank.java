import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class pageRank {
    private double lambda = 0.15;
    private double tau = 0.0001;
    public pageRank(double lambda, double tau) {
        this.lambda = lambda;
        this.tau = tau;
    }
    public Map<String,Double> page(Map<String,Set<String>> g){
        Map<String,Double> i = new HashMap<String, Double>();
        Map<String,Double> r = new HashMap<String, Double>();
        Iterator<String> it = g.keySet().iterator();
        //Start with each equally likely
        while(it.hasNext()) {
            i.put(it.next(), 1.0/g.size());
        }
        Boolean converge = false;
        Double toAdd = 0.0;
        //While the norm is greater than tau
        while(!converge){
            Iterator<String>itForR = i.keySet().iterator();
            //Ri ← λ/|P| Reset random values
            while(itForR.hasNext()){
                r.put(itForR.next(), lambda / g.keySet().size());
            }
            toAdd = 0.0;
            //For every page, page, in P
            Iterator<String>page = g.keySet().iterator();
            while(page.hasNext()) {

                String key = page.next();
                Set<String> q = g.get(key);
                q.retainAll(g.keySet());
                //q is all pages in connected to page that are also in P
                //If its size is greater than zero
                if (Math.abs(q.size()) > 0){
                    Iterator<String> temp = q.iterator();
                    //For all pages temp in q
                    while (temp.hasNext()) {
                        String qKey = temp.next();
                        //Line 17
                        Double val = r.get(qKey) + ((1-lambda) * i.get(key) / Math.abs(q.size()));
                        r.put(qKey, val);
                        //Update value of R(q)    
                    }
                } 
                else {
                        toAdd += (1-lambda) * i.get(key)/g.size();
                    
                }              
            }
            //Accumulator
            Iterator<String> allPage = g.keySet().iterator();
            while(allPage.hasNext()){
                String allPageKey = allPage.next();
                Double val = toAdd + r.get(allPageKey);
                r.put(allPageKey, val);   
            }
            //Get the convergence
            converge = converged(i, r);
            //Update the value of I
            Iterator<String> all2Page = g.keySet().iterator();
            while(all2Page.hasNext()){
                String all2PageKey = all2Page.next();
                i.put(all2PageKey, r.get(all2PageKey));
                
            }

        }
        return r;
    }
    private boolean converged(Map<String,Double> i, Map<String,Double> r){
        //Find norm, if its less than tau then true
        Double norm = 0.0;
        Iterator<String> conv = i.keySet().iterator();
        while (conv.hasNext()) {
            String conKey = conv.next();
            norm += Math.abs(i.get(conKey) - r.get(conKey));
        }
        System.out.println("Norm: " + norm);
        if (norm < tau) {
            return true;
        }
        return false;
    }
}