package com.seanyeh.clefheroes;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NumberSprite {
    Array<TextSprite> digitSprites;

    public NumberSprite(AbstractState g, int number, float inX, float inY, float h) {

        // Init array
        digitSprites = new Array<TextSprite>();

        // All digit images are 1:2 aspect ratio
        float digitWidth = h/2;
        char[] digits = Integer.toString(number).toCharArray();

        for (int i = 0; i < digits.length; i++) {
            String s = Character.toString(digits[i]);
            TextSprite t = new TextSprite(g, "digits/" + s, inX + i*digitWidth, inY, h, false, false);
            digitSprites.add(t);
        }
    }

    public void render(SpriteBatch batch) {
        // Draw each sprite
        for (TextSprite t: digitSprites) {
            t.render(batch);
        }
    }
}
