package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Tank {
    private Vector2 position;
    private Vector2 tmp;
    private TextureRegion[] textures;
    private float angle;
    private float speed;
    private Projectile projectile;
    private float moveTimer;
    private float timePerFrame;
    private boolean isMouseMothin;

    public Projectile getProjectile() {
        return projectile;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Tank(TextureAtlas atlas, float x, float y) {
        super ();
        this.position = new Vector2 (x, y);
        this.tmp = new Vector2 ();
        this.textures = new TextureRegion (atlas.findRegion ("tankanim")).split (64, 64)[0];
        this.projectile = new Projectile (atlas);
        this.speed = 140.0f;
        this.timePerFrame = 0.08f;
    }

    private int getCurrentFrameIndex() {
        return (int) (moveTimer / timePerFrame) % textures.length;
    }

    public void update(float dt) {

        Gdx.input.setInputProcessor (new InputAdapter () {
            public boolean touchDown(int x, int y, int pointer, int button) {
                isMouseMothin = true;
                tmp.set (x, 720-y);
                tmp.sub (position);
                tmp.nor ();
                return true;
            }
        });

        if (isMouseMothin) {
            position.add (speed * tmp.x * dt, speed * tmp.y * dt);
        }


        if (Gdx.input.isKeyPressed (Input.Keys.LEFT)) {
            angle += 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed (Input.Keys.RIGHT)) {
            angle -= 180.0f * dt;
        }

        if (Gdx.input.isKeyPressed (Input.Keys.UP)) {
            position.add (speed * MathUtils.cosDeg (angle) * dt, speed * MathUtils.sinDeg (angle) * dt);
            moveTimer += dt;
        } else {
            if (getCurrentFrameIndex () != 0) {
                moveTimer += dt;
            }
        }

        if (Gdx.input.isKeyJustPressed (Input.Keys.ENTER)) {
            if (!projectile.isFire ()) {
                fire ();
            }
        }
        checkBounds ();
    }

    private void fire() {
        tmp.set (position).add(32*MathUtils.cosDeg (angle),32*MathUtils.sinDeg (angle));
        projectile.fire (tmp, angle);
    }

    public void checkBounds() {
        if (position.x < 40) {
            position.x = 40;
        }
        if (position.y < 40) {
            position.y = 40;
        }
        if (position.x > 1240) {
            position.x = 1240;
        }
        if (position.y > 680) {
            position.y = 680;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw (textures[getCurrentFrameIndex ()], position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
    }
}
