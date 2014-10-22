package neu.edu.madcource.timwright;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;


public class DictionaryMemory {

    private List<Long> longs;
    private BufferedReader reader;
    public Context cntxt;
    final private String key = "abcdefghijklmnopqrstuvwxyz";
    
    /**
     * Constructor for the class. Calls readLines with the wordlist.txt
     * to initialize the list of longs; takes a very long time
     * @param context
     */
    public DictionaryMemory(Context context) {
        cntxt = context;
        longs = new ArrayList<Long>();
        this.readLines("wordlist.txt");
    }
    /**
     * Parses through the input file. Each word is converted to a unique long
     * and added to the list of longs.
     * @param filename
     */
    private void readLines(String filename) {
        try {
            AssetManager assets = cntxt.getAssets();
            reader = new BufferedReader(new InputStreamReader(assets.open(filename)));
            while(true) {
                String s = reader.readLine();
                if (s == null) {
                    break;
                }
                longs.add(stringToLong(s));
            }
        }
        catch(IOException e) {
            longs.add(stringToLong("readfail"));
        }
    }
    /**
     * Converts a String to a unique long. Strongs must
     * be all lowercase letters, a-z only.
     * @param s the string
     * @return long value
     */
    private long stringToLong(String s) {

        long longstr = 0;
        for (char c : s.toCharArray()) {
            longstr *= 32;
            longstr += this.key.indexOf(c)+1;
        }
        return longstr;
    }
    /**
     * Checks if the given string is in the list of longs
     * by changing the string to a long, then using Java's
     * built-in contains function.
     * @param s
     * @return
     */
    public boolean isWordPresent(String s) {
        return longs.contains(stringToLong(s));
    }
        
}