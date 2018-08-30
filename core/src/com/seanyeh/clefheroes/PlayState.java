package com.seanyeh.clefheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Iterator;

enum Mode {
    CHALLENGE, PRACTICE
}

public class PlayState extends AbstractState implements InputProcessor {
    private OrthographicCamera camera;

    SpriteBatch batch;
    ShapeRenderer shape;

    Texture pixelTexture;

    // Sprites

    Array<AbstractSprite> notes, tempNotes;
    Array<Note> finishedNotes;
    Array<MovingTextSprite> texts;
    PipeSprite pipeSprite;

    ButtonSprite menuButton;

    TextSprite doneSprite;
    boolean doneActive;

    Sprite skybg;


    float BEAT_WIDTH, STAFF_Y, PIANO_HEIGHT, KEY_WIDTH;
    float NOTE_HEIGHT, NOTE_WIDTH;

    // BEAT_WIDTH's per second. A unit is 1/6 of WIDTH
    final double[] SPEEDS = new double[]{0.5, 1, 1.5, 2.25};
    final String[] SPEED_NAMES = new String[]{"Slow", "Medium", "Fast", "Insane"};
    final int[] CHALLENGE_LIMITS = new int[]{20, 30, 40, 50};
    final Clef[] CLEFS = new Clef[]{Clef.TREBLE(), Clef.BASS(), Clef.ALTO(),
            Clef.TENOR(), Clef.SOPRANO(), Clef.MEZZO(), Clef.BARITONE()};
    final Mode[] MODES = new Mode[]{Mode.PRACTICE, Mode.CHALLENGE};

    final String[] NOTES = new String[]{"A", "B", "C", "D", "E", "F", "G", "A"};
    TextSprite[] letterSprites;

    // Settings: speed, challengeLimit, clef, mode
    double SPEED;
    String speedName;
    int challengeLimit;
    Clef clef;
    Mode mode;


    int numNotes;

    // Move somewhere else probably (alon with keypress logic)
    ObjectMap<Integer, String> KEYS;

    // Keep track of last touched key
    final Color GREEN = new Color(100/255f, 254/255f, 80/255f, 0.6f);
    final Color RED = new Color(254/255f, 100/255f, 80/255f, 0.6f);
    int activeKey = 0;
    int activeKeyLife = 0;
    Color activeKeyColor = GREEN;



    public PlayState(ClefHeroesGame main) {
        super(main);
    }

    @Override
    public void create () {

        HEIGHT = Gdx.graphics.getHeight();
        WIDTH = Gdx.graphics.getWidth();
        BEAT_WIDTH = WIDTH/6;

        NOTE_HEIGHT = HEIGHT/16;
        NOTE_WIDTH = (int)(380*(NOTE_HEIGHT/281.0)); // magic numbers from blob dimensions

        STAFF_Y = HEIGHT - 10*NOTE_HEIGHT;
        PIANO_HEIGHT = STAFF_Y - 10;
        KEY_WIDTH = WIDTH/8;

        notes = new Array<AbstractSprite>();
        tempNotes = new Array<AbstractSprite>();
        finishedNotes = new Array<Note>();

        // -- Graphics --
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        batch = new SpriteBatch();

        // Texts
        texts = new Array<MovingTextSprite>();


        pixelTexture = new Texture(Gdx.files.internal("data/pixel_g.png"));

        shape = new ShapeRenderer();

        menuButton = new ButtonSprite(this, "backtomenu", WIDTH, (float)(HEIGHT - 1.5*NOTE_HEIGHT), (float)(1.5*NOTE_HEIGHT), false, false);
        menuButton.x -= menuButton.width;


        // Letter sprites
        letterSprites = new TextSprite[NOTES.length];
        float keyWidth = WIDTH/8;
        for (int i = 0; i < NOTES.length; i++) {
            String s = "letters/" + NOTES[i].toLowerCase();
            letterSprites[i] = new TextSprite(this, s, (float)((i+0.5)*keyWidth), 2*NOTE_HEIGHT, 2*NOTE_HEIGHT);
        }


        // Pipe sprite
        pipeSprite = new PipeSprite(this, BEAT_WIDTH);

        // Done sprite
        doneSprite = new TextSprite(this, "done", WIDTH/2, HEIGHT/2, 1);
        doneActive = false;

        // BG
        skybg = new Sprite(new Texture(Gdx.files.internal("data/sky.png")));
        skybg.setSize(WIDTH, HEIGHT);

        // Setup Input
        Gdx.input.setInputProcessor(this);

        KEYS = new ObjectMap<Integer, String>();
        KEYS.put(Input.Keys.A, "A");
        KEYS.put(Input.Keys.B, "B");
        KEYS.put(Input.Keys.C, "C");
        KEYS.put(Input.Keys.D, "D");
        KEYS.put(Input.Keys.E, "E");
        KEYS.put(Input.Keys.F, "F");
        KEYS.put(Input.Keys.G, "G");

        numNotes = 0;

        // Defaults
        mode = Mode.PRACTICE;
        clef = Clef.ALTO();
        clef.setSizePosition(this);
        SPEED = 1;
        challengeLimit = 100;
    }

    public void setSettings(int modeIndex, int clefIndex, int speedIndex) {
        System.out.println("setSettings: clefIndex: " + clefIndex);
        mode = MODES[modeIndex];
        SPEED = SPEEDS[speedIndex];
        speedName = SPEED_NAMES[speedIndex];
        challengeLimit = CHALLENGE_LIMITS[speedIndex];
        clef = CLEFS[clefIndex];
        clef.setSizePosition(this);

        /*
         * Add notes
         */

        // Add extra note for "slow"
        if (speedIndex <= 0) {
            addNote(WIDTH - 2*BEAT_WIDTH);
        }
        // Add extra note for "slow" or "medium"
        if (speedIndex <= 1) {
            addNote(WIDTH - BEAT_WIDTH);
        }
        addNote();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.begin();
        skybg.draw(batch, 0.3f);
        batch.end();

        // Lines
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.BLACK);

        for (int i = 0; i < 5; i++) {
            // i+2 because STAFF_Y is the bottom BELOW the ledger line.
            float y = STAFF_Y + (i+2)*NOTE_HEIGHT;
            shape.line(0, y, WIDTH, y);
        }
        shape.end();


        // Draw keyboard

        // -- Draw white keys
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.WHITE);
        shape.rect(0, 0, WIDTH, PIANO_HEIGHT);

        shape.setColor(activeKeyColor);
        // -- Draw active key
        if (activeKeyLife > 0) {
            activeKeyLife--;
            shape.rect(activeKey*KEY_WIDTH, 0, KEY_WIDTH, PIANO_HEIGHT);
        }
        shape.end();

        // -- Draw vertical lines
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.GRAY);
        for (int i = 0; i < 8; i++) {
            float tempX = (i*(WIDTH/8));
            shape.line(tempX, 0, tempX, PIANO_HEIGHT);
        }
        shape.end();

        batch.begin();
        // -- Draw letters
        for (TextSprite letter : letterSprites) {
            letter.render(batch);
        }

        // Images
        batch.setProjectionMatrix(camera.combined);

        // Draw Clef
        clef.render(batch);

        // Draw Notes
        renderSpriteArray(notes);
        renderSpriteArray(tempNotes);

        // Draw Labels
        Iterator<MovingTextSprite> textIt = texts.iterator();
        while (textIt.hasNext()) {
            MovingTextSprite t = textIt.next();
            t.render(batch);

            if (t.isFinished) {
                textIt.remove();
            }
        }

        // Draw Button
        menuButton.render(batch);

        // Draw Pipes
        pipeSprite.render(batch);



        // Add new note if needed
        if (notes.size > 0 && notes.peek().x < WIDTH - BEAT_WIDTH) {
            addNote();
        }

        // Show Done!
        if (doneActive) {
            doneSprite.render(batch);
        }
        // -- If no more notes
        else if (notes.size == 0 && finishedNotes.size > 0) {
            doneActive = true;
            doneSprite.addActions(doneSprite.Delay(60),
                    doneSprite.QuickExpand(HEIGHT/2, 40, "EaseOut"),
                    doneSprite.Delay(60),
                    doneSprite.Runner(new Runnable() {
                        public void run() {
                            setState("RESULTS");
                        }
                    })
            );
        }

        batch.end();
    }

    public void renderSpriteArray(Array<AbstractSprite> arr) {
        Iterator<AbstractSprite> it = arr.iterator();
        while (it.hasNext()) {
            AbstractSprite s = it.next();
            s.render(batch);

            if (s.isFinished) {
                it.remove();
                finishedNotes.add((Note)s);
            }
        }
    }

    public void addNote() {
        addNote(WIDTH);
    }

    public void addNote(float x) {
        if (mode == Mode.PRACTICE || numNotes < challengeLimit) {
            Note n = Note.genRandomNote(this);
            n.setX(x);
            notes.add(n);

            numNotes++;
        }
    }

    public void showNoteResult(Note n, Status st) {
        texts.add(new MovingTextSprite(this, st.name().toLowerCase(), (int)n.x, (int)n.y, 2*NOTE_HEIGHT));
    }


    public boolean guessAnswer(String guess) {
        if (notes.size == 0) {
            return false;
        }

        Note n = (Note)(notes.first());
        String answer = clef.toNoteName(n);

        // If already answered
        if (n.getAnswerStatus() != Status.NONE) {

        }
        else if (n.getNoteStatus() == Status.NONE) {
            // do nothing
        }
        // If Correct
        else if (guess == answer) {
            int midi = clef.toMidi(n);
            MAIN.playSound(midi);
            showNoteResult(n, n.getNoteStatus());
            n.setAnswerStatus(n.getNoteStatus());

            n.gotoPipe();
            pipeSprite.swallowNote();

            notes.removeIndex(0);
            tempNotes.add(n);

            return true;
        }

        else {
            n.setAnswerStatus(Status.WRONG);
            showNoteResult(n, Status.WRONG);
        }

        return false;
    }

    public boolean touchPiano(float x, float y) {
        if (y > PIANO_HEIGHT) {
            return false;
        }

        for (int i = 0; i < 8; i++) {
            if (x < (i+1)*KEY_WIDTH) {
                boolean b = guessAnswer(NOTES[i]);
                setActiveKey(i, b);
                return true;
            }
        }

        return false;
    }

    public void setActiveKey(int key, boolean isCorrect) {
        activeKey = key;
        activeKeyLife = 10;

        if (isCorrect) {
            activeKeyColor = GREEN;
        } else {
            activeKeyColor = RED;
        }
    }

    public Array<Note> getResults() {
        return finishedNotes;
    }

    public String getSpeed() {
        return speedName;
    }

    public String getClef() {
        return clef.toString();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }


    // Inputs
    public boolean keyDown (int keycode) {

        if (!KEYS.containsKey(keycode)) {
            return false;
        }

        String guess = KEYS.get(keycode);
        guessAnswer(guess);

        return true;
    }

    public boolean keyUp (int keycode) {
        return false;
    }

    public boolean keyTyped (char character) {
        return false;
    }

    public boolean touchDown (int x, int y, int pointer, int button) {
        Vector3 p = new Vector3(x, y, 0);
        camera.unproject(p);

        if (touchPiano(p.x, p.y)) {
            return true;
        }

        if (menuButton.containsXY(p.x, p.y)) {
            setState("MENU");
        }

        return false;
    }

    public boolean touchUp (int x, int y, int pointer, int button) {
        return false;
    }

    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    public boolean mouseMoved (int x, int y) {
        return false;
    }

    public boolean scrolled (int amount) {
        return false;
    }
}
