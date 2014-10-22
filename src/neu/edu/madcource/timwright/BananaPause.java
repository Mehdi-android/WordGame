package neu.edu.madcource.timwright;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class BananaPause extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banana_pause);
    }

    public void resumeGame(View view) {
        finish();
    }
    public void quitGame(View view) {
        setResult(2);
        finish();
    }
    
}
