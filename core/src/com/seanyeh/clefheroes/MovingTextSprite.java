package com.seanyeh.clefheroes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MovingTextSprite extends AbstractSprite {
    private int alpha;

    AbstractState game;

    public MovingTextSprite(AbstractState g, String s, float inX, float inY, float h) {
        super(s, inX, inY, 0, 0);
        width = sprite.getWidth();
        height = sprite.getHeight();
        resizeToHeight(h);

        game = g;
        alpha = 255;

        addActions(GotoPos(x + g.WIDTH/2, y, 100, "Linear"));
    }

    public void render(SpriteBatch batch) {
        super.render(batch);

        sprite.setAlpha((float)(alpha/255.0));
        sprite.setPosition(x, y);
        sprite.setSize(width, height);

        alpha -= 5;

        if (alpha < 0) {
            alpha = 0;
            isFinished = true;
        }

        sprite.draw(batch);
    }

}
