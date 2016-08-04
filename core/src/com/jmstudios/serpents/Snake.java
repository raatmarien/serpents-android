package com.jmstudios.serpents;

import com.jmstudios.serpents.IntVector;
import com.jmstudios.serpents.FoodManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.ArrayList;

/* This class holds the placement info of the snake in an ordered
 * list. It is also responsible for drawing itself, moving itself if
 * active and collision detection if active.
 * 
 * Snakes are always more then one piece long.
 */
public class Snake {
    public static final String TAG = "Snake";
    
    private OnCollisionListener collisionListener;
    private ArrayList<TextureRegion> snakeTextures;

    // This list contains all the snake pieces that make up this
    // snake, beginning with the head and ending with the tail.
    private LinkedList<IntVector> snakePieces;
    private Direction snakeDirection;

    // This list contains all snakes in the game *including* this
    // snake itself. It is null until it is set with setSnakeList
    private ArrayList<Snake> snakes;
    private FoodManager foodManager;
    private IntVector boardSize;
    private IntVector blockSize;
    
    public Snake(OnCollisionListener collisionListener,
                 IntVector boardSize,
                 IntVector blockSize,
                 FoodManager foodManager,
                 Texture snakeTexture) { 
        this.collisionListener = collisionListener;
        this.boardSize = boardSize;
        this.blockSize = blockSize;
        this.foodManager = foodManager;

        snakeTextures = new ArrayList<TextureRegion>();
        snakeTextures.add(new TextureRegion(snakeTexture, 0, 0, 16, 16));
        snakeTextures.add(new TextureRegion(snakeTexture, 16, 0, 16, 16));
        snakeTextures.add(new TextureRegion(snakeTexture, 0, 16, 16, 16));
        snakeTextures.add(new TextureRegion(snakeTexture, 16, 16, 16, 16));
    }

    // Only the active snake needs to be updated. setDirection should
    // always be called before the snake is updated.
    public void update() {
        // The tail needs to be saved, because if it has been removed
        // and the snake crashes this 'turn' then it needs to be put
        // back.
        IntVector oldTail = snakePieces.get(snakePieces.size() - 1).copy();

        IntVector newHeadPosition = snakePieces.get(0).copy()
            .addWithBounds(directionToVector(snakeDirection), boardSize);
        boolean foodPickedUp = pickUpFood(newHeadPosition);

        // Only if food has been picked up, then the last part of the
        // snake doesn't have to be removed.
        if (!foodPickedUp) {
            snakePieces.remove(snakePieces.size() - 1);
        }

        if (doesSnakeCollide(newHeadPosition)) {
            Gdx.app.log(TAG, "Collision with a snake!");

            collisionListener.onSnakeCollision();

            if (!foodPickedUp) {
                // If the food hasn't been picked up, so the tail has
                // been removed. Then it has to be put back, because
                // the snake can't move forward in this case.
                snakePieces.add(oldTail);
            }
        } else {
            // Move forward by adding a new piece at the front of the
            // list
            snakePieces.add(0, newHeadPosition);
        }
    }

    public void draw(SpriteBatch batch, Texture img) {
        IntVector origin = new IntVector(blockSize.x / 2, blockSize.y / 2);
        Vector2 scale = new Vector2(((float) blockSize.x) / 16.0f,
                                    ((float) blockSize.y) / 16.0f);
        scale.x = scale.y = 1.0f;
        
        // Draw the head
        TextureRegion head = snakeTextures.get(0);
        float rotation = directionToDegrees
            (getDirectionBetween(snakePieces.get(1), snakePieces.get(0)));
        IntVector headPos = new IntVector(snakePieces.get(0).x * blockSize.x,
                                           snakePieces.get(0).y * blockSize.y);

        batch.draw(head, headPos.x, headPos.y, origin.x, origin.y, blockSize.x, blockSize.y,
                   scale.x, scale.y, rotation);

        // Draw the body
        for (int i = 1; i < snakePieces.size() - 1; i++) {
            // IntVector piece = snakePieces.get(i);
            // IntVector n1 = snakePieces.get(i - 1);
            // IntVector n2 = snakePieces.get(i + 1);
            // IntVector nDif = n1.copy().substract(n2);
            // IntVector pos = new IntVector(piece.x * blockSize.x,
            //                               piece.y * blockSize.y);
            
            // if (nDif.y == 0 || nDif.x == 0) {
            //     TextureRegion straightBody = snakeTextures.get(1);
            //     rotation = nDif.y == 0 ? 90.0f : 0.0f;

            //     batch.draw(straightBody, pos.x, pos.y, origin.x, origin.y,
            //                blockSize.x, blockSize.y, scale.x, scale.y, rotation);
            // } else {
            //     TextureRegion curve = snakeTextures.get(2);

            //     IntVector n1D = n1.copy().substract(pos);
            //     IntVector n2D = n2.copy().substract(pos);

            //     if (n1D.y == -1 && n2D.x == 1) rotation = 0.0f;
            //     if (n1D.y == 1 && n2D.x == 1) rotation = 90.0f;
            //     if (n1D.y == 1 && n2D.x == -1) rotation = 180.0f;
            //     if (n1D.y == -1 && n2D.x == -1) rotation = 270.0f;
            //     if (n1D.x == -1 && n2D.y == -1) rotation = 0.0f;
            //     if (n1D.x == -1 && n2D.y == 1) rotation = 90.0f;
            //     if (n1D.x == 1 && n2D.y == 1) rotation = 180.0f;
            //     if (n1D.x == 1 && n2D.y == -1) rotation = 270.0f;

            //     batch.draw(curve, pos.x, pos.y, origin.x, origin.y,
            //                blockSize.x, blockSize.y, scale.x, scale.y, rotation);
            // }

            IntVector current = snakePieces.get(i),
                next = snakePieces.get(i + 1),
                previous = snakePieces.get(i - 1),
                pos = new IntVector (current.x * blockSize.x,
                                          current.y * blockSize.y);
            Direction toNext = getDirectionBetween(next, current),
                fromPrevious = getDirectionBetween(current, previous);

            if (toNext == fromPrevious) {
                TextureRegion straightBody = snakeTextures.get(1);
                rotation = directionToDegrees(toNext);

                batch.draw(straightBody, pos.x, pos.y, origin.x, origin.y,
                           blockSize.x, blockSize.y, scale.x, scale.y, rotation);
            } else {
                TextureRegion curve = snakeTextures.get(2);
                Direction toPrevious = getDirectionBetween(previous, current),
                    first = toNext.ordinal() < toPrevious.ordinal()
                    ? toNext : toPrevious,
                    second = toNext.ordinal() < toPrevious.ordinal()
                    ? toPrevious : toNext;

                if (first == Direction.UP && second == Direction.RIGHT)
                    rotation = 0f;
                else if (first == Direction.RIGHT && second == Direction.DOWN)
                    rotation = 270f;
                else if (first == Direction.DOWN && second == Direction.LEFT)
                    rotation = 180f;
                else if (first == Direction.UP && second == Direction.LEFT)
                    rotation = 90f;
                
                  batch.draw(curve, pos.x, pos.y, origin.x, origin.y,
                           blockSize.x, blockSize.y, scale.x, scale.y, rotation);
            }
        }

        // Draw the tail
        TextureRegion tail = snakeTextures.get(3);
        rotation = directionToDegrees
            (getDirectionBetween(snakePieces.get(snakePieces.size() - 1),
                                 snakePieces.get(snakePieces.size() - 2)));
        IntVector tailPos = new IntVector
            (snakePieces.get(snakePieces.size() - 1).x * blockSize.x,
             snakePieces.get(snakePieces.size() - 1).y * blockSize.y);

        batch.draw(tail, tailPos.x, tailPos.y, origin.x, origin.y, blockSize.x,
                   blockSize.y, scale.x, scale.y, rotation);
    }

    // Should always be called before update is called. Only needs to
    // be called on the active snake
    public void setDirection(Direction snakeDirection) {
        this.snakeDirection = snakeDirection;
    }

    // Returns the direction the head is in relative to the first part
    // of the body.
    public Direction getHeadDirection() {
        IntVector head = snakePieces.get(0).copy();
        IntVector body = snakePieces.get(1).copy();
        
        return getDirectionBetween(head, body);
    }

    private Direction getDirectionBetween(IntVector piece1, IntVector piece2) {
        IntVector difference = piece1.copy().substract(piece2);

        if (difference.y == 1) return Direction.UP;
        if (difference.x == 1) return Direction.RIGHT;
        if (difference.y == -1) return Direction.DOWN;
        if (difference.x == -1) return Direction.LEFT;

        if (difference.y == -boardSize.y + 1) return Direction.UP;
        if (difference.x == -boardSize.x + 1) return Direction.RIGHT;
        if (difference.y == boardSize.y - 1) return Direction.DOWN;
        if (difference.x == boardSize.x - 1) return Direction.LEFT;

        throw new IllegalStateException();
    }

    private float directionToDegrees(Direction dir) {
        switch (dir) {
        case DOWN: return 0.0f;
        case RIGHT: return 90.0f;
        case UP: return 180.0f;
        case LEFT: return 270.0f;
        }

        throw new IllegalArgumentException();
    }

    // Picks up food with the FoodManager, which returns what food (if
    // any) has been picked up. If food has been picked up this method
    // notifies the OnCollisionListener and returns true. Otherwise it
    // returns false.
    private boolean pickUpFood(IntVector position) {
        FoodManager.SnakeFood food = foodManager.pickUpFoodAt(position);

        if (food == null) {
            return false;
        } else {
            collisionListener.onFoodCollision(food);
            return true;
        }
    }

    // Checks whether this snake would collide with another snake if
    // it where to move to ```position```.
    private boolean doesSnakeCollide(IntVector position) {
        for (Snake snake : snakes) {
            for (IntVector piece : snake.getSnakePieces()) {
                if (piece.equals(position)) return true;
            }
        }
        return false;
    }

    private IntVector directionToVector(Direction direction) {
        switch(direction) {
        case UP: return new IntVector(0, 1);
        case RIGHT: return new IntVector(1, 0);
        case DOWN: return new IntVector(0, -1);
        case LEFT: return new IntVector(-1, 0);
        }

        throw new IllegalArgumentException("Unbound Direction enum");
    }


    public void setSnakeList(ArrayList<Snake> snakes) {
        this.snakes = snakes;
    }

    public void setSnakePieces(LinkedList<IntVector> snakePieces) {
        this.snakePieces = snakePieces;
    }

    public LinkedList<IntVector> getSnakePieces() {
        return snakePieces;
    }

    // The order in which these are declared is important, because in
    // the draw function the ordinal() method is called on them and it
    // is presumed that the directions are ordered clockwise.
    public enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }

    public interface OnCollisionListener {
        // collidedOn: The snake that the active snake collided
        // against. Note: can be the active Snake itself.
        public void onSnakeCollision();
        public void onFoodCollision(FoodManager.SnakeFood food);
    }
}
