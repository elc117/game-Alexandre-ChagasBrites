package com.alegz.mermaid.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.alegz.mermaid.MermaidGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.pauseWhenBackground = true;
		config.pauseWhenMinimized = true;
		new LwjglApplication(new MermaidGame(), config);
	}
}
