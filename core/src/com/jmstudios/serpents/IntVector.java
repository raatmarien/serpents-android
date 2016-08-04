package com.jmstudios.serpents;

public class IntVector {
    public int x;
    public int y;

    public IntVector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public IntVector copy() {
        return new IntVector(x, y);
    }

    public IntVector add(IntVector vec2) {
        this.x += vec2.x;
        this.y += vec2.y;
        return this;
    }
    
    public IntVector substract(IntVector vec2) {
        this.x -= vec2.x;
        this.y -= vec2.y;
        return this;
    }

    // Adds vec2, but wraps within the bounds [(0,0), (sizeOfBounds.x,
    // sizeOfbounds.y)].
    public IntVector addWithBounds(IntVector vec2, IntVector sizeOfBounds) {
        this.add(vec2);
        if (this.x >= sizeOfBounds.x) this.x = this.x % sizeOfBounds.x;
        else if (this.x < 0) this.x += sizeOfBounds.x;

        if (this.y >= sizeOfBounds.y) this.y = this.y % sizeOfBounds.y;
        else if (this.y < 0) this.y += sizeOfBounds.y;

        return this;
    }

    @Override
    public boolean equals(Object v) {
        IntVector vec2 = (IntVector) v;
        return this.x == vec2.x && this.y == vec2.y;
    }
}
