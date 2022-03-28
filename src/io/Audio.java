package io;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import io.AudioManager.Status;

public class Audio {
	AudioInputStream aIStream;
	Long currentFrame;
	Clip clip;
	Status status;
	FloatControl gain;
	String filePath;
	boolean fadeNearEnd; //Whether or not the stream should fade out as it approaches the end of the clip

	public Audio(String filePath, boolean fadeNearEnd) {
		status = Status.Stopped;
		this.filePath = filePath;
		this.fadeNearEnd = fadeNearEnd;
		
		//Set up stream
		try {
			aIStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
			clip = AudioSystem.getClip(); //Make clip reference
			clip.open(aIStream); //Open stream to the clip
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			System.out.println("Audio Stream error");
		}
		gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	}

	public void play() {
		if (status==Status.Stopped) {
			System.out.println("Playing an audio");
			status = Status.Playing;
			gain.setValue(0f);
			clip.setMicrosecondPosition(0);
			clip.start();
			runManager();
		}
	}

	public void fade() {
		if (status==Status.Playing) status = Status.Fading;
	}

	public void forceStop() {
		if (status!=Status.Stopped) {
			System.out.println("Stopping an audio");
			status = Status.Stopped;
			clip.stop();
		}
	}

	/**
	 * Runs a thread which monitors the position in the track and fades if
	 * near end, or if externally asked too.
	 */
	public void runManager() {
		Thread managerThread = new Thread(){
			public void run() {
				try {
					boolean temp = true;
					while (temp) {
						if (fadeNearEnd&&clip.getMicrosecondPosition()-2000000>clip.getMicrosecondLength()) {
							status = Status.Fading; //Near end so fade out
						}
						if (clip.getMicrosecondPosition()>=clip.getMicrosecondLength()-10) {
							forceStop(); //At or past end so stop
						}

						if (status!=Status.Playing) {
							if (status==Status.Fading) { //Not a force stop
								while (gain.getValue()>-80f&&status!=Status.Stopped) {
									gain.setValue(gain.getValue()-0.5f);
									Thread.sleep(20);
								}
								forceStop();
							}
							temp = false;
						}
						Thread.sleep(20);
					}
				}
				catch (InterruptedException e) {System.out.println("Sleep error");}
			}
		};
		managerThread.start();
	}

	public void close() {
		if (aIStream!=null) {
			try {aIStream.close();}
			catch (IOException e) {System.out.println("Audio closing error");}
		}
	}

	public boolean isPlaying() {
		if (status==Status.Playing) return true;
		return false;
	}

	public boolean isFading() {
		if (status==Status.Fading) return true;
		return false;
	}

	public boolean isStopped() {
		if (status==Status.Stopped) return true;
		return false;
	}
}
