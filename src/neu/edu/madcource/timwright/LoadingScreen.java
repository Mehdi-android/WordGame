package neu.edu.madcource.timwright;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoadingScreen extends Activity {

    private static int LOADING_TIME = 1500; // masks the long delay while Dictionary loads
    public static final String PILE_SIZE = "neu.edu.madcource.timwright.pilesize";
    public static final String SAVED_GAME = "neu.edu.madcource.timwright.savedgame";
    public static final int PILE_SIZE_SMALL = 11;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        
        Thread background = new Thread() {
            public void run() {
            
            try {
                sleep(LOADING_TIME);
                Intent intent = new Intent(getBaseContext(), BananagramGame.class);
                intent.putExtra(BananagramGame.PILE_SIZE, getIntent().getIntExtra(PILE_SIZE, PILE_SIZE_SMALL));
                intent.putExtra(BananagramGame.SAVED_GAME, getIntent().getBooleanExtra(SAVED_GAME, false));
                startActivity(intent);
                
                finish();
            }
            catch (Exception e) {
                
            }
        }
    };
    background.start();
    }
}
