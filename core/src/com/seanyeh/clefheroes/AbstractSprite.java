package com.seanyeh.clefheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public abstract class AbstractSprite {
    protected float x, y;
    protected float width, height;

    protected Sprite sprite;
    protected String name;

    public boolean isFinished;

    protected int _frame = 0;

    protected Array<AnimationAction> actions;

    public AbstractSprite(String imgName, float inX, float inY, float inWidth, float inHeight) {
        Texture texture = new Texture(Gdx.files.internal("data/" + imgName + ".png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(texture);

        name = imgName;
        x = inX;
        y = inY;
        width = inWidth;
        height = inHeight;

        isFinished = false;

        actions = new Array<AnimationAction>();
    }

    public void resizeToHeight(float h) {
        double ratio = h/height;
        width = (float)(width*ratio);
//        y += (height-h)/2;
        height = h;
    }

    public void addActions(AnimationAction ... actionArr) {
        for (AnimationAction a: actionArr) {
            actions.add(a);
        }
    }

    public void nextAction() {
        if (actions.size > 0) {
            actions.removeIndex(0);
        }
        _frame = 0;
    }

    public void render(SpriteBatch batch) {
        if (actions.size > 0) {
            actions.first()._act(this);
        }

    }

    public void setSize(float w, float h) {
        width = w;
        height = h;
    }

    // Getters/Setters
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float inX) { x = inX; }
    public void setY(float inX) { x = inX; }


    // =======
    // Actions
    // =======

    public AnimationAction GotoPos(float tX, float tY, float inDuration, String inEasing) {
        final float targetX = tX;
        final float targetY = tY;
        final float duration = inDuration;
        final String easing = inEasing;

        return new AnimationAction() {
            public void act(AbstractSprite sprite) {
                float newX = Ease(_frame, startX, targetX, duration, easing);
                float newY = Ease(_frame, startY, targetY, duration, easing);

                if (_frame++ >= duration) {
                    sprite.nextAction();
                }

                sprite.x = newX;
                sprite.y = newY;
            }
        };
    }

    public AnimationAction QuickExpand(float inGrowth, float inDuration) {
        return QuickExpand(inGrowth, inDuration, "EaseOut");
    }

    public AnimationAction QuickExpand(float inGrowth, float inDuration, String inEasing) {
        final float growth = inGrowth;
        final float duration = inDuration;
        final String easing = inEasing;

        return new AnimationAction() {

            public void act(AbstractSprite sprite) {
                sprite.width = Ease(_frame, startWidth, startWidth + growth, duration, easing);
                sprite.height = Ease(_frame, startHeight, startHeight + growth, duration, easing);

                // Modify x,y so that the center stays the same
                float xDelta = (sprite.width - startWidth)/2;
                float yDelta = (sprite.height - startHeight)/2;

                sprite.x = startX - xDelta;
                sprite.y = startY - yDelta;


                if (_frame++ >= duration) {
                    sprite.nextAction();
                }
            }
        };
    }


    public AnimationAction FadeInRight(float inDuration, float tX) {
        final float targetX = tX;
        final float duration = inDuration;

        return new AnimationAction() {
            public void act(AbstractSprite sprite) {
                float newX = Ease(_frame, startX, targetX, duration, "EaseOut");

                if (_frame++ >= duration) {
                    sprite.nextAction();
                }

                sprite.x = newX;
            }
        };
    }

    public AnimationAction FadeOutLeft(float inDuration) {
        float duration = inDuration;
        return new AnimationAction() {
            public void act(AbstractSprite sprite) {

            }
        };
    }

    public AnimationAction Delay(int inDelay) {
        final int delay = inDelay;
        return new AnimationAction() {
            public void act(AbstractSprite sprite) {
                if (_frame++ > delay) {
                    sprite.nextAction();
                }
            }
        };
    }

    public AnimationAction DeleteMe() {
        return new AnimationAction() {
            public void act(AbstractSprite sprite) {
                sprite.isFinished = true;
            }
        };
    }

    public AnimationAction Runner(Runnable inR) {
        final Runnable r = inR;
        return new AnimationAction() {
            public void act(AbstractSprite sprite) {
                r.run();
                sprite.nextAction();
            }
        };
    }

    // ================
    // Easing Functions
    // ================
    // http://upshots.org/actionscript/jsas-understanding-easing

    public float Ease(float a, float b, float c, float d, String method) {
        if (method.equals("EaseOut")) {
            return EaseOut(a, b, c, d, 10);
        }
        else if (method.equals("EaseIn")) {
            return EaseIn(a, b, c, d, 10);
        }
        else if (method.equals("EaseOut5")) {
            return EaseOut(a, b, c, d, 5);
        }
        else if (method.equals("EaseIn5")) {
            return EaseIn(a, b, c, d, 5);
        }
        else {
            return Linear(a, b, c, d);
        }

    }

    public float Linear(float timeElapsed, float start , float end, float duration) {
        float diff = end - start;
        return (timeElapsed >= duration) ? end : diff * (timeElapsed/duration) + start;
    }

    public float EaseOut(float timeElapsed, float start , float end, float duration, int exp) {
        float diff = end - start;
        return (timeElapsed >= duration) ? end : diff * (-(float)Math.pow(2, -exp * timeElapsed/ duration) + 1) + start;
    }

    public float EaseIn(float timeElapsed, float start , float end, float duration, int exp) {
        float diff = end - start;
        return (timeElapsed == 0) ? start : diff * (float)Math.pow(2, exp * (timeElapsed/ duration - 1)) + start;
    }

//    public float easeIn(float t,float b , float c, float d) {
//        return (t==0) ? b : c * (float)Math.pow(2, 10 * (t/d - 1)) + b;
//    }

}
