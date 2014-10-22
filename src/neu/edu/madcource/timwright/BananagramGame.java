package neu.edu.madcource.timwright;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

public class BananagramGame extends Activity {
    
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String SAVED_INSTANCE_KEY = "saved_instance_key";
    public static final String SAVED_GAME_STARTED = "saved_game_started";
    public static final String PILE_SIZE = "neu.edu.madcource.timwright.pilesize";
    public static final String SAVED_GAME = "neu.edu.madcource.timwright.savedgame";
    public static final int PILE_SIZE_SMALL = 24;
    public static final int PILE_SIZE_MEDIUM = 32;
    public static final int PILE_SIZE_LARGE = 48;
    public static final int STARTER_SIZE = 15;
    public static final int TIME_UNTIL_PEEL = 46000;
    public static final int TIME_ONTICK_INTERVAL = 1000;
    public static final int NUM_TILES_ACROSS = 15;
    public static final int NUM_TILES_VISIBLE = 9;
    
    private int board_row = 3;
    private int board_column = 3;
    private LetterHeap heap;
    private int userpilesize;
    private int userheapsize;
    private boolean savedgame;
    private boolean heapempty = false;
    private BoardView boardview;
    private HeapView heapview;
    private Integer boardselectx;
    private Integer boardselecty;
    private Integer userselectx;
    private Integer userselecty;
    
    private LetterTile[][] board;
    private LetterHeap[][] userpile;
    
    private WordGameDictionary dictmem;
    private int score = 0;
    
    private CountDownTimerUntilPeel timer;
    private ToneGenerator beeper;
//    beeper.startTone(ToneGenerator.TONE_DTMF_5,100);
    private long timeleft = TIME_UNTIL_PEEL;
    
    private final String masterlist = 
            "AAAAABBCCCDDEEEEEFFGGHHIIIIJKKLLMMMNNNOOOOOPPQRRRSSSSTTTUUUVWWXYYZ";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bananagram_game);
        
        this.userpilesize = STARTER_SIZE;
        this.userheapsize = setPileSize(getIntent().getIntExtra(PILE_SIZE, PILE_SIZE_SMALL));
        this.savedgame = getIntent().getBooleanExtra(SAVED_GAME, false);
        
        this.heap = new LetterHeap(this, "");
        for (int i=0; i<userheapsize; i++) {
            Random generator = new Random();
            int j = generator.nextInt(masterlist.length()-1);
            this.heap.addLetter(new LetterTile(this, masterlist.substring(j,j+1), null));
        }
        
        board = new LetterTile[NUM_TILES_ACROSS][NUM_TILES_ACROSS];
        userpile = new LetterHeap[7][4];
        for (int i=0; i<7; i++) {
            for (int j=0; j<4; j++) {
                userpile[i][j] = new LetterHeap(this, "");
            }
        }

        boardview = (BoardView) findViewById(R.id.boardview_id);
        heapview = (HeapView) findViewById(R.id.heapview_id);
        
        dictmem = new WordGameDictionary(this);
        new InitializeDictionaryTask().execute(null,null,null);
        //dictmem.onStartCommand(new Intent(), 0, 0);
        beeper = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
        
        if (this.savedgame) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String gamestate = settings.getString(SAVED_INSTANCE_KEY, "");
            restoreGameState(gamestate);
        }
    }
    
    private class InitializeDictionaryTask extends AsyncTask<Integer, Integer, Integer> {
        protected Integer doInBackground(Integer... i) {
            dictmem.onStartCommand(new Intent(), 0, 0);
            return 0;
        }
    }

    
    @Override
    protected void onPause() {
        super.onPause();
        if (this.timer != null) {
            this.timer.cancel();
        }
    }
    
    @Override
    protected void onResume() {
       super.onResume();
       if (this.timer != null) {
           this.timer.start();
       }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (this.timer != null) {
            this.timer.cancel();
        }
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SAVED_GAME_STARTED, true);
        editor.putString(SAVED_INSTANCE_KEY, gameStateToString());
        editor.commit();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.timer != null) {
            this.timer.cancel();
        }
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SAVED_GAME_STARTED, true);
        editor.putString(SAVED_INSTANCE_KEY, gameStateToString());
        editor.commit();
    }
    
    public void pauseButton() {
        Intent intent = new Intent(this, BananaPause.class);
        startActivityForResult(intent, 0);
    }
    
    /**
     * Allows the user to exit from the pause menu
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==2){
            finish();
        }
    }
    
    /**
     * Converts the state of the game into a (long & complicated) string
     * @return
     */
    private String gameStateToString() {
        String gamestate = "BOARD\n";
        for (int i=0; i<NUM_TILES_ACROSS; i++) {
            for (int j=0; j<NUM_TILES_ACROSS; j++) {
                if (board[i][j] != null) {
                    gamestate += i + "\n" + j + "\n" + board[i][j].toString();
                }
            }
        }
        gamestate += "USERPILE\n";
        for (int i=0; i<7; i++) {
            for (int j=0; j<4; j++) {
                if (userpile[i][j] != null) {
                    for (int k=0; k<userpile[i][j].size(); k++) {
                        gamestate += i + "\n" + j + "\n" + userpile[i][j].get(k).toString();
                    }
                }
            }
        }
        if (timer != null) {
            gamestate += "SPLIT\n";
            gamestate += timeleft + "\n";
        }
        gamestate += "SCORE\n";
        gamestate += this.score + "\n";
        return gamestate;
    }
    
    /**
     * Given a gamestate string, instantiates all variable values
     * to continue a game.
     * @param gamestate
     */
    private void restoreGameState(String gamestate) {
        if (gamestate.equals(""))
            return;
        try {
            BufferedReader reader = new BufferedReader(new StringReader(gamestate));
            String s;
            boolean boardbool = false;
            boolean userpilebool = false;
            for (int i=0; i<7; i++) {
                for (int j=0; j<4; j++) {
                    userpile[i][j] = new LetterHeap(this, "");
                }
            }
            while ((s = reader.readLine()) != null) {
                if (s.equals("")) {
                    return;
                }
                else if (s.equals("BOARD")) {
                    boardbool = true;
                    
                }
                else if (s.equals("USERPILE")) {
                    boardbool = false;
                    userpilebool = true;
                }
                else if (s.equals("SPLIT")) {
                    timer = new CountDownTimerUntilPeel(
                            Integer.parseInt(reader.readLine()), TIME_ONTICK_INTERVAL);
                    timer.start();
                    userpilebool = false;
                }
                else if (s.equals("SCORE")) {
                    this.score = Integer.parseInt(reader.readLine());
                }
                else if (!s.matches("-?\\d+(\\.\\d+)?")) {
                    return;
                }
                else if (boardbool) {
                    board[Integer.parseInt(s)][Integer.parseInt(reader.readLine())] =
                            new LetterTile(this, reader.readLine(), new Rect(Integer.parseInt(reader.readLine()),
                                    Integer.parseInt(reader.readLine()),
                                    Integer.parseInt(reader.readLine()),
                                    Integer.parseInt(reader.readLine())));
                }
                else if (userpilebool) {
                    userpile[Integer.parseInt(s)][Integer.parseInt(reader.readLine())]
                            .addLetter(new LetterTile(this, reader.readLine(), 
                                    new Rect(Integer.parseInt(reader.readLine()),
                                    Integer.parseInt(reader.readLine()),
                                    Integer.parseInt(reader.readLine()),
                                    Integer.parseInt(reader.readLine()))));
                }
            }
        }
        catch (IOException e) {
            return;
        }
    }
    
    /**
     * Returns size of pile. 0 maps to 11, 1 maps to 15,
     * 2 maps to 21.
     * @param i
     * @return
     */
    private int setPileSize(int i) {
        if (i == 0) {
            return PILE_SIZE_SMALL;
        } else if (i == 1) {
            return PILE_SIZE_MEDIUM;
        } else if (i == 2) {
            return PILE_SIZE_LARGE;
        }
        else return PILE_SIZE_MEDIUM;
    }
    
    /**
     * Handles all activity when the user selects a tile from the heap
     * @param x coordinate of motionevent
     * @param y coordinate of motionevent
     * @param tw width of tile in this game
     * @param th height of tile in this game
     */
    public void selectTile(float x, float y, float tw, float th) {

        int offsetx = -3;
        int offsety = -1;
        while (x > tw) {
            offsetx++;
            x -= tw;
        }
        while (y > th) {
            offsety++;
            y -= th;
        }
        //lt = userpile[offsetx][offsety]; // lt is the tile at the selected spot, or null if there is no tile
        if (userpile[offsetx][offsety].size() == 0) { // there is no tile at the selected spot
            if (userselectx != null && userselecty != null) {
                userpile[userselectx][userselecty].changeColorToNotBlue();
                userselectx = null;
                userselecty = null;
            }
            if (boardselectx != null && boardselecty != null) {
                board[boardselectx][boardselecty].changeColorToNotBlue();
                boardselectx = null;
                boardselecty = null;
            }
        }
        else if (userpile[offsetx][offsety].size() > 0) {
             // there is a tile at the selected spot
                beeper.startTone(ToneGenerator.TONE_PROP_BEEP,100); // Beep!
                if (userselectx != null && userselecty != null) { // if another tile is selected
                    userpile[userselectx][userselecty].changeColorToNotBlue();
                }
                if (boardselectx != null && boardselecty != null) {
                    board[boardselectx][boardselecty].changeColorToNotBlue();
                    boardselectx = null;
                    boardselecty = null;
                }
            userpile[offsetx][offsety].changeColorToBlue();
            userselectx = offsetx;
            userselecty = offsety;
        
        }
        this.heapview.invalidate();
        this.boardview.invalidate();
    }
    
    /**
     * Handles all activity when the player touches the board
     * @param x coordinate of motionevent
     * @param y coordinate of motionevent
     * @param tw width of tiles in this game
     * @param th height of tiles in this game
     */
    public void selectBoard(float x, float y, float tw, float th) {
        LetterTile lt;
        
        int offsetx = 0;
        int offsety = 0;
        while (x > tw) {
            offsetx++;
            x -= tw;
        }
        while (y > th) {
            offsety++;
            y -= th;
        }
        Rect r = new Rect((int)(offsetx*tw), (int)(offsety*th), (int)(offsetx*tw+tw), (int)(offsety*th+th));
        offsetx += board_column;
        offsety += board_row;
        lt = board[offsetx][offsety]; // lt is the tile at the selected spot, or null if there is no tile
        
        if (lt == null) { // No tile at the selected spot
            if (this.userselectx != null && this.userselecty != null) { 
                // If there is a selected heap tile,
                // put the tile on the board
                this.boardview.tutorialNext();
                userpile[userselectx][userselecty].changeColorToNotBlue();
                lt = userpile[userselectx][userselecty].removeFromIdenticalHeap();
                userselectx = null;
                userselecty = null;
                lt.place(r);
                board[offsetx][offsety] = lt;
                board[offsetx][offsety].changeColorToNotBlue();
            }
            if (this.boardselectx != null && this.boardselecty != null) { // If there is a selected board tile
                lt = board[boardselectx][boardselecty];
                board[boardselectx][boardselecty] = null;
                lt.place(r);
                board[offsetx][offsety] = lt;
                board[offsetx][offsety].changeColorToNotBlue();
                if (boardselectx<NUM_TILES_ACROSS - 1) {
                    checkWordAndUpdateColors(boardselectx+1, boardselecty);
                }
                if (boardselectx>0) {
                    checkWordAndUpdateColors(boardselectx-1, boardselecty);
                }
                if (boardselecty<NUM_TILES_ACROSS - 1) {
                    checkWordAndUpdateColors(boardselectx, boardselecty+1);
                }
                if (boardselecty>0) {
                    checkWordAndUpdateColors(boardselectx, boardselecty-1);
                }
                this.boardselectx = null;
                this.boardselecty = null;
            }
            checkWordAndUpdateColors(offsetx, offsety);
        }
        else { // We selected a tile on the board
            beeper.startTone(ToneGenerator.TONE_PROP_BEEP,100); // Beep!
            if (userselectx != null && userselecty != null) { //if we have a selected heap tile
                userpile[userselectx][userselecty].changeColorToNotBlue();
                this.userselectx = null;
                this.userselecty = null;
                this.boardselectx = offsetx;
                this.boardselecty = offsety;
                board[boardselectx][boardselecty].changeColorToBlue();
                this.heapview.invalidate();
                this.boardview.invalidate();
                return;
            }
            if (this.boardselectx != null && this.boardselecty != null) { // there is a boardtile here
                board[boardselectx][boardselecty].changeColorToNotBlue();
                if (boardselectx == offsetx && boardselecty == offsety) {
                    board[boardselectx][boardselecty].changeColorToNotBlue();
                    this.boardselectx = null;
                    this.boardselecty = null;
                    this.heapview.invalidate();
                    this.boardview.invalidate();
                    return;
                }
                this.boardselectx = null;
                this.boardselecty = null;
            }
            board[offsetx][offsety].changeColorToBlue();
            this.boardselectx = offsetx;
            this.boardselecty = offsety;
        }

        this.heapview.invalidate();
        this.boardview.invalidate();
    }
    
    /**
     * Checks all the adjacent tiles for words
     * @param x
     * @param y
     */
    private void checkWordAndUpdateColors(int x, int y) {
        if (board[x][y] == null)
            return;
        String horizontal = letterLeft(x,y) + board[x][y].getLetter() + letterRight(x,y);
        String vertical = letterUp(x,y) + board[x][y].getLetter() + letterDown(x,y);
        if (horizontal.length() > 2) {
            horizontalRedOnFalse(x, y, dictmem.isWordPresent(horizontal.toLowerCase(new Locale("US"))));
        }
        if (vertical.length() > 2) {
            verticalRedOnFalse(x, y, dictmem.isWordPresent(vertical.toLowerCase(new Locale("US"))));
        }
    }
    
    /**
     * Starting from the given coordinates, searches if the horizontal
     * adjacent tiles form a word. Turns red if not.
     * @param x
     * @param y
     * @param b
     */
    private void horizontalRedOnFalse(int x, int y, boolean b) {
        int l = x;
        int r = x+1;
        while (l >= 0 && board[l][y] != null) {
            if (b)
                board[l][y].changeColorToNotRed();
            else
                board[l][y].changeColorToRed();
            l--;
        }
        while (r <= NUM_TILES_ACROSS - 1 && board[r][y] != null) {
            if (b)
                board[r][y].changeColorToNotRed();
            else
                board[r][y].changeColorToRed();
            r++;
        }
        if (b) {
            beeper.startTone(ToneGenerator.TONE_DTMF_1,100);
            score += 10;
        }
        else {
            beeper.startTone(ToneGenerator.TONE_PROP_NACK,100);
        }
    }
    
    /**
     * Starting from the given coordinates, searches if the vertical
     * adjacent tiles form a word. Turns red if not.
     * @param x
     * @param y
     * @param b
     */
    private void verticalRedOnFalse(int x, int y, boolean b) {
        int u = y;
        int d = y+1;
        while (u >= 0 && board[x][u] != null) {
            if (b)
                board[x][u].changeColorToNotRed();
            else
                board[x][u].changeColorToRed();
            u--;
        }
        while (d <= NUM_TILES_ACROSS - 1 && board[x][d] != null) {
            if (b)
                board[x][d].changeColorToNotRed();
            else
                board[x][d].changeColorToRed();
            d++;
        }
        if (b) {
            beeper.startTone(ToneGenerator.TONE_DTMF_1,100);
            score += 10;
        }
        else {
            beeper.startTone(ToneGenerator.TONE_PROP_NACK,100);
        }
    }
    
    private String letterLeft(int x, int y) {
        String s = "";
        while (x > 0) {
            x--;
            if (board[x][y] == null) {
                break;
            }
            s = board[x][y].getLetter() + s;
        }
        return s;
    }
    
    private String letterRight(int x, int y) {
        String s = "";
        while (x < NUM_TILES_ACROSS - 1) {
            x++;
            if (board[x][y] == null) {
                break;
            }
            s = s + board[x][y].getLetter();
        }
        return s;
    }
    
    private String letterUp(int x, int y) {
        String s = "";
        while (y > 0) {
            y--;
            if (board[x][y] == null) {
                break;
            }
            s = board[x][y].getLetter() + s;
        }
        return s;
    }
    
    private String letterDown(int x, int y) {
        String s = "";
        while (y < NUM_TILES_ACROSS - 1) {
            y++;
            if (board[x][y] == null) {
                break;
            }
            s = s + board[x][y].getLetter();
        }
        return s;
    }
    
    /**
     * Starts the game. Deals out the desired number of letters,
     * starts the timer
     */
    public void split() {
        int h = (int)this.heapview.getTileHeight();
        int w = (int)this.heapview.getTileWidth();
        LetterTile lt;
        for (int i=0; i<this.userpilesize; i++) {
            lt = this.heap.peel();
            String l = lt.getLetter();
            addLetterToHeap(lt, l, h, w);
        }
        
        this.timer = new CountDownTimerUntilPeel(TIME_UNTIL_PEEL, TIME_ONTICK_INTERVAL);
        this.timer.start();
        this.boardview.tutorialNext();
        this.boardview.invalidate();
        this.heapview.invalidate();
    }
    
    /**
     * A letter is peeled off from the heap and added to the heapview
     */
    public void peel() {
        if (this.heap.size() > 1) {
            LetterTile lt = this.heap.peel();
            addLetterToHeap(lt, lt.getLetter(), (int)this.heapview.getTileHeight(), (int)this.heapview.getTileWidth());
        }
        else {
            this.heapempty = true;
        }
    }
    
    /**
     * The user's selected tile is discarded, and three new tiles are
     * added to the heap.
     * @param w
     * @param h
     */
    public void dump(float w, float h) {
        if (this.userselectx != null && this.userselecty != null) {
            userpile[userselectx][userselecty] = null;
            userselectx = null;
            userselecty = null;
        }
        else if (this.boardselectx != null && this.boardselecty != null) {
            board[boardselectx][boardselecty] = null;
            boardselectx = null;
            boardselecty = null;
        } else {
            return;
        }
        for (int i=0; i<3; i++) {
            Random generator = new Random();
            int j = generator.nextInt(masterlist.length()-1);
            LetterTile lt = new LetterTile(this, masterlist.substring(j,j+1), null);
            addLetterToHeap(lt, lt.getLetter(), (int)h, (int)w);
        }
        this.heapview.invalidate();
        this.boardview.invalidate();
    }
    
    /**
     * Places the given tile in the heapview.
     * MAJOR PROBLEM: Overrides any previous letter there; multiple
     * letters of the same type are impossible. Needs fixing.
     * @param lt
     * @param l
     * @param h
     * @param w
     */
    public void addLetterToHeap(LetterTile lt, String l, int h, int w) {
        if (l.equals("A")) {
            userpile[0][0].addLetter(new LetterTile(this, l, new Rect(3*w, h, 4*w, 2*h)));
        } else if (l.equals("B")) {
            userpile[1][0].addLetter(new LetterTile(this, l, new Rect(4*w, h, 5*w, 2*h)));
        } else if (l.equals("C")) {
            userpile[2][0].addLetter(new LetterTile(this, l, new Rect(5*w, h, 6*w, 2*h)));
        } else if (l.equals("D")) {
            userpile[3][0].addLetter(new LetterTile(this, l, new Rect(6*w, h, 7*w, 2*h)));
        } else if (l.equals("E")) {
            userpile[4][0].addLetter(new LetterTile(this, l, new Rect(7*w, h, 8*w, 2*h)));
        } else if (l.equals("F")) {
            userpile[5][0].addLetter(new LetterTile(this, l, new Rect(8*w, h, 9*w, 2*h)));
        } else if (l.equals("G")) {
            userpile[6][0].addLetter(new LetterTile(this, l, new Rect(9*w, h, 10*w, 2*h)));
        } else if (l.equals("H")) {
            userpile[0][1].addLetter(new LetterTile(this, l, new Rect(3*w, 2*h, 4*w, 3*h)));
        } else if (l.equals("I")) {
            userpile[1][1].addLetter(new LetterTile(this, l, new Rect(4*w, 2*h, 5*w, 3*h)));
        } else if (l.equals("J")) {
            userpile[2][1].addLetter(new LetterTile(this, l, new Rect(5*w, 2*h, 6*w, 3*h)));
        } else if (l.equals("K")) {
            userpile[3][1].addLetter(new LetterTile(this, l, new Rect(6*w, 2*h, 7*w, 3*h)));
        } else if (l.equals("L")) {
            userpile[4][1].addLetter(new LetterTile(this, l, new Rect(7*w, 2*h, 8*w, 3*h)));
        } else if (l.equals("M")) {
            userpile[5][1].addLetter(new LetterTile(this, l, new Rect(8*w, 2*h, 9*w, 3*h)));
        } else if (l.equals("N")) {
            userpile[6][1].addLetter(new LetterTile(this, l, new Rect(9*w, 2*h, 10*w, 3*h)));
        } else if (l.equals("O")) {
            userpile[0][2].addLetter(new LetterTile(this, l, new Rect(3*w, 3*h, 4*w, 4*h)));
        } else if (l.equals("P")) {
            userpile[1][2].addLetter(new LetterTile(this, l, new Rect(4*w, 3*h, 5*w, 4*h)));
        } else if (l.equals("Q")) {
            userpile[2][2].addLetter(new LetterTile(this, l, new Rect(5*w, 3*h, 6*w, 4*h)));
        } else if (l.equals("R")) {
            userpile[3][2].addLetter(new LetterTile(this, l, new Rect(6*w, 3*h, 7*w, 4*h)));
        } else if (l.equals("S")) {
            userpile[4][2].addLetter(new LetterTile(this, l, new Rect(7*w, 3*h, 8*w, 4*h)));
        } else if (l.equals("T")) {
            userpile[5][2].addLetter(new LetterTile(this, l, new Rect(8*w, 3*h, 9*w, 4*h)));
        } else if (l.equals("U")) {
            userpile[6][2].addLetter(new LetterTile(this, l, new Rect(9*w, 3*h, 10*w, 4*h)));
        } else if (l.equals("V")) {
            userpile[1][3].addLetter(new LetterTile(this, l, new Rect(4*w, 4*h, 5*w, 5*h)));
        } else if (l.equals("W")) {
            userpile[2][3].addLetter(new LetterTile(this, l, new Rect(5*w, 4*h, 6*w, 5*h)));
        } else if (l.equals("X")) {
            userpile[3][3].addLetter(new LetterTile(this, l, new Rect(6*w, 4*h, 7*w, 5*h)));
        } else if (l.equals("Y")) {
            userpile[4][3].addLetter(new LetterTile(this, l, new Rect(7*w, 4*h, 8*w, 5*h)));
        } else if (l.equals("Z")) {
            userpile[5][3].addLetter(new LetterTile(this, l, new Rect(8*w, 4*h, 9*w, 5*h)));
        }
    }
    
    /**
     * Private class for the timer. Gives 45 seconds until peeling.
     * @author Tim
     *
     */
    private class CountDownTimerUntilPeel extends CountDownTimer {
        
        public CountDownTimerUntilPeel(long startTime, long interval) {
            super(startTime, interval);
            timeleft = startTime / TIME_ONTICK_INTERVAL;
            heapview.changeTimerText("" + timeleft);
            }
        
        public void onFinish() {
            if (heap.size() == 0) {
                heapview.changeTimerText("Done");
                return;
            }
            peel();
            timer = new CountDownTimerUntilPeel(TIME_UNTIL_PEEL, TIME_ONTICK_INTERVAL);
            timer.start();
        }
        
        @Override
        public void onTick(long millisUntilFinished) {
            timeleft = millisUntilFinished;
            heapview.changeTimerText("" + timeleft / TIME_ONTICK_INTERVAL);
        }
        
    }
    
    /**
     * Returns a list of user tiles for HeapView to draw.
     * Also checks if the user is out of tiles, and awards
     * points if they are.
     * @return
     */
    public LetterHeap getUser() {
        LetterHeap usertiles = new LetterHeap(this, "");
        for (int i=0; i<7; i++) {
            for (int j=0; j<4; j++) {
                if (userpile[i][j] != null && this.userpile[i][j].size() > 0) {
                    usertiles.addLetter(userpile[i][j].get(0));
                }
            }
        }
        if (this.heapempty) {
            this.endOfGame();
        }
        else if (usertiles.size() == 0 && timer != null) { 
            // The user has used all the tiles before the timer ran out
            this.score += 100; // Points!
            this.heapview.updateScore(this.score);
            timer.cancel();
            timer.onFinish();// Peel and reset clock
        }
        return usertiles;
    }
    public void drawUserHeapAtCoord(int i, int j, Canvas canvas) {
        if (userpile[i][j] != null) {
            userpile[i][j].drawPileInHeapView(canvas);
        }
    }
    
    public void endOfGame() {
        this.timer.cancel();
        this.heapview.changeTimerText("Done");
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                if (board[i][j] != null) {
                    if (!board[i][j].red) {
                        score += 50;
                    }
                }
            }
        }
        this.heapempty = false;
        this.timer = null;
        this.heapview.updateScore(score);
    }
    
    /**
     * Returns a list of board tiles for BoardView to draw
     * @return
     */
    public LetterHeap getPlacedTiles() {
        LetterHeap placedtiles = new LetterHeap(this, "");
        for (int i=0; i<NUM_TILES_ACROSS; i++) {
            for (int j=0; j<NUM_TILES_ACROSS; j++) {
                if (board[i][j] != null) {
                    placedtiles.addLetter(board[i][j]);
                }
            }
        }
        return placedtiles;
    }
    
    /**
     * IMPORTANT:
     * 1 equals down/right, -1 equals up/left 
     * true equals vertical, false equals horizontal
     * SO...
     * 1,true = tiles go down, "looking up"
     * 1,false = tiles go right, "looking left"
     * -1,true = tiles go up, "looking down"
     * -1,false = tiles go left, "looking right"
     * @param th
     * @param dir
     * @param vorh
     */
    public void moveTiles(int dir, boolean vorh) {
        if (dir == 1 && vorh) {
            //moving down
            if (board_row <= 0) {// 
                return;
            }
            board_row--;
        }
        else if (dir == 1 && !vorh) {
            //moving right
            if (board_column <= 0) {
                return;
            }
            board_column--;
        }
        else if (dir == -1 && vorh) {
            //moving up
            if (board_row >= NUM_TILES_ACROSS - NUM_TILES_VISIBLE) {
                return;
            }
            board_row++;
        }
        else if (dir == -1 && !vorh) {
            //moving left
            if (board_column >= NUM_TILES_ACROSS - NUM_TILES_VISIBLE) {
                return;
            }
            board_column++;
        }
        int tw = (int)this.boardview.tilewidth;
        int th = (int)this.boardview.tileheight;
        for (int i=0; i<NUM_TILES_ACROSS; i++) {
            for (int j=0; j<NUM_TILES_ACROSS; j++) {
                if (board[i][j] != null) {
                    if (vorh) {
                        board[i][j].moveTileVertical(th, dir);
                    }
                    else {
                        board[i][j].moveTileHorizontal(tw, dir);
                    }
                }
            }
        }
        this.boardview.invalidate();
    }
}
