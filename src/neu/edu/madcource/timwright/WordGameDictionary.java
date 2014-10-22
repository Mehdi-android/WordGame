package neu.edu.madcource.timwright;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class WordGameDictionary extends Service {
    
    private List<Long> longs;
    private Context cntxt;
    private BufferedReader reader;
    final private String key = "abcdefghijklmnopqrstuvwxyz";
    
    public WordGameDictionary(Context context) {
        this.cntxt = context;
    }
    
    public int onStartCommand (Intent intent, int flags, int startId) {
        longs = new ArrayList<Long>();
        this.readLines("wordlist.txt");
        return 0;
    }
    
    /**
     * Parses through the input file. Each word is converted to a unique long
     * and added to the list of longs.
     * @param filename
     */
    private void readLines(String filename) {
        try {
            long longstr;
            reader = new BufferedReader(new InputStreamReader(cntxt.getAssets().open(filename)));
            while(true) {
                String s = reader.readLine();
                if (s == null) {
                    break;
                }
                longstr = 0;
                for (char c : s.toCharArray()) {
                    longstr *= 32;
                    longstr += this.key.indexOf(c)+1;
                }
                longs.add(longstr);
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
    
    public IBinder onBind (Intent intent) {
        return null;
    }

}
