package com.jmstudios.serpents;

import com.jmstudios.serpents.SerpentsGame;
import com.jmstudios.serpents.Snake;
import com.jmstudios.serpents.FoodManager;
import com.jmstudios.serpents.IntVector;
import com.jmstudios.serpents.MoveGestureListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.Input.Keys;

import java.util.ArrayList;
import java.util.LinkedList;

/* The screen that displays the main game.
 */
public class GameScreen implements Screen {
    private static final String TAG = "GameScreen";

    private SerpentsGame mGame;

    private Texture testImage;
    private Texture snakeFoodTexture;
    private ArrayList<TextureRegion> snakeFoodTextureRegions;
    private Texture greenSnakeTexture, redSnakeTexture, blueSnakeTexture;

    private OrthographicCamera camera;

    private ArrayList<Snake> snakes;
    private Snake activeSnake;
    private FoodManager foodManager;
    private MoveGestureListener moveGestureListener;

    private float timeSinceStep;

    public static final int snakesAmmount = 3;
    public static final int startLength = 3;
    public static final IntVector boardSize = new IntVector(17, 10);
    public static final IntVector blockSize = new IntVector(48, 48);
    public static final float timeBetweenSteps = 0.2f;

    public GameScreen(SerpentsGame game) {
        this.mGame = game;

        testImage = new Texture(Gdx.files.internal("badlogic.jpg"));

        snakeFoodTexture = new Texture(Gdx.files.internal("snake-food.png"));
        snakeFoodTextureRegions = new ArrayList<TextureRegion>();
        for (int i = 0; i < snakesAmmount; i++) {
            snakeFoodTextureRegions.add
                (new TextureRegion(snakeFoodTexture, 16 * i, 0, 16, 16));
        }

        greenSnakeTexture = new Texture(Gdx.files.internal("snake-green.png"));
        redSnakeTexture = new Texture(Gdx.files.internal("snake-red.png"));
        blueSnakeTexture = new Texture(Gdx.files.internal("snake-blue.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        foodManager = new FoodManager(snakesAmmount, boardSize, blockSize,
                                      snakeFoodTextureRegions);
        snakes = new ArrayList<Snake>();
        for (int i = 0; i < snakesAmmount; i++) {
            Texture snakeTexture = greenSnakeTexture;
            switch (i) {
            case 0: snakeTexture = greenSnakeTexture; break;
            case 1: snakeTexture = redSnakeTexture; break;
            case 2: snakeTexture = blueSnakeTexture; break;
            }

            Snake snake = new Snake
                (new Snake.OnCollisionListener() {
                        @Override
                        public void onSnakeCollision() {
                            mGame.setScreen(new GameScreen(mGame));
                        }

                        @Override
                        public void onFoodCollision
                            (FoodManager.SnakeFood food) {
                            if (snakes.get(food.color) != activeSnake)
                                moveGestureListener.setCurrentDirection
                                    (snakes.get(food.color).getHeadDirection());

                            activeSnake = snakes.get(food.color);
                        }
                    },
                    boardSize, blockSize, foodManager,
                    snakeTexture);
            snake.setSnakeList(snakes);

            LinkedList<IntVector> snakePieces =
                new LinkedList<IntVector>();
            for (int x = startLength - 1; x >= 0; x--) {
                snakePieces.add(new IntVector(x, i));
            }
            snake.setSnakePieces(snakePieces);

            snakes.add(snake);
        }

        foodManager.setSnakes(snakes);
        foodManager.addStartingFood();

        moveGestureListener = new MoveGestureListener();
        Gdx.input.setInputProcessor
            (new GestureDetector(moveGestureListener));

        activeSnake = snakes.get(0);
        moveGestureListener.setCurrentDirection
            (activeSnake.getHeadDirection());

        timeSinceStep = 0.0f;
    }

    public void update(float delta) {
        if (!Gdx.input.isKeyPressed(Keys.P))
            timeSinceStep += delta;
        while (timeSinceStep > timeBetweenSteps) {
            timeSinceStep -= timeBetweenSteps;
            step();
        }
    }

    public void step() {
        Gdx.app.log(TAG, "Applying a step");

        activeSnake.setDirection
            (moveGestureListener.getCurrentDirection());
        activeSnake.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        mGame.batch.setProjectionMatrix(camera.combined);

        mGame.batch.begin();

        for (Snake snake : snakes)
            snake.draw(mGame.batch, testImage);

        foodManager.draw(mGame.batch);

        mGame.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
