package com.seanyeh.clefheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Clef {
    int MIDI_OFFSETS[] = new int[]{0, 2, 3, 5, 7, 9, 10};

    // The "accidental" notes aren't used
    String NAMES[] = {"C", "C", "D", "D", "E", "F", "F", "G", "G", "A", "A", "B"};

    String name;

    public Texture texture;
    public int numOffset, octaveOffset, origWidth, origHeight;
    public double spriteScaleRatio, spriteOffsetRatio;

    Sprite sprite;

    public Clef(String inName, int inNumOffset, int inOctaveOffset, String spriteName,
                double inSpriteScaleRatio, double inSpriteOffsetRatio,
                int inOrigWidth, int inOrigHeight) {

        name = inName;
        texture = new Texture(Gdx.files.internal("data/" + spriteName + ".png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(texture);

        numOffset = inNumOffset;
        octaveOffset = inOctaveOffset;
        spriteScaleRatio = inSpriteScaleRatio;
        spriteOffsetRatio = inSpriteOffsetRatio;
        origWidth = inOrigWidth;
        origHeight = inOrigHeight;
    }


    public static Clef BASS() {
        return new Clef("Bass", 0, 0, "bass", 3.25, 2.75, 256, 296);
    }
    public static Clef BARITONE() {
        return new Clef("Baritone", 2, 0, "bass", 3.25, 1.75, 256, 296);
    }
    public static Clef TENOR() {
        return new Clef("Tenor", 4, 0, "cclef", 4, 3, 251, 378);
    }
    public static Clef ALTO() {
        return new Clef("Alto", 6, 0, "cclef", 4, 2, 251, 378);
    }
    public static Clef MEZZO() {
        return new Clef("Mezzo", 1, 1, "cclef", 4, 1, 251, 378);
    }
    public static Clef SOPRANO() {
        return new Clef("Soprano", 3, 1, "cclef", 4, 0, 251, 378);
    }
    public static Clef TREBLE() {
        return new Clef("Treble", 5, 1, "treble", 7.5, (1.0 / 3), 228, 644);
    }

    public void setSizePosition(PlayState g) {
        double scaleRatio = spriteScaleRatio*g.NOTE_HEIGHT / origHeight;
        float clefX = 0;
        float clefY = (int)(g.STAFF_Y + spriteOffsetRatio*g.NOTE_HEIGHT);
        float clefWidth = (int)(scaleRatio * origWidth);
        float clefHeight = (int)(scaleRatio * origHeight);

        sprite.setSize(clefWidth, clefHeight);
        sprite.setPosition(clefX, clefY);
    }

    public int toMidi(Note n) {
        // Bass is the lowest note, D (below 1st ledger)

        int normIndex = n.getNoteIndex() + numOffset + (7 * octaveOffset);
        int numOctaves = normIndex/7;

        int midiAddend = MIDI_OFFSETS[normIndex - (7*numOctaves)];

        return 38 + midiAddend + 12*numOctaves; // 37 is low D
    }

    public String toNoteName(Note n) {
        int midi = toMidi(n);
        return NAMES[midi%12];
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public String toString() {
        return name;
    }
}

