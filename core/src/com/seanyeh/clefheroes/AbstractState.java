package com.seanyeh.clefheroes;

import com.badlogic.gdx.ApplicationAdapter;

public abstract class AbstractState extends ApplicationAdapter {
    public float WIDTH, HEIGHT;

    protected ClefHeroesGame MAIN;

    boolean isActive = true;

    public AbstractState(ClefHeroesGame main) {
        MAIN = main;
    }

    public void setState(String st) {
        if (isActive) {
            MAIN.setState(st);
            deactivate();
        }
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }
}
