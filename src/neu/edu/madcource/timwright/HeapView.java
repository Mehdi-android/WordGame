package neu.edu.madcource.timwright;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HeapView extends View {
    
    private final BananagramGame bgame;
    public static final int NUM_TILES_ACROSS = 15;
    public static final int NUM_TILES_VISIBLE = 9;
    
    private float boardwidth;
    private float boardheight;
    private float tilewidth;
    private float tileheight;
    
    private Rect heaprect;
    private Rect dumprect;
    private Rect pauserect;
    private Rect scorerect;
    private Rect uprect;
    private Rect downrect;
    private Rect leftrect;
    private Rect rightrect;
    private Paint backgroundpaint;
    
    LetterTile heaplt;
    LetterTile dumplt;
    LetterTile pauselt;
    LetterTile scorelt;
    LetterTile uplt;
    LetterTile downlt;
    LetterTile leftlt;
    LetterTile rightlt;
    
    
    Bitmap tilebm;
    
    private final int TILE_WIDTH_PROPORTION = 10;
    private final int TILE_HEIGHT_PROPORTION = 5;
    
    public HeapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.bgame = (BananagramGame) context;
        tilebm = BitmapFactory.decodeResource(getResources(), R.drawable.blank_tile); 
        backgroundpaint = new Paint(); // set custom, move to constructor
        backgroundpaint.setColor(getResources().getColor(
              R.color.heap_background));
        
        invalidate();
    }
    
    public float getTileHeight() {
        return this.tileheight;
    }
    
    public float getTileWidth() {
        return this.tilewidth;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
       if (event.getAction() != MotionEvent.ACTION_DOWN)
          return super.onTouchEvent(event);
       float tapx = event.getX();
       float tapy = event.getY();
       
       if (tapx >= tilewidth*3 && tapx <= boardwidth &&
               tapy >= tileheight && tapy <= boardheight) { // The user pressed a tile
           bgame.selectTile(tapx, tapy, tilewidth, tileheight);
           return true;
       }
       else if (tapx >= dumprect.left && tapx <= dumprect.right &&
               tapy >= dumprect.top && tapy <= dumprect.bottom) { // The user pressed the dump button
           bgame.dump(tilewidth, tileheight);
           return true;
       } 
       else if (tapx >= heaprect.left && tapx <= heaprect.right &&
               tapy >= heaprect.top && tapy <= heaprect.bottom) { // The user pressed heap
           if (heaplt.getLetter().equals("Split!")) {
               bgame.split();
               return true;
           }
           return true;
       }
       else if (tapx >= pauserect.left && tapx <= pauserect.right &&
               tapy >= pauserect.top && tapy <= pauserect.bottom) { // The user pressed pause
           bgame.pauseButton();
       } 
       else if (tapx >= leftrect.left && tapx <= leftrect.right &&
               tapy >= leftrect.top && tapy <= leftrect.bottom) { // The user pressed left
           bgame.moveTiles(1, false);
       } 
       else if (tapx >= uprect.left && tapx <= uprect.right &&
               tapy >= uprect.top && tapy <= uprect.bottom) { // The user pressed up
           bgame.moveTiles(1, true);
       } 
       else if (tapx >= downrect.left && tapx <= downrect.right &&
               tapy >= downrect.top && tapy <= downrect.bottom) { // The user pressed down
           bgame.moveTiles(-1, true);
       } 
       else if (tapx >= rightrect.left && tapx <= rightrect.right &&
               tapy >= rightrect.top && tapy <= rightrect.bottom) { // The user pressed right
           bgame.moveTiles(-1, false);
       } 

       return true;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
       this.boardwidth = w;
       this.boardheight = h;
       this.tilewidth = w / TILE_WIDTH_PROPORTION;
       this.tileheight = h / TILE_HEIGHT_PROPORTION;

       this.heaprect = new Rect(0, (int)tileheight, (int)(2*tilewidth), (int)(2*tileheight));
       this.dumprect = new Rect(0, (int)(3*tileheight), (int)(2*tilewidth), (int)(4*tileheight));
       this.pauserect = new Rect((int)tilewidth*8, 0, (int)tilewidth*10, (int)tileheight);
       this.scorerect = new Rect(0, 0, (int)tilewidth*3, (int)tileheight);
       this.leftrect = new Rect((int)tilewidth*4, 0, (int)tilewidth*5, (int)tileheight);
       this.downrect = new Rect((int)tilewidth*5, 0, (int)tilewidth*6, (int)tileheight);
       this.uprect = new Rect((int)tilewidth*6, 0, (int)tilewidth*7, (int)tileheight);
       this.rightrect = new Rect((int)tilewidth*7, 0, (int)tilewidth*8, (int)tileheight);
       
       heaplt = new LetterTile(this.bgame, "Split!", heaprect);
       dumplt = new LetterTile(this.bgame, "Dump", dumprect);
       pauselt = new LetterTile(this.bgame, "Pause", pauserect);
       scorelt = new LetterTile(this.bgame, "Score: 0", scorerect);
       leftlt = new LetterTile(this.bgame, "<", leftrect);
       downlt = new LetterTile(this.bgame, "v", downrect);
       uplt = new LetterTile(this.bgame, "^", uprect);
       rightlt = new LetterTile(this.bgame, ">", rightrect);
       
       super.onSizeChanged(w, h, oldw, oldh);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        
        // Draw the background
        canvas.drawRect(0, 0, boardwidth, boardheight, backgroundpaint);
        // Draw the buttons
        heaplt.drawTile(canvas);
        dumplt.drawTile(canvas);
        pauselt.drawTile(canvas);
        scorelt.drawTile(canvas);
        leftlt.drawTile(canvas);
        downlt.drawTile(canvas);
        uplt.drawTile(canvas);
        rightlt.drawTile(canvas);
        // Draw the tiles
        /*
        LetterHeap lh = bgame.getUser();
        for (int i = 0; i < lh.size(); i++) {
            lh.get(i).drawTile(canvas);
        }
        */
        for (int i=0; i<7; i++) {
            for (int j=0; j<4; j++) {
                bgame.drawUserHeapAtCoord(i,j, canvas);
            }
        }
    }
    public void changeTimerText(String s) {
        if (this.heaplt != null) {
            this.heaplt.changeText(s);
            invalidate();
        }
    }
    public void updateScore(int s) {
        this.scorelt.changeText("Score: " + s);
        invalidate();
    }
}
