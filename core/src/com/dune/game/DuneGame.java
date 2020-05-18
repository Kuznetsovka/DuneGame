package com.dune.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DuneGame extends ApplicationAdapter {

    protected SpriteBatch batch;
    private Tank tank;
    private Circle circle;

    @Override
    public void create() {
        batch = new SpriteBatch();
        tank = new Tank(200, 200);
        circle = new Circle();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        update(dt);
        Gdx.gl.glClearColor(0, 0.4f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        tank.render(batch);
        circle.render(batch);
        batch.end();
    }

    public void update(float dt) {
        tank.update(dt);
        if (isTouch())
            circle.update();
    }

    private boolean isTouch() {
        return distance()<=tank.getHalfWidth() - tank.getOffset()+circle.getRadius();
    }

    private float distance() {
        return (float) Math.sqrt( Math.pow(tank.getPosition().x - circle.getPosition().x,2) + Math.pow(tank.getPosition().y - circle.getPosition().y,2));
    }

    @Override
    public void dispose() {
        batch.dispose();
        tank.dispose();
        circle.dispose();
    }

}