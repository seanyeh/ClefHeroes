package com.seanyeh.clefheroes;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;


public class ClefHeroesGame extends ApplicationAdapter {

//	ApplicationAdapter PLAYSTATE, STARTSTATE, MENUSTATE, RESULTSSTATE;

	ApplicationAdapter state;
	String currentStateName = "";

	private Sound[] KEYSOUNDS;
	private int[] lastSettings;

	@Override
	public void create () {
		state = null;
		loadSounds();

		setState("START");
	}

	@Override
	public void render () {
		state.render();
	}

	public void setState(String newStateName) {



		if (state != null) {
			state.dispose();
		}

		System.out.println("setState");

		AbstractState newState;
		if (newStateName.equals("MENU")) {
			newState = new MenuState(this);
		}
		else if (newStateName.equals("PLAY")) {
			newState = new PlayState(this);
		}
		else if (newStateName.equals("RESULTS")) {
			newState = new ResultsState(this);
		}
		else {
		    newState = new StartState(this); // default
		}

	    newState.create();
		newState.activate();

		if (newStateName.equals("PLAY")) {
			int[] settings = lastSettings;
			if (currentStateName.equals("MENU")) {
				System.out.println("using new menu settings");
				settings = ((MenuState)state).getSettings();
				System.out.println("clef index: " + settings[1]);
				lastSettings = settings;
			}

			((PlayState)newState).setSettings(settings[0], settings[1], settings[2]);
		}

		if (newStateName.equals("RESULTS")) {
			PlayState p = (PlayState)state;
			Array<Note> finishedNotes = p.getResults();
			String clef = p.getClef();
			String speed = p.getSpeed();

			((ResultsState)newState).setSettings(finishedNotes, clef, speed);
		}


		state = newState;
		currentStateName = newStateName;
	}

	public void loadSounds() {
		// Setup Audio
		KEYSOUNDS = new Sound[88];
		for (int i = 0; i <= 88; i++) {
			// Only fill in 36-84 since that's all we need
			if (i < 36 || i > 84) { continue; }

			String filename = i + ".wav";
			if (i < 10) {
				filename = "0" + filename;
			}

			KEYSOUNDS[i] = Gdx.audio.newSound(Gdx.files.internal("data/sounds/" + filename));
		}
	}

	public void playSound(int midi) {
		KEYSOUNDS[midi].play();
	}
}
