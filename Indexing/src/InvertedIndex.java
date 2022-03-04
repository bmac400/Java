package src;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InvertedIndex {
    
    private Map<String,List<Posting>> index = new HashMap<String,List<Posting>>();
    private int numberOfTerms = 0;

    //Adds term with document id and scene id
    public void add(String term, Integer documentId, int sceneId, int wordPos) {
        if (index.containsKey(term)) {
            List<Posting> temp = index.get(term);
            //Check if theres already a posting with said document id
            //Could loop through and check each one but that would be annoying
            Iterator<Posting> it = temp.iterator();
            Boolean found = false;
            while(it.hasNext()) {
                Posting current = it.next();
                if(current.getDocID() == documentId) {
                    current.addPosition(sceneId,wordPos);
                    found = true;
                    break;
                }
            }
            if (!found) {
                temp.add(new Posting(documentId, sceneId, wordPos));
            }
        }
        else {
            List<Posting> temp = new LinkedList<>();
            temp.add(new Posting(documentId, sceneId, wordPos));
            index.put(term, temp);
            numberOfTerms++;
        }
    }
    public int getNumberOfTerms() {
        return numberOfTerms;
    }
    public Map<String, List<Posting>> getIndex() {
        return index;
    }
    public List<Posting> getPostings(String term) {
        //TODO
        //Probably a error if no term with given input
        return index.get(term);
    }

    //Returns map with Document IDS attached to frequency of word
    public Map<Integer, Integer> getFrequencyByDocument(String term) {
        Map<Integer, Integer> result = new HashMap<>();
        //Returns essentially an empty map if not found. If it is not found returns map with location 0 for 
        if(!index.containsKey(term)) {
            result.put(0, 0);
            return result;
        }
        List<Posting> locations = index.get(term);
        //Size of postings 
        for (Posting post : locations) {
            result.put(post.getDocID(), post.getPostitionSize());
        }

        return result;
    }
    public Map<List<Integer>, Integer> getFrequencyByDocumentandScene(String term) {
        Map<List<Integer>, Integer> result = new HashMap<>();
        //Returns essentially an empty map if not found. If it is not found returns map with location 0 for 
        List<Integer> current = new LinkedList<Integer>();
        current.add(0,0);
        current.add(1,0);
        if(!index.containsKey(term)) {
            result.put(current, 0);
            return result;
        }
        List<Posting> locations = index.get(term);
        //Size of postings 
        for (Posting post : locations) {
            current.set(0,post.getDocID());
            Set<Integer> sceneIds = post.getSceneIDS();
            for(Integer x : sceneIds) {
                current.set(1,x);
                List<Integer> clone = new LinkedList<Integer>(current);
                result.put(clone, post.getPositionsSizeByScene(x));
            }       
        }

        return result;
    }
    public Map<List<Integer>, Integer> findPhrase(String term) {
        Map<List<Integer>, Integer> result = new HashMap<>();
        //Returns essentially an empty map if not found. If it is not found returns map with location 0 for 
        String[] words = term.split(" ");
        List<Integer> current = new LinkedList<Integer>();
        current.add(0,0);
        current.add(1,0);
        for (String test : words) {
            if(!index.containsKey(test)) {
                result.put(current, 0);
                return result;
            }
        }
        List<List<Posting>> toFindInt = new LinkedList<List<Posting>>();
        for(String x: words) {
            toFindInt.add(index.get(x));
        }
        List<Posting> results = intersectingPostings(toFindInt);
        for(Posting x : results) {
            current.add(0,x.getDocID());
            current.add(1,x.getSceneIDS().iterator().next());
            List<Integer> clone = new LinkedList<Integer>(current);
            if(result.containsKey(clone)) {
                result.put(clone, result.get(clone)+1);
            } else {
                result.put(clone, 1);
            }
        }       
        return result;
       
            
    }
   
    private List<Posting> intersectingPostings(List<List<Posting>> L) {
        List<List<List<Integer>>> matchingPostings = new LinkedList<List<List<Integer>>>();
        List<Posting> newPostings = new LinkedList<Posting>();
        //List1 is current node position
        List<Integer>List1 = new LinkedList<Integer>();
        boolean go = true;
        for(int i = 0; i < L.size(); i++) {
            List1.add(0);
            
        }
        int maxDocId = -5;
        while(go){
            //Max over current docid
            matchingPostings = new LinkedList<List<List<Integer>>>();
            //For each list in L skip to canidate docid
            //Find max docid
            for (int i = 0; i < L.size(); i++) {
                if(L.get(i).get(List1.get(i)).getDocID() > maxDocId) {
                    maxDocId = L.get(i).get(List1.get(i)).getDocID();
                }
            }
            //Skip to canidate/max doc id
            for (int i = 0; i < L.size(); i++) {
                if(L.get(i).get(List1.get(i)).getDocID() < maxDocId) {
                    while(L.get(i).get(List1.get(i)).getDocID() < maxDocId && List1.get(i) < L.get(i).size()) {
                        List1.set(i, List1.get(i)+ 1);
                    } 
                    if (List1.get(i) >= L.get(i).size()) {
                        go = false;
                    }
                }
            }
            if(!go) {
                break;
            }
            //if all match between l and canidate
            for (int i = 0; i < L.size(); i++) {
                matchingPostings.add(L.get(i).get(List1.get(i)).getPositions());
            }
            //Find matches in this
            //To find matches find points with same scenes then find sequences of L length in a row.
            
            for(int j = 0; j < matchingPostings.get(0).size(); j++) {
                    boolean found = true;
                    //Get value from first and then check rest
                    List<Integer> temp = matchingPostings.get(0).get(j);
                    //Check for each in the list
                    for (int i = 1; i < matchingPostings.size(); i++){
                        List<Integer> temp2 = new LinkedList<Integer>();
                        temp2.add(0,temp.get(0));
                        temp2.add(1,temp.get(1)+i);
                        boolean found2 = false;
                        //Contains
                        for(int y = 0; y < matchingPostings.get(i).size(); y++) {
                            if (matchingPostings.get(i).get(y).get(0).equals(temp2.get(0)) && matchingPostings.get(i).get(y).get(1).equals(temp2.get(1))) {
                                found2 = true;
                                break;
                            } 
                        }
                        found = found && found2;
                    }
                    if(found) {
                        newPostings.add(new Posting(maxDocId, matchingPostings.get(0).get(j).get(0),matchingPostings.get(0).get(j).get(1)));
                    }

                }
            


            for (int i = 0; i < L.size(); i++) {
                List1.set(i, List1.get(i)+1);
                if(L.get(i).size() <= List1.get(i)){
                    go = false;
                    break;
                        
                }

                }
            }
        
        return newPostings;


    }
}
