package com.dune.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Circle {
    private Vector2 position;
    private Texture texture;

    private int radius;
    private int diameter;

    public int getRadius() {
        return radius;
    }

    public Circle() {
        this.position = new Vector2(getRandomX(),getRandomY());
        this.texture = new Texture("circle.png");
        this.diameter = texture.getWidth();
        this.radius = diameter / 2;
    }

    private float getRandomX() {
        return (float) Math.random() * (1280 - diameter) + radius;
    }
    private float getRandomY() {
        return (float) Math.random() * (720 - diameter) + radius;
    }

    public void update() {
        position.x = getRandomX();
        position.y = getRandomY();
    }

    public Vector2 getPosition() {
        return position;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - radius, position.y - radius, radius, radius, diameter, diameter, 1, 1, 0, 0, 0, diameter, diameter, false, false);
    }

    public void dispose() {
        texture.dispose();
    }
}
