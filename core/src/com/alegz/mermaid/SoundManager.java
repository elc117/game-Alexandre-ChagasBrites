package com.alegz.mermaid;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;

public class SoundManager 
{
	private static Assets assets;
	private static ArrayList<Sound> sounds;
	private static HashMap<Sound, Float> soundsVolume;
	
	public static void init(Assets assets)
	{
		SoundManager.assets = assets;
		SoundManager.sounds = new ArrayList<>();
		SoundManager.soundsVolume = new HashMap<>();
	}
	
	public static void update()
	{
		for (Sound sound : sounds)
		{
			Float volume = soundsVolume.get(sound);
			if (volume != null)
				sound.play(volume);
			else
				sound.play();
		}
		sounds.clear();
		soundsVolume.clear();
	}
	
	public static void play(String name)
	{
		Sound sound = assets.getSound(name);
		if (sound != null && !sounds.contains(sound))
			sounds.add(sound);
		//	sound.play();
	}
	
	public static void play(String name, float volume)
	{
		Sound sound = assets.getSound(name);
		if (sound != null && !sounds.contains(sound))
		{
			sounds.add(sound);
			soundsVolume.put(sound, volume);
		}
		//	sound.play(volume);
	}
	
	public static void playMusic()
	{
		assets.getMusic().setLooping(true);
		assets.getMusic().play();
	}
	
	public static void stopMusic()
	{
		assets.getMusic().pause();
	}
	
	public static boolean isMusicPlaying()
	{
		return assets.getMusic().isPlaying();
	}
}
