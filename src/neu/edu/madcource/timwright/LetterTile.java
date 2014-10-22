package neu.edu.madcource.timwright;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;

public class LetterTile {
    
    NinePatchDrawable tilepatchdraw;
    Bitmap tilebm;

    private String letter;
    
    public Rect r;
    private Paint p;
    private Paint pblue;
    private Paint pred;
    private boolean blue = false;
    public boolean red = false;
    
    public LetterTile(Context c, String l, Rect r) {
        this.letter = l;
        
        tilebm = BitmapFactory.decodeResource(c.getResources(), R.drawable.blank_tile); 
        byte[] chunk = tilebm.getNinePatchChunk();
        tilepatchdraw = new NinePatchDrawable(c.getResources(), tilebm, chunk, new Rect(), null);
        
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(c.getResources().getColor(R.color.puzzle_foreground));
        p.setStyle(Style.FILL);
        
        pblue = new Paint();
        pblue.setColor(c.getResources().getColor(R.color.tile_selected_transparent));
        
        pred = new Paint();
        pred.setColor(c.getResources().getColor(R.color.tile_notaword_transparent));
        
        if (r != null) {
            this.r = r;
            tilepatchdraw.setBounds(r);
            p.setTextSize(r.height() * 0.60f);
            //p.setTextScaleX(r.width() / r.height());
            p.setTextAlign(Paint.Align.CENTER);
        }
    }
    
    public String letter() {
        return this.letter;
    }
    
    public void changeColorToRed() {
        red = true;
    }
    
    public void changeColorToBlue() {
        blue = true;
    }
    
    public void changeColorToNotRed() {
        red = false;
    }
    
    public void changeColorToNotBlue() {
        blue = false;
    }
    
    public LetterTile place(Rect r) {
        this.r = r;
        tilepatchdraw.setBounds(r);
        p.setTextSize(r.height() * 0.60f);
        p.setTextAlign(Paint.Align.CENTER);
        return this;
    }
    
    public String getLetter() {
        return this.letter;
    }
    
    protected void drawTile(Canvas canvas) {
        if (this.r == null) {
            canvas.drawText("ERROR: No dimension given", 25, 25, this.p);
        }
        else {
            tilepatchdraw.draw(canvas);
            canvas.drawText(this.letter, r.centerX(), (r.bottom + r.centerY()) / 2, this.p);
            if (this.blue) {
                canvas.drawRect(r, pblue);
            }
            if (this.red) {
                canvas.drawRect(r, pred);
            }
        }
    }
    
    public float getLeft() {
        if (r != null) {
            return this.r.left;
        }
        return -1;
    }
    public float getTop() {
        if (r != null) {
            return this.r.top;
        }
        return -1;
    }
    public float getRight() {
        if (r != null) {
            return this.r.right;
        }
        return -1;
    }
    public float getBottom() {
        if (r != null) {
            return this.r.bottom;
        }
        return -1;
    }
    public void changeText(String s) {
        this.letter = s;
    }
    /**
     * Move the tile vertically, up or down
     * @param th
     * @param dir can only be 1 or -1
     */
    public void moveTileVertical(int th, int dir) {
        Rect r = new Rect(this.r.left, this.r.top + th*dir, this.r.right, this.r.bottom + th*dir);
        this.r = r;
        tilepatchdraw.setBounds(r);
    }
    /**
     * Move the tile vertically, up or down
     * @param th
     * @param dir can only be 1 or -1
     */
    public void moveTileHorizontal(int tw, int dir) {
        Rect r = new Rect(this.r.left + tw*dir, this.r.top, this.r.right + tw*dir, this.r.bottom);
        this.r = r;
        tilepatchdraw.setBounds(r);
    }
    
    @Override
    public String toString() {
        return this.letter + "\n" + this.r.left +
                "\n" + this.r.top + "\n" +
                this.r.right + "\n" + this.r.bottom + "\n";
    }
}
