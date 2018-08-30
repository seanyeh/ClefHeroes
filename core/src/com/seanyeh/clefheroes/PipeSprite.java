package com.seanyeh.clefheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PipeSprite extends AbstractSprite {
    float centerX;

    public PipeSprite(PlayState g, float inX) {
        // name, x, y, width, height
        super("pipe", inX - g.NOTE_WIDTH, g.HEIGHT - g.NOTE_WIDTH, 2*g.NOTE_WIDTH, g.NOTE_WIDTH);

        centerX = inX;
    }


    public void render(SpriteBatch batch) {
        super.render(batch);
        sprite.setPosition(x, y);
        sprite.setSize(width, height);

        sprite.draw(batch);
    }

    public void swallowNote() {
        addActions(Delay(12), QuickExpand(25, 12), QuickExpand(-25, 12));
    }

}
