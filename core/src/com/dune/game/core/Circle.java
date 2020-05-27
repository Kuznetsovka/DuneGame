package com.dune.game.core;

import com.badlogic.gdx.math.Vector2;

public class Circle implements Poolable {
    private Vector2 position;

    public Vector2 getPosition() {
        return position;
    }

    public Circle(Vector2 position) {
        this.position = position;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
