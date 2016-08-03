package com.jmstudios.serpents;

import com.jmstudios.serpents.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SerpentsGame extends Game {
    public SpriteBatch batch;

    @Override
    public void create () {
        batch = new SpriteBatch();

        this.setScreen(new GameScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }
	
    @Override
    public void dispose () {
        batch.dispose();
    }
}
