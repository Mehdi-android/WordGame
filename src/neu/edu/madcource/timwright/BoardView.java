package neu.edu.madcource.timwright;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {
    
    private final BananagramGame bgame;
    
    private static final int NUM_TILES_ACROSS = 15;
    public static final int NUM_TILES_VISIBLE = 9;
    private final String FIRST_INSTRUCTION = "Press 'Split' to Begin!";
    private final String SECOND_INSTRUCTION_L1 = "Tap a letter tile to select,";
    private final String SECOND_INSTRUCTION_L2 = "then tap the board!";
    
    private float boardwidth;
    private float boardheight;
    float tilewidth;
    float tileheight;
    private Paint background;
    private Paint hilite;
    private Paint light;
    private Paint tutorialwordpaint;
    private String tutorial1;
    private String tutorial2;

    
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.bgame = (BananagramGame) context;

        
        background = new Paint();
        background.setColor(getResources().getColor(
              R.color.board_background));
        tutorialwordpaint = new Paint();
        tutorialwordpaint.setColor(getResources().getColor(R.color.board_tutorial_words));
        tutorialwordpaint.setStyle(Style.FILL);
        tutorialwordpaint.setTextAlign(Paint.Align.CENTER);
        hilite = new Paint();
        hilite.setColor(getResources().getColor(R.color.board_grid_light));
        light = new Paint();
        light.setColor(getResources().getColor(R.color.board_grid_dark));
        tutorial1 = FIRST_INSTRUCTION;
        tutorial2 = "";
        invalidate();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
       this.boardwidth = w;
       this.boardheight = h;
       this.tilewidth = w / NUM_TILES_VISIBLE;
       this.tileheight = h / NUM_TILES_VISIBLE;
       

       tutorialwordpaint.setTextSize(tileheight);
       
       super.onSizeChanged(w, h, oldw, oldh);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
       if (event.getAction() != MotionEvent.ACTION_DOWN)
          return super.onTouchEvent(event);

       bgame.selectBoard(event.getX(), event.getY(), tilewidth, tileheight);
       return true;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the background...
        
        canvas.drawRect(0, 0, boardwidth, boardheight, background);
        
        // Draw the grid lines
        for (int i = 0; i < NUM_TILES_VISIBLE; i++) {
           canvas.drawLine(0, i * tileheight, boardwidth, i * tileheight,
                 light);
           canvas.drawLine(0, i * tileheight + 1, boardwidth, i * tileheight
                 + 1, hilite);
           canvas.drawLine(i * tilewidth, 0, i * tilewidth, boardheight,
                 light);
           canvas.drawLine(i * tilewidth + 1, 0, i * tilewidth + 1,
                   boardheight, hilite);
        }
        
        canvas.drawText(tutorial1, boardwidth/2, boardheight/2, tutorialwordpaint);
        canvas.drawText(tutorial2, boardwidth/2, boardheight/2 + tileheight, tutorialwordpaint);

        // Draw the tiles placed on the board

        LetterHeap lh = bgame.getPlacedTiles();
        for (int i = 0; i < lh.size(); i++) {
            lh.get(i).drawTile(canvas);
        }

    }
    
    public float getTileHeight() {
        return this.tileheight;
    }
    
    public float getTileWidth() {
        return this.tilewidth;
    }
    
    public void tutorialNext() {
        if (this.tutorial1.equals(FIRST_INSTRUCTION)) {
            this.tutorial1 = SECOND_INSTRUCTION_L1;
            this.tutorial2 = SECOND_INSTRUCTION_L2;
        }
        else if (this.tutorial1.equals(SECOND_INSTRUCTION_L1)) {
            this.tutorial1 = "";
            this.tutorial2 = "";
        }
        else {
            
            
        }
    }
}
