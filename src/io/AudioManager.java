package io;

import java.util.HashMap;
import java.util.Map;

import core.Render;

public class AudioManager {

	Render c;
	public enum Status {Playing, Fading, Stopped};
	public enum sFX {Walking, Pushing, Dropping, Fire, Water, Winning, Dying};

	Map<sFX, Audio> audios;
	String[] filePaths = {"moving.wav", "pushing.wav", "dirtdrop.wav", "fire.wav", "water.wav", "winning.wav"};


	public AudioManager(Render c) {
		this.c = c;
		makeAudios();
	}

	public void makeAudios() {
		audios = new HashMap<sFX, Audio>();
		for (int i=0; i<filePaths.length; i++) {
			sFX s = sFX.values()[i];
			Audio a = new Audio("assets/audio/"+filePaths[i], false);
			audios.put(s, a);

		}
	}

	public void run(sFX s) {
		if (audios.get(s)!=null) audios.get(s).play();
	}

	public void fade(sFX s) {
		if (audios.get(s)!=null) audios.get(s).fade();
	}
	
	public void stop(sFX s) {
		if (audios.get(s)!=null) audios.get(s).forceStop();
	}

	public void fadeAll() {
		for (Audio a : audios.values()) {
			if (a.isPlaying()) a.fade();
		}
	}

	public void forceStopAll() {
		for (Audio a : audios.values()) {
			if (a!=null) {
				if (a.isPlaying()) a.forceStop();
			}
		}
	}

	public void closeAll() {
		if (audios!=null) {
			for (Audio a : audios.values()) {
				if (a!=null) {
					a.close();
				}
			}
		}
	}
}
