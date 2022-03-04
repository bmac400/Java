package src;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Posting {
    private int DocID;
    private List<List<Integer>> positions;
    
    //Doc id is document id, and posit is 2 long linked list because arrays dont hash with scene number in 0 pos and 1 is word position
    public Posting(int DocID, int sceneId, int wordPos) {
        this.positions = new LinkedList<List<Integer>>();
        List<Integer> why = new LinkedList<Integer>();
        why.add(0, sceneId);
        why.add(1,wordPos);
        this.positions.add(why);
        this.DocID = DocID;
    }
    public void addPosition(int sceneId, int wordPos) {
        List<Integer> why = new LinkedList<Integer>();
        why.add(0, sceneId);
        why.add(1,wordPos);
        this.positions.add(why);
    }
    public int getDocID() {
        return DocID;
    } 
    public Set<Integer> getSceneIDS() {
        Set<Integer> result = new HashSet<>();
        for (List<Integer> pos : positions){
            result.add(pos.get(0));
        }
        return result;
    }
    public List<List<Integer>> getPositions(){
        return positions;
    }
    public int getPositionsSizeByScene(int sceneIdNum) {
        int total = 0;
        Iterator<List<Integer>> it = positions.iterator();
        while (it.hasNext()) {
            List<Integer> current = it.next();
            if(sceneIdNum == current.get(0)) {
                total++;
            }
        }
        return total;
    }
    public int getPostitionSize(){
        return positions.size();
    }
    //Removes given scene ids
    public void keepSceneIds(Set<Integer> sceneIDSET) {
        for (int i = 0; i < positions.size(); i++) {
            if(!sceneIDSET.contains(positions.get(i).get(0))){
                positions.remove(i);
            }
        }
    }
    

}