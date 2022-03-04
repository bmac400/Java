
import static java.util.Map.Entry.comparingByValue;
import static java.util.Comparator.comparingInt;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class App {
    public static void main(String[] args) throws Exception {
        double lambda = 0.15;
        double tau = 0.0001;
        String filepath = "C:/Users/bryan/VSCode/pageRank/links.srt.gz";
        Path source = Paths.get(filepath);
        GZIPInputStream gis = new GZIPInputStream(new FileInputStream(source.toFile()));
        InputStreamReader tes = new InputStreamReader(gis,"UTF-8");
        BufferedReader buf = new BufferedReader(tes);
        
        Map<String,Set<String>> g = new HashMap<String,Set<String>>();
        String t = "";
        while (buf.read() != -1) {
            
            t = buf.readLine();
            String[] buffer = t.split("\t");
            if(!g.containsKey(buffer[0])) {
                Set<String> temp = new HashSet<String>();
                temp.add(buffer[1]);
                g.put(buffer[0], temp);
            } else {
                g.get(buffer[0]).add(buffer[1]);
            }
            if (g.containsKey(buffer[1])) {
                g.get(buffer[1]).add(buffer[0]);
            } else {
                Set<String> temp = new HashSet<String>();
                temp.add(buffer[0]);
                g.put(buffer[1], temp);
            }
        }
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("Output2.txt"));
        

        Stream<Entry<String, Set<String>>> valtoReturn = g.entrySet().stream()
        .sorted(comparingByValue(comparingInt(Set::size))).skip(g.size()-75);
        List<Entry<String, Set<String>>> out = new ArrayList<Map.Entry<String,Set<String>>>();
        valtoReturn.forEach(out::add);
        for (int i = out.size()-1; i > out.size()-76; i--) {
            String output = "Key: " + out.get(i).getKey() + " " + " Linkgs: " + out.get(i).getValue().size() + "\n";
            writer2.write(output);
        }
            
        
        pageRank test = new pageRank(lambda, tau);
        writer2.close();
        Map<String, Double> val = test.page(g);

        List<Entry<String, Double>> list = new ArrayList<>(val.entrySet());
        list.sort(Entry.comparingByValue());
        BufferedWriter writer = new BufferedWriter(new FileWriter("Output.txt"));
        Iterator<Map.Entry<String,Double>> write = list.iterator();
        while(write.hasNext()) {
            Map.Entry<String,Double> thingToAdd = write.next();
            String writeString = thingToAdd.getKey() + "\t" + thingToAdd.getValue() + "\n";
            writer.write(writeString);
            
        }
        writer.close();
        
        
        
        
    }
    public static String outputfunc(Entry<String, Set<String>> out){
        return "" + out.getKey() + "\t" + out.getValue().size();
    }
}
