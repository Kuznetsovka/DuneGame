package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private boolean isFire;
    private Vector2 position;
    private Vector2 velocity;
    private TextureRegion texture;
    private float speed;
    private int rotate;

    public void setFire(boolean fire) {
        isFire = fire;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public Projectile(TextureAtlas atlas) {
        this.texture = new TextureRegion (atlas.findRegion ("bullet"));
        this.speed = 200.0f;
        this.position = new Vector2 ();
        this.velocity = new Vector2 (0, 0);
    }

    public void fire(Vector2 startPosition, float angle) {
        this.position.set(startPosition);
        velocity.add (speed * MathUtils.cosDeg (angle), speed * MathUtils.sinDeg (angle));
        isFire = true;
    }

    public boolean isFire() {
        return isFire;
    }

    public void update(float dt) {
        // position.x += velocity.x * dt;
        // position.y += velocity.y * dt;
        if (isFire) {
            position.mulAdd (velocity, dt);
            rotate = (rotate + (int) (dt * speed)) % 360;
            checkBounds ();
        }
    }

    public void render(SpriteBatch batch) {
        if (isFire)
            batch.draw (texture, position.x, position.y, texture.getRegionWidth () / 2, texture.getRegionHeight () / 2, texture.getRegionWidth (), texture.getRegionHeight (), 1, 1, rotate);
    }

    public void checkBounds() {
        if (position.x < 0 || position.x > 1280) {
            isFire = false;
            velocity.set (0, 0);
        }
        if (position.y < 0 || position.y > 720) {
            isFire = false;
            velocity.set (0, 0);
        }
    }
}
