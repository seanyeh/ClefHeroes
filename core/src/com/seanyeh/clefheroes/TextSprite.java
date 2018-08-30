package com.seanyeh.clefheroes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextSprite extends AbstractSprite {
    private int alpha;

    AbstractState game;

    boolean isFading;

    public TextSprite(AbstractState g, String s, float inX, float inY, float h, boolean isCenteredX, boolean isCenteredY) {
        super(s, inX, inY, 0, 0);
        width = sprite.getWidth();
        height = sprite.getHeight();

        resizeToHeight(h);

        // Center
        if (isCenteredX) {
            x -= width / 2;
        }

        if (isCenteredY) {
            y -= height / 2;
        }

        alpha = 255;

        game = g;

        isFading = false;
    }

    public TextSprite(AbstractState g, String s, float inX, float inY, float h) {
        this(g, s, inX, inY, h, true, true);
    }


    public void render(SpriteBatch batch) {
        super.render(batch);

//        if (isFading) {
//            alpha = Math.max(0, alpha - 2);
//        }
//        else {
//            alpha = Math.min(255, alpha + 2);
//        }

        sprite.setAlpha((float) (alpha / 255.0));
        sprite.setPosition(x, y);
        sprite.setSize(width, height);

        sprite.draw(batch);
    }

    public void move(float inX) {
        this.addActions(GotoPos(inX - width/2, y, 30, "EaseOut"));
    }

    public void setX(float inX) {
        x = inX - width/2;
    }

    public void setY(float inY) {
        y = inY - height / 2;
    }

    public void setAlpha(int a) {
        alpha = a;
    }
}
