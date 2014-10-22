package neu.edu.madcource.timwright;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class LetterHeap {
    
    private final int NUM_INDICATOR_RADIUS = 12;
    private Paint numindicatorpaint;
    private Paint numpaint;
    private ArrayList<LetterTile> letters;
    private String initializelist;
    
    public LetterHeap(Context c, String s) {
        this.initializelist = s;
        letters = new ArrayList<LetterTile>();
        for(int i=0; i<initializelist.length()-1; i++) {
            letters.add(new LetterTile(c, initializelist.substring(i,i+1), null));
        }
        numindicatorpaint = new Paint();
        numindicatorpaint.setColor(c.getResources().getColor(R.color.heap_num_indicator));
        numpaint = new Paint();
        numpaint.setColor(c.getResources().getColor(R.color.banana_letter));
        numpaint.setTextSize((float)(NUM_INDICATOR_RADIUS*1.2));
        numpaint.setTextAlign(Paint.Align.CENTER);
    }
    
    /**
     * A random LetterTile from the list is selected and returned.
     * The LetterTile is removed from the list.
     * Returns NULL if the generator gave an invalid index.
     * @return
     */
    public LetterTile peel() {
        Random generator = new Random();
        int i = generator.nextInt(letters.size()-1);
        if (i < this.letters.size()) {
            return letters.remove(i);
        }
        return null;
    }

    /**
     * Adds the given LetterTile to the list
     * @param lt
     */
    public void addLetter(LetterTile lt) {
        this.letters.add(lt);
    }
    
    /**
     * Returns the size of the list
     * @return
     */
    public int size() {
        return this.letters.size();
    }
    
    /**
     * Returns the LetterTile at index i
     * @param i index
     * @return
     */
    public LetterTile get(int i) {
        return this.letters.get(i);
    }
    
    /**
     * Moves the LetterTile at index i to the new coordinates given
     * by Rect r
     * @param i index
     * @param r new position
     */
    public void moveTo(int i, Rect r) {
        this.letters.set(i, this.letters.get(i).place(r));
    }
    
    public void select(int i) {
        LetterTile lt = this.letters.get(i);
        lt.changeColorToBlue();
        this.letters.set(i, lt);
    }
    
    public void remove(LetterTile lt) {
        String s = lt.getLetter();
        for (int i=0; i<this.letters.size(); i++) {
            if (s.equals(this.letters.get(i).getLetter())) {
                this.letters.remove(i);
                return;
            }
        }
    }
    public LetterTile removeFromIdenticalHeap() {
        return this.letters.remove(0);
    }
    
    protected void drawPileInHeapView(Canvas canvas) {
        if (this.size() == 0) {
            return;
        }
        if (this.size() > 0) {
            this.get(0).drawTile(canvas);
        }
        if (this.size() > 1) {
            Rect r = this.get(0).r;
            canvas.drawCircle(r.right-NUM_INDICATOR_RADIUS, r.top+NUM_INDICATOR_RADIUS, NUM_INDICATOR_RADIUS, numindicatorpaint);
            canvas.drawText(new String(this.size() + ""), (float)r.right-NUM_INDICATOR_RADIUS, (float)r.top+NUM_INDICATOR_RADIUS+5, this.numpaint);
        }
    }
    
    public void changeColorToRed() {
        for (int i=0; i<this.size(); i++) {
            this.letters.get(i).changeColorToRed();
        }
    }
    public void changeColorToBlue() {
        for (int i=0; i<this.size(); i++) {
            this.letters.get(i).changeColorToBlue();
        }
    }
    public void changeColorToNotRed() {
        for (int i=0; i<this.size(); i++) {
            this.letters.get(i).changeColorToNotRed();
        }
    }
    public void changeColorToNotBlue() {
        for (int i=0; i<this.size(); i++) {
            this.letters.get(i).changeColorToNotBlue();
        }
    }
}
