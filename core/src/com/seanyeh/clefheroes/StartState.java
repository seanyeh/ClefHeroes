package com.seanyeh.clefheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import java.util.Random;

public class StartState extends AbstractState implements InputProcessor {
    OrthographicCamera camera;

    SpriteBatch batch;
    ButtonSprite playButton, aboutButton;

    TextSprite aboutSprite;
    boolean aboutActive, aboutShowing;

    Sprite logo;

    float YUNIT;

    // Clefhero animation
    Sprite clefhero;
    float clefheroVelY;
    boolean clefheroMoving;
    float gravity, startVel;

    Random R = new Random();


    public StartState(ClefHeroesGame main) {
        super(main);
    }

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        YUNIT = HEIGHT/12;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        Texture logoTexture = new Texture(Gdx.files.internal("data/logo.png"));
        logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        logo = new Sprite(logoTexture);

        float logoHeight = (float)(0.75 * HEIGHT);

        if (logo.getHeight() > logoHeight) {
            logo.setSize(logoHeight/logo.getHeight() * logo.getWidth(), logoHeight);
        }
        logo.setY(HEIGHT - logo.getHeight());
        logo.setCenterX(WIDTH/2);

        batch = new SpriteBatch();

        aboutButton = new ButtonSprite(this, "about", WIDTH - WIDTH/16, HEIGHT - HEIGHT/8, HEIGHT/12);
        playButton = new ButtonSprite(this, "play", WIDTH/2, HEIGHT/8, HEIGHT/5);

        Gdx.input.setInputProcessor(this);

        aboutShowing = false;
        aboutActive = false;
        aboutSprite = new TextSprite(this, "aboutdialog", WIDTH/2, HEIGHT/2, 1);
        resetAbout();

        // Show clefhero :)
        Texture texture = new Texture(Gdx.files.internal("data/blob6.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        clefhero = new Sprite(texture);
        clefhero.setSize(2*YUNIT, (float)(1.7*YUNIT));
        clefhero.setPosition(WIDTH - 3*YUNIT, YUNIT);
        startVel = (float)(0.7*YUNIT);
        clefheroVelY = startVel;
        clefheroMoving = true;
        gravity = YUNIT/30;
    }

    public void resetAbout() {
        aboutSprite.setSize(1, 1);
        aboutSprite.setX(WIDTH/2);
        aboutSprite.setY(HEIGHT/2);
        aboutSprite.setAlpha(0);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();
        batch.begin();
        batch.setProjectionMatrix(camera.combined);


        logo.draw(batch);

        aboutButton.render(batch);
        playButton.render(batch);

        aboutSprite.render(batch);

        /*
         * Clefhero animation
         */

        if (clefheroMoving) {
            // Add gravity for clefhero
            clefheroVelY -= gravity;
            float newY = clefhero.getY() + clefheroVelY;
            // YUNIT is the lowest it can go, then bounce up
            if (newY < YUNIT) {
                clefheroVelY *= -0.8;
                newY = YUNIT;

                if (Math.abs(clefheroVelY) < startVel/50) {
                    clefheroMoving = false;
                }
            }
            clefhero.setY(newY);
        }
        else {
            int i = R.nextInt(300);
            if (i == 0) {
                clefheroMoving = true;
                clefheroVelY = startVel;
            }
        }


        clefhero.draw(batch);

        batch.end();
    }

    public void setAboutActive(boolean b) {
        aboutActive = b;
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

        if (aboutActive) {
            // If clicking link:
            // Hack:
            //  y: the link is in the bottom 15% of the dialog
            //  x: distance from center is < half width of dialog
            if (p.y < 0.15*HEIGHT && Math.abs(p.x - WIDTH/2) < aboutSprite.width/2) {
                Gdx.net.openURI("https://clefheroes.seanyeh.com");
            }
            else {
                aboutShowing = false;
                aboutActive = false;
                resetAbout();
            }
        }

        else if (aboutButton.containsXY(p.x, p.y) && !aboutShowing) {
            aboutShowing = true;
            aboutSprite.setAlpha(255);
            aboutSprite.addActions(
                    aboutSprite.QuickExpand(HEIGHT, 15, "EaseOut"),
                    aboutSprite.Runner(new Runnable() {
                        public void run() {
                            setAboutActive(true);
                        }
                    })
            );
        }
        else if (playButton.containsXY(p.x, p.y)) {
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
