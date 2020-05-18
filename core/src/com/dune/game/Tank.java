package com.dune.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Tank {
    private Vector2 position;
    private Texture texture;
    private float angle;
    private float speed;
    private int halfWidth;
    private int halfHeight;

    public float getOffset() {
        return offset;
    }

    private int width;
    private int height;
    private float offset = 10;

    public Vector2 getPosition() {
        return position;
    }

    public Tank(float x, float y) {
        this.position = new Vector2(x, y);
        this.texture = new Texture("tank.png");
        this.speed = 200.0f;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        this.halfHeight = height / 2;
        this.halfWidth = width / 2;
    }

    public void update(float dt) {

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            angle += 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            angle -= 180.0f * dt;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            position.x = nextX(dt);
            position.y = nextY(dt);
        }
    }

    private float nextY(float dt) {
        float nextY = position.y + speed * MathUtils.sinDeg(angle) * dt;
        if (nextY + halfHeight - offset < 720 && nextY - halfHeight + offset > 0)
            return nextY;
        else
            return position.y;
    }

    public int getHalfWidth() {
        return halfWidth;
    }

    private float nextX(float dt) {
        float nextX = position.x + speed * MathUtils.cosDeg(angle) * dt;
        if (nextX + halfWidth - offset< 1280 && nextX - halfWidth + offset > 0)
            return nextX;
        else
            return position.x;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - halfWidth, position.y - halfHeight, halfWidth, halfHeight, width, height, 1, 1, angle, 0, 0, width, height, false, false);
    }

    public void dispose() {
        texture.dispose();
    }
}
