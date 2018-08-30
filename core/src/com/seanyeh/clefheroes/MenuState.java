package com.seanyeh.clefheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;


public class MenuState extends AbstractState implements InputProcessor {
    OrthographicCamera camera;

    Sprite skybg;

    float XUNIT, YUNIT;
    float X_CENTER, X_OFFSCREEN_RIGHT, X_OFFSCREEN_LEFT;

    SpriteBatch batch;

    TextSprite[] modeSprites, clefSprites, speedSprites;
    final String[] MODES = new String[] {"practice", "100note"};
    final String[] CLEFS = new String[] {"treble", "bass", "alto", "tenor", "soprano", "mezzo", "baritone"};
    String[] CLEF_TEXTS, CLEF_IMAGES;
    final String[] SPEEDS = new String[] {"slow", "medium", "fast", "insane"};



    ButtonSprite[] buttons;
    int[] indexes;


    ButtonSprite playButton, backButton, helpButton;

    TextSprite helpSprite;
    boolean helpActive, helpShowing;


    public MenuState(ClefHeroesGame main) {
        super(main);
    }

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        XUNIT = WIDTH/8;
        YUNIT = HEIGHT/4;

        X_CENTER = WIDTH/2;
        X_OFFSCREEN_RIGHT = (float)(1.5*WIDTH);
        X_OFFSCREEN_LEFT = (float)(-.5*WIDTH);

        // Initialize the items off-screen

        CLEF_TEXTS = new String[CLEFS.length];
        CLEF_IMAGES = new String[CLEFS.length];
        for (int i = 0; i < CLEFS.length; i++) {
            CLEF_TEXTS[i] = CLEFS[i] + "_text";
            CLEF_IMAGES[i] = CLEFS[i] + "_clef";
        }
        modeSprites = initOptions(MODES, (float)(3.5*YUNIT));
        clefSprites = initOptions(CLEF_TEXTS, (float)(2.5*YUNIT));
        speedSprites = initOptions(SPEEDS, (float)(1.5*YUNIT));

        buttons = new ButtonSprite[6];

        // Mode (white)
        buttons[0] = new ButtonSprite(this, "arroww_left", XUNIT/2, (float)(3.5*YUNIT), YUNIT/2);
        buttons[1] = new ButtonSprite(this, "arroww_right", (float)(7.5*XUNIT), (float)(3.5*YUNIT), YUNIT/2);

        // Clef (green)
        buttons[2] = new ButtonSprite(this, "arrowg_left", XUNIT/2, (float)(2.5*YUNIT), YUNIT/2);
        buttons[3] = new ButtonSprite(this, "arrowg_right", (float)(7.5*XUNIT), (float)(2.5*YUNIT), YUNIT/2);

        // Speed (yellow)
        buttons[4] = new ButtonSprite(this, "arrowy_left", XUNIT/2, (float)(1.5*YUNIT), YUNIT/2);
        buttons[5] = new ButtonSprite(this, "arrowy_right", (float)(7.5*XUNIT) , (float)(1.5*YUNIT), YUNIT/2);

        skybg = new Sprite(new Texture(Gdx.files.internal("data/sky.png")));
        skybg.setSize(WIDTH, HEIGHT);


        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(this);

        // Initialize indexes
        indexes = new int[]{0, 0, 0};

        playButton = new ButtonSprite(this, "play", WIDTH/2, (float)(0.5*YUNIT), (float)(0.8*YUNIT));
        backButton = new ButtonSprite(this, "backarrow", XUNIT/2, (float)(0.5*YUNIT), YUNIT/2);

        // Help button and dialog
        helpButton = new ButtonSprite(this, "help", (float)(7.5*XUNIT), (float)(0.5*YUNIT), YUNIT/2);

        helpShowing = false;
        helpActive = false;
        helpSprite = new TextSprite(this, "helpdialog", WIDTH/2, HEIGHT/2, 1);
        resetHelp();
    }

    public void resetHelp() {
        helpSprite.setSize(1, 1);
        helpSprite.setX(WIDTH/2);
        helpSprite.setY(HEIGHT/2);
        helpSprite.setAlpha(0);
    }


    public TextSprite[] initOptions(String[] arr, float inY) {
        TextSprite[] sprites = new TextSprite[arr.length];
        for (int i = 0; i < arr.length; i++) {
            sprites[i] = new TextSprite(this, arr[i], X_OFFSCREEN_RIGHT, inY, (float)(0.8*YUNIT));
        }
        // Set first one to be shown
        sprites[0].setX(X_CENTER);

        return sprites;
    }

    @Override
    public void render() {
//        Gdx.gl.glClearColor(1, 1, 1, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.begin();

        for (ButtonSprite btn: buttons) {
            btn.render(batch);
        }

        for (TextSprite t: modeSprites) {
            t.render(batch);
        }
        for (TextSprite t: speedSprites) {
            t.render(batch);
        }
        for (TextSprite t: clefSprites) {
            t.render(batch);
        }

        playButton.render(batch);
        backButton.render(batch);
        helpButton.render(batch);
        helpSprite.render(batch);

        batch.end();
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

    public boolean touchDown (int x, int y, int pointer, int b2) {
        Vector3 p = new Vector3(x, y, 0);
        camera.unproject(p);

        // Check if settings buttons are pressed
        for (int i = 0; i < buttons.length; i++) {
            ButtonSprite b = buttons[i];
            int index = indexes[i/2];
            boolean isLeft = i%2 == 0;

            TextSprite[] arr;
            if (i/2 == 0) {
                arr = modeSprites;
            }
            else if (i/2 == 1) {
                arr = clefSprites;
            }
            else {
                arr = speedSprites;
            }

            // Button is pressed
            if (b.containsXY(p.x, p.y)) {
                if (isLeft && index >= 1) {
                    arr[index].move(X_OFFSCREEN_RIGHT);
                    arr[index - 1].setX(X_OFFSCREEN_LEFT);
                    arr[index - 1].move(X_CENTER);

                    indexes[i/2]--;
                }
                else if (!isLeft && index + 1 < arr.length) {
                    arr[index].move(X_OFFSCREEN_LEFT);
                    arr[index + 1].setX(X_OFFSCREEN_RIGHT);
                    arr[index + 1].move(X_CENTER);

                    indexes[i/2]++;
                }
            }
        }

        if (backButton.containsXY(p.x, p.y)) {
            setState("START");
        }

        // Check if play button is pressed (and help is not showing)
        else if (!helpActive && playButton.containsXY(p.x, p.y)) {
            setState("PLAY");
        }


        // For showing help
        else if (helpActive) {
            helpShowing = false;
            helpActive = false;
            resetHelp();
        }

        else if (helpButton.containsXY(p.x, p.y) && !helpShowing) {
            helpShowing = true;
            helpSprite.setAlpha(255);
            helpSprite.addActions(
                    helpSprite.QuickExpand(HEIGHT, 15, "EaseOut"),
                    helpSprite.Runner(new Runnable() {
                        public void run() {
                            setHelpActive(true);
                        }
                    })
            );

        }

        return false;
    }

    public void setHelpActive(boolean b) {
        helpActive = b;
    }


    public int[] getSettings() {
        return indexes;
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

