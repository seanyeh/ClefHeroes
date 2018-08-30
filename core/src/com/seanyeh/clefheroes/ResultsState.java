package com.seanyeh.clefheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ResultsState extends AbstractState implements InputProcessor {
    OrthographicCamera camera;

    Sprite skybg;

    SpriteBatch batch;
    ButtonSprite againButton, menuButton;

    float XUNIT, YUNIT;


    String[] CATEGORIES = new String[]{"perfect", "good", "tooearlylate", "missedwrong"};
    TextSprite[] categorySprites;
    NumberSprite[] numberSprites;
    TextSprite totalSprite;
    NumberSprite scoreSprite;
    TextSprite highScoreTextSprite;
    NumberSprite highScoreSprite;

    TextSprite congratsTextSprite;

    // Clef Hero sprites
    Sprite[] clefheroes = new Sprite[4];

    int prevHighScore = 0;
    int score;

    int[] results; // Perfect, Good, TooEarly/TooLate, Wrong/Missed
    String clef, speed;

    float RIGHT_X, TOTAL_Y;

    public ResultsState(ClefHeroesGame main) {
        super(main);
    }

    private Sprite genClefHero(int num) {
        Texture texture = new Texture(Gdx.files.internal("data/blob" + num + ".png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Sprite s = new Sprite(texture);
        s.setSize((float)(YUNIT*0.6), YUNIT/2);
//        s.setSize((float)YUNIT*(3/5), (float)YUNIT/2);
        return s;
    }

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        YUNIT = HEIGHT/8;
        RIGHT_X = (float)(0.6*WIDTH);

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);


        // Show Results
        results = new int[]{0, 0, 0, 0};
        numberSprites = new NumberSprite[]{null, null, null, null};
        categorySprites = new TextSprite[CATEGORIES.length];
        float x = (float)(0.3*WIDTH);
        for (int i = 0; i < CATEGORIES.length; i++) {
            float y = HEIGHT - (i+1)*YUNIT;

            String s = CATEGORIES[i] + "_category";
            // Don't center on x
            categorySprites[i] = new TextSprite(this, s, x, y, (float)(0.8*YUNIT), false, false);
        }

        // Total
        TOTAL_Y = HEIGHT - (float)((CATEGORIES.length + 1.5)*YUNIT);
        totalSprite = new TextSprite(this, "total", x, TOTAL_Y, YUNIT, false, false);
        score = 0;

        highScoreTextSprite = new TextSprite(this, "hiscore", x, TOTAL_Y - YUNIT, (float)(0.6*YUNIT), false, false);

        againButton = new ButtonSprite(this, "tryagain", 10, 0, YUNIT, false, false);
        menuButton = new ButtonSprite(this, "backtomenu", WIDTH - 10, 0, YUNIT, false, false);
        menuButton.x -= menuButton.width;

        skybg = new Sprite(new Texture(Gdx.files.internal("data/sky.png")));
        skybg.setSize(WIDTH, HEIGHT);


        // Show clef heroes :)
        // Order of numbers (which determine color)
        int[] clefheroNumbers = new int[]{5, 4, 3, 2};
        for (int i = 0; i < clefheroes.length; i++) {
            float y = HEIGHT - (i+1)*YUNIT + YUNIT/8;

            clefheroes[i] = genClefHero(clefheroNumbers[i]);
            clefheroes[i].setPosition(x - YUNIT, y);
        }


        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        skybg.draw(batch, 0.3f);


        againButton.render(batch);
        menuButton.render(batch);


        for (TextSprite t: categorySprites) {
            t.render(batch);
        }
        for (NumberSprite n : numberSprites) {
            if (n != null) {
                n.render(batch);
            }
        }

        totalSprite.render(batch);
        if (scoreSprite != null) {
            scoreSprite.render(batch);
        }

        if (highScoreSprite != null) {
            highScoreTextSprite.render(batch);
            highScoreSprite.render(batch);
        }

        if (congratsTextSprite != null) {
            congratsTextSprite.render(batch);
        }

        // Draw the clef heroes
        for (Sprite s : clefheroes) {
            s.draw(batch);
        }


        batch.end();
    }


    public void setSettings(Array<Note> notes, String inClef, String inSpeed) {
        // First, reset the results
        results = new int[]{0, 0, 0, 0};
        score = 0;

        if (notes == null) { return; }
        // Add results
        for (Note note : notes) {
            Status s = note.getAnswerStatus();
            if (s == Status.PERFECT) {
                results[0]++;
                score += 5;
            }
            else if (s == Status.GOOD) {
                results[1]++;
                score += 3;
            }
            else if (s == Status.TOOEARLY || s == Status.TOOLATE) {
                results[2]++;
                score += 1;
            }
            else {
                results[3]++;
            }
        }

        // Create number sprites
        for (int i = 0; i < results.length; i++) {
            float y = HEIGHT - (i+1)*YUNIT;
            numberSprites[i] = new NumberSprite(this, results[i], RIGHT_X, y, (float)(0.8*YUNIT));
        }

        scoreSprite = new NumberSprite(this, score, RIGHT_X, TOTAL_Y + YUNIT/12, YUNIT);

        // Set clef, speed
        clef = inClef;
        speed = inSpeed;

        // Check high score
        //   High scores are stored under "Hiscores",
        //      Keys are Clef_Speed, e.g.: Treble_Slow = ...
        Preferences prefs = Gdx.app.getPreferences("Hiscores");
        String prefsKey = clef + "_" + speed;
        prevHighScore = prefs.getInteger(prefsKey, 0);

        System.out.println("Key: " + prefsKey + ", Previous high score: " + prevHighScore + ", current score: " + score);

        congratsTextSprite = null;

        // Player beat high score! Show "New hi score!". Don't show previous high score
        if (score > prevHighScore) {
            highScoreSprite = null;
            congratsTextSprite = new TextSprite(this, "newhiscore", (float)(0.3*WIDTH), TOTAL_Y - YUNIT, YUNIT, false, false);

            // Save high score
            prefs.putInteger(prefsKey, score);
            prefs.flush();
        }
        // Player did not beat high score, or tied with high score with > 0. Show previous high score
        else if (score < prevHighScore || score > 0) {
            float scoreX = highScoreTextSprite.getX() + highScoreTextSprite.getWidth();
            highScoreSprite = new NumberSprite(this, prevHighScore, scoreX , TOTAL_Y - YUNIT + YUNIT/12, (float)(0.5*YUNIT));
        }
        // Player score is 0 and high score is 0. Don't show high score
        else {
            highScoreSprite = null;
        }

    }



    @Override
    public void dispose() {
    }

    public boolean keyDown (int keycode) {
        return false;
    }

    public boolean keyUp (int keycode) {
        return false;
    }

    public boolean keyTyped (char character) {
        return false;
    }

    public boolean touchDown (int x, int y, int pointer, int b) {
        Vector3 p = new Vector3(x, y, 0);
        camera.unproject(p);

        if (againButton.containsXY(p.x, p.y)) {
            setState("PLAY");
        }
        else if (menuButton.containsXY(p.x, p.y)) {
            setState("MENU");
        }
        else {
            return false;
        }

        return true;
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
