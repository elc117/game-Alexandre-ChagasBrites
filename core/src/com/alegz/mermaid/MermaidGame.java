package com.alegz.mermaid;

import com.alegz.mermaid.rendering.Renderer;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.states.GameState;
import com.alegz.mermaid.states.MenuState;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;

public class MermaidGame implements ApplicationListener
{
	private Assets assets;
	
	private Renderer renderer;
	private float t;
	
	private GameState gameState;
	private GameState transitionState;
	
	@Override
	public void create () 
	{
		assets = new Assets();
		assets.load();
		
		renderer = new Renderer(1);
		renderer.setProjectionMatrix(new Matrix4().scl(2.0f, 2.0f, 1.0f));
		
		SoundManager.init(assets);
		SoundManager.playMusic();
		
		changeState(new MenuState(this, assets));
	}

	@Override
	public void render () 
	{
		gameState.update();
		if (transitionState != null)
			transition();
		SoundManager.update();
	}
	
	public void setState(GameState newState)
	{
		if (transitionState != null || newState == null)
			return;
		transitionState = newState;
		t = -1.0f;
	}
	
	private void changeState(GameState newState)
	{
		if (newState == null)
			return;
		if (gameState != null)
			gameState.dispose();
		newState.create();
		newState.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gameState = newState;
	}
	
	private void transition()
	{
		float cutout = GameUtils.easeStep(Math.abs(t));
		float flip = t < 0.0f ? 0.0f : 1.0f;
		
		Material transition = assets.getMaterial(Assets.MATERIAL_TRANSITION);
		transition.setFloat("u_cutout", cutout);
		transition.setFloat("u_flip", flip);
		transition.setVector2("u_size", 6.0f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), 6.0f);
		
		renderer.begin();
		renderer.setMaterial(transition);
		renderer.draw();
		renderer.end();
		
		float oldT = t;
		t += Gdx.graphics.getDeltaTime();
		if (oldT < 0.0f && t >= 0.0f)
			changeState(transitionState);
		else if (oldT < 1.0f && t >= 1.0f)
			transitionState = null;
	}
	
	@Override
	public void resize(int width, int height)
	{
		gameState.resize(width, height);
	}
	
	@Override
	public void pause()
	{
		gameState.pause();
		SoundManager.stopMusic();
	}
	
	@Override
	public void resume()
	{
		gameState.resume();
		SoundManager.playMusic();
	}
	
	@Override
	public void dispose () 
	{
		gameState.dispose();
		assets.dispose();
	}
}
