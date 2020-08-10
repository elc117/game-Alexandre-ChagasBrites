package com.alegz.mermaid;

import com.alegz.mermaid.states.GameState;
import com.alegz.mermaid.states.MenuState;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;

public class MermaidGame extends ApplicationAdapter 
{
	private Assets assets;
	private GameState gameState;
	private GameState newState;
	
	public void create () 
	{
		assets = new Assets();
		assets.load();
		
		SoundManager.init(assets);
		//SoundManager.playMusic();
		
		gameState = new MenuState(this, assets);
		gameState.create();
	}
	
	public void setState(GameState newState)
	{
		this.newState = newState;
	}

	public void render () 
	{
		SoundManager.update();
		
		newState = null;
		gameState.update();
		if (newState != null && newState != gameState)
		{
			gameState.dispose();
			newState.create();
			newState.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			gameState = newState;
		}
		
		if (Gdx.input.isButtonJustPressed(Buttons.LEFT) && !SoundManager.isMusicPlaying())
			SoundManager.playMusic();
	}
	
	public void resize(int width, int height)
	{
		gameState.resize(width, height);
	}
	
	public void pause()
	{
		gameState.pause();
		SoundManager.stopMusic();
	}
	
	public void resume()
	{
		gameState.resume();
		SoundManager.playMusic();
	}
	
	public void dispose () 
	{
		gameState.dispose();
		assets.dispose();
	}
}
