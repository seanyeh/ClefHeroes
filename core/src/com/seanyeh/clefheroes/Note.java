package com.seanyeh.clefheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;


public class Note extends AbstractSprite {
    Texture noteTexture;

//    public int x, y;


    private int midi, noteIndex, ledger;
    private Status noteStatus, answerStatus;

    PlayState game;

    private boolean isMoving;

    public Note(PlayState g, int index) {
        super("blob" + MathUtils.random(1, 6), g.WIDTH, g.STAFF_Y + index*(g.NOTE_HEIGHT/2), g.NOTE_WIDTH, g.NOTE_HEIGHT);

        game = g;
        noteIndex = index;

        noteStatus = Status.NONE;
        answerStatus = Status.NONE;

        isMoving = true;

        // Ledger lines: -1 means no ledger
        ledger = -1;
        if (noteIndex == 14) {
            ledger = 0;
        }
        if (noteIndex == 1 || noteIndex == 13) {
            ledger = 1;
        }
        if (noteIndex == 0) {
            ledger = 2;
        }


        midi = 60;
    }

    public int getLedger() {
        return ledger;
    }

    public int getNoteIndex() {
        return noteIndex;
    }

    public Texture getTexture() {
        return noteTexture;
    }

    public void setNoteStatus(Status s) {
        noteStatus = s;
    }

    public void setAnswerStatus(Status s) {
        answerStatus = s;
    }

    public Status getNoteStatus() {
        return noteStatus;
    }

    public Status getAnswerStatus() {
        return answerStatus;
    }

    public void render(SpriteBatch batch) {
        super.render(batch);

        // Draw

        if (isMoving) {
            x -= (game.SPEED * game.BEAT_WIDTH) * Gdx.graphics.getDeltaTime();
        }
        sprite.setPosition(x, y);
        sprite.setSize(width, height);


        // If ledger line
        if (ledger >= 0) {
            float ledgerY = y + ledger * (game.NOTE_HEIGHT/2);
            batch.draw(game.pixelTexture, x - game.NOTE_WIDTH/4, ledgerY, (int)(1.5*game.NOTE_WIDTH), 1);
        }


        // blob 380x281
//        batch.draw(getTexture(), x, y, game.NOTE_WIDTH, game.NOTE_HEIGHT);
        sprite.draw(batch);

        // Set status
        double targetX = game.BEAT_WIDTH;
        double noteCenterX = x + .5*game.NOTE_WIDTH;

        double dist = noteCenterX - targetX;
        double x = game.BEAT_WIDTH/10.0;

        /*
         * In units of BEAT_WIDTH/10
         *
         * -2 - 2 => Perfect
         * -3 - 3 => Good
         *
         * -4 - -3 => Too Late
         * 4 - 3 => Too Early
         */

        if (dist >= -2*x && dist <= 2*x) {
            // if (firstNote.noteStatus !== Status.Perfect) { console.log("Perfect"); }

            // set note status to perfect
            setNoteStatus(Status.PERFECT);

        }
        else if (dist >= -3*x && dist <= 3*x) {
            // if (firstNote.noteStatus !== Status.Good) { console.log("Good"); }

            // good
            setNoteStatus(Status.GOOD);
        }
        else if (dist >= -4*x && dist <= -3*x) {
            // if (firstNote.noteStatus !== Status.TooLate) { console.log("TooLate"); }

            // too late
            setNoteStatus(Status.TOOLATE);
        }
        else if (dist >= -4*x && dist <= 4*x) {
            // Set current note
//				if (currentNote !== firstNote) {
//					// console.log("Set current note");
//					this.currentNote = firstNote;
//				}

            // if (firstNote.noteStatus !== Status.TooEarly) { console.log("TooEarly"); }
            // too early
            setNoteStatus(Status.TOOEARLY);
        }
        else if (dist <= -4*x) {
            // if (firstNote.noteStatus !== Status.Missed) { console.log("Missed"); }

            // Missed!
            if (getAnswerStatus() == Status.NONE) {
                setNoteStatus(Status.MISSED);
                game.showNoteResult(this, Status.MISSED);
            }

            isFinished = true;
        }



    }

    public void gotoPipe() {
        isMoving = false;
        float x = game.pipeSprite.centerX - game.NOTE_WIDTH/2;
        float y = game.pipeSprite.y + game.pipeSprite.getHeight()/2;
        addActions(GotoPos(x, y, 50, "EaseOut"), DeleteMe());
    }

    public static Note genRandomNote(PlayState g) {
        int noteIndex = MathUtils.random(0, 14);

        return new Note(g, noteIndex);
    }
}
