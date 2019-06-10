package com.seanyeh.clefheroes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NoteRingSprite extends AbstractSprite {
    private Note note;

    public NoteRingSprite(Note n) {
        super("circle" + n.getColor(), n.getX(), n.getY(), n.getWidth(), n.getHeight());
        note = n;
    }

    public void render(SpriteBatch batch) {
        if (note.getRingGradient() == 0) { return; }

        sprite.setAlpha(note.getRingGradient()*0.6f);

        // Get center of note
        float centerX = note.getX() + note.getWidth()/2;
        float centerY = note.getY() + note.getHeight()/2;

        // Make width 1.2x of note and keep same center as note
        float width = (float)(1.5 * note.getWidth());

        sprite.setSize(width, width);
        sprite.setPosition(centerX - width/2, centerY - width/2);
        sprite.draw(batch);
    }
}
