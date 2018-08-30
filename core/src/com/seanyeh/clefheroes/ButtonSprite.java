package com.seanyeh.clefheroes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ButtonSprite extends AbstractSprite {

    String name;
    Texture texture;

    AbstractState game;

    public ButtonSprite(AbstractState g, String inName, float inX, float inY, float h, boolean isCenteredX, boolean isCenteredY) {
        super(inName, inX, inY, 100, 100);
        width = sprite.getWidth();
        height = sprite.getHeight();

        resizeToHeight(h);

        if (isCenteredX) {
            x -= width/2;
        }

        if (isCenteredY) {
            y -= height/2;
        }

        game = g;
    }

    public ButtonSprite(AbstractState g, String n, float x, float y, float h) {
        this(g, n, x, y, h, true, true);
    }

    public void addBounce() {
        AnimationAction repeat = Runner(new Runnable() {
            public void run() {
                addBounce();
            }
        });

        addActions(GotoPos(x, 45, 20, "EaseIn5"), GotoPos(x, 50, 20, "EaseOut5"), Delay(10), repeat);
    }

    public void render(SpriteBatch batch) {
        super.render(batch);
        sprite.setPosition(x, y);
        sprite.setSize(width, height);

        sprite.draw(batch);

    }

    public boolean containsXY(float x, float y) {
        Rectangle r = sprite.getBoundingRectangle();
        return r.contains(x, y);
    }
}
