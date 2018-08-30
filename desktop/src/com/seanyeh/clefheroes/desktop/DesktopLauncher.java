package com.seanyeh.clefheroes.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.seanyeh.clefheroes.ClefHeroesGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Clef Heroes";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new ClefHeroesGame(), config);
	}
}
