package neu.edu.madcource.timwright;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class WordGame extends Activity implements OnClickListener {
    
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String SAVED_INSTANCE_KEY = "saved_instance_key";
    public static final String SAVED_GAME_STARTED = "saved_game_started";
    
    boolean playmusic = true;
    boolean savedgame = false;
    private final int bg_continue_button = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordgame);
        
        View newButton = findViewById(R.id.bg_start_button);
        newButton.setOnClickListener(this);
        View acknowButton = findViewById(R.id.bg_acknowledgements_button);
        acknowButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.bg_exit_button);
        exitButton.setOnClickListener(this);
        View musicButton = findViewById(R.id.bg_music_button);
        musicButton.setOnClickListener(this);
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        this.savedgame = settings.getBoolean(SAVED_GAME_STARTED, false);
        
        if (this.savedgame) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.word_game_start_screen);
            Button continueButton = new Button(this);
            continueButton.setText("Continue");
            continueButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            continueButton.setId(bg_continue_button);
            layout.addView(continueButton, 1);
            continueButton.setOnClickListener(this);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (this.playmusic){ 
            Music.play(this, R.raw.bananaphone);
        }
     }
    
    @Override
    protected void onPause() {
       super.onPause();
       Music.stop(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case bg_continue_button:
            newGame(true);
            break;
        case R.id.bg_start_button:
           newGame(false);
           break;
           // ...
        case R.id.bg_acknowledgements_button:
            Intent ack = new Intent(this, BGAcknow.class);
            startActivity(ack);
            break;
        case R.id.bg_music_button:
            this.playmusic = !this.playmusic;
            if (this.playmusic)
                Music.play(this, R.raw.bananaphone);
            else
                Music.stop(this);
            break;
        case R.id.bg_exit_button:
           finish();
           break;
        }
     }
    private void newGame(boolean saved) {
        if (saved) {
            startGame(0, saved);
            return;
        }
        new AlertDialog.Builder(this)
        .setTitle(R.string.bg_pile_size_select)
        .setItems(R.array.pile_size_arr,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface,
                    int i) {
                startGame(i, false);
            }
        })
        .show();    
    }
    
    private void startGame(int i, boolean saved) {
        Intent intent = new Intent(this, LoadingScreen.class);
        intent.putExtra(BananagramGame.PILE_SIZE, i);
        intent.putExtra(BananagramGame.SAVED_GAME, saved);
        startActivity(intent);
    }
}
