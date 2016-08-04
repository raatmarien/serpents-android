package com.jmstudios.serpents;

import com.jmstudios.serpents.Snake;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/* This class attempts to register slides by the user and translate
 * them to a movement for the active snake.
 */
public class MoveGestureListener
    implements GestureDetector.GestureListener {
    public static final float minVelocity = 1.0f;
    private Snake.Direction currentDirection;

    public MoveGestureListener() {
        currentDirection = Snake.Direction.RIGHT;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (Math.abs(velocityX) > Math.abs(velocityY) &&
            Math.abs(velocityX) > minVelocity) {
            Snake.Direction slideDirection = velocityX > 0 ?
                Snake.Direction.RIGHT : Snake.Direction.LEFT;

            if (!areOpposites(slideDirection, currentDirection))
                currentDirection = slideDirection;

            return true;
        } else if (Math.abs(velocityY) > minVelocity) {
            Snake.Direction slideDirection = velocityY > 0 ?
                Snake.Direction.DOWN : Snake.Direction.UP;

            if (!areOpposites(slideDirection, currentDirection))
                currentDirection = slideDirection;

            return true;
        }

        return false;
    }

    public Snake.Direction getCurrentDirection() {
        return currentDirection;
    }

    /* The current direction needs to be changed manually when a new
     * snake is made active.
     */
    public void setCurrentDirection(Snake.Direction direction) {
        currentDirection = direction;
    }

    private boolean areOpposites(Snake.Direction d1, Snake.Direction d2) {
        if (d1 == Snake.Direction.UP) return d2 == Snake.Direction.DOWN;
        if (d1 == Snake.Direction.RIGHT) return d2 == Snake.Direction.LEFT;
        if (d1 == Snake.Direction.DOWN) return d2 == Snake.Direction.UP;
        if (d1 == Snake.Direction.LEFT) return d2 == Snake.Direction.RIGHT;

        throw new IllegalArgumentException();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        return false;
    }

    @Override
    public boolean longPress(float x, float y) {

        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        return false;
    }

    @Override
    public boolean zoom (float originalDistance, float currentDistance){

        return false;
    }

    @Override
    public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer,
                          Vector2 firstPointer, Vector2 secondPointer){

        return false;
    }

    @Override
    public void pinchStop() {

    }
}
