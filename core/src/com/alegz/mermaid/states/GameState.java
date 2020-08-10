package com.alegz.mermaid.states;

public interface GameState 
{
	public void create();
	public void update();
	public void resize(int width, int height);
	public void pause();
	public void resume();
	public void dispose();
}
