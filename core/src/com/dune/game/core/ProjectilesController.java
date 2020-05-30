package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ProjectilesController extends ObjectPool<Projectile> {
    private GameController gc;
    private TextureRegion projectileTexture;

    @Override
    protected Projectile newObject() {
        return new Projectile(gc);
    }

    public ProjectilesController(GameController gc) {
        this.gc = gc;
        this.projectileTexture = Assets.getInstance().getAtlas().findRegion("bullet");
    }

    public void render(SpriteBatch batch) {
        for (Projectile projectile : activeList) {
            projectile.render(batch);
        }
    }

    public void setup(Vector2 srcPosition, float angle, int power) {
        Projectile p = activateObject();
        p.setup(srcPosition, angle, projectileTexture, power);
    }

    public void update(float dt) {
        for (Projectile projectile : activeList) {
            projectile.update (dt);
        }
        checkPool();
    }
}
