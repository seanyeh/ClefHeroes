package com.seanyeh.clefheroes;

public abstract class AnimationAction {
    float startX, startY, startWidth, startHeight;

    // Called by the AbstractSprite in every render() call
    void _act(AbstractSprite s) {
        // Hack to initialize variables on frame = 0
        if (s._frame == 0) {
            startX = s.x;
            startY = s.y;
            startWidth = s.width;
            startHeight = s.height;
        }

        act(s);
    }

    // To be implemented by the subclass
    abstract void act(AbstractSprite s);
}
