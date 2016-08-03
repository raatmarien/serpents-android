package com.jmstudios.serpents.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jmstudios.serpents.SerpentsGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.title = "Serpents";
                config.width = 800;
                config.height = 480;
                new LwjglApplication(new SerpentsGame(), config);
	}
}
