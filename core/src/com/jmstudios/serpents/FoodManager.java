package com.jmstudios.serpents;

import com.jmstudios.serpents.IntVector;
import com.jmstudios.serpents.Snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Random;

/* This class manages all food on the board. It is also responsible
 * for drawing the food.
 */
public class FoodManager {
    private IntVector boardSize;
    private IntVector blockSize;
    private int foodAmmount;
    private ArrayList<TextureRegion> foodTextures;

    private ArrayList<SnakeFood> foodOnBoard;
    private ArrayList<Snake> snakes;

    private Random random;

    public FoodManager(int foodAmmount, IntVector boardSize,
                       IntVector blockSize,
                       ArrayList<TextureRegion> foodTextures) {
        foodOnBoard = new ArrayList<SnakeFood>();
        this.boardSize = boardSize;
        this.blockSize = blockSize;

        random = new Random();

        this.foodAmmount = foodAmmount;
        this.foodTextures = foodTextures;
    }

    public void addStartingFood() {
        for (int i = 0; i < foodAmmount; i++) {
            // We don't need to exclude anything, so we pass null.
            addFood(null);
        }
    }

    // Removes food from the position if there is any and returns the
    // food. Also adds a new food if one has been removed.
    public SnakeFood pickUpFoodAt(IntVector position) {
        for (SnakeFood food : foodOnBoard) {
            if (food.position.equals(position)) {
                foodOnBoard.remove(food);
                addFood(food.position.copy());
                return food;
            }
        }

        return null;
    }

    public void draw(SpriteBatch batch) {
        for (SnakeFood food : foodOnBoard) {
            batch.draw(foodTextures.get(food.color),
                       food.position.x * blockSize.x,
                       food.position.y * blockSize.y,
                       blockSize.x, blockSize.y);
        }
    }

    private void addFood(IntVector excluded) {
        int color = random.nextInt(snakes.size());
        while (true) {
            IntVector position =
                new IntVector(random.nextInt(boardSize.x),
                              random.nextInt(boardSize.y));
            boolean positionOccupied = false;

            // Check for all possible obstacles: snakes, food and an
            // excluded place
            for (Snake snake : snakes) {
                for (IntVector piece : snake.getSnakePieces()) {
                    if (piece.equals(position))
                        positionOccupied = true;
                }
            }
            for (SnakeFood food : foodOnBoard) {
                if (food.position.equals(position))
                    positionOccupied = true;
            }
            if (excluded != null && excluded.equals(position))
                positionOccupied = true;

            if (!positionOccupied) {
                foodOnBoard.add
                    (new SnakeFood(position, color));
                return;
            }
        }
    }


    public void setSnakes(ArrayList<Snake> snakes) {
        this.snakes = snakes;
    }

    public class SnakeFood {
        public IntVector position;
        public int color;

        public SnakeFood(IntVector position, int color) {
            this.position = position;
            this.color = color;
        }

        public void draw() {

        }
    }
}
