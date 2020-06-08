package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dune.game.core.*;

public abstract class AbstractBuild extends GameObject implements Poolable, Targetable {
    protected BuildType buildType;
    protected int level;
    protected Owner ownerType;
    protected TextureRegion texture;
    protected int hp;
    protected int hpMax;
    protected TextureRegion progressbarTexture;
    protected float timePerFrame;
    protected float lifeTime;
    protected int container;

    public AbstractBuild(GameController gc) {
        super(gc);
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.timePerFrame = 0.08f;

    }

    public abstract void setup(Owner ownerType, float x, float y);

    public Owner getOwnerType() {
        return ownerType;
    }

    public boolean takeDamage(int damage) {
        if (!isActive()) {
            return false;
        }
        hp -= damage;
        return (hp <= 0);
    }

    public void render(SpriteBatch batch) {
        float c = 1.0f;
        float r = 0.0f;
        if (gc.isUnitSelected(this)) {
            c = 0.7f + (float) Math.sin(lifeTime * 8.0f) * 0.3f;
        }
        if (ownerType == Owner.AI) {
            r = 0.4f;
        }
        batch.setColor(c, c - r, c - r, 1.0f);
        batch.draw(texture, position.x - 50, position.y - 50, 50, 50, 100, 100, 1, 1, 0);
        batch.setColor(1, 1, 1, 1);
        renderGui(batch);
    }

    public void renderGui(SpriteBatch batch) {
        if (hp < hpMax) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 32, position.y + 30, 64, 12);
            batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            float percentage = (float) hp / hpMax;
            batch.draw(progressbarTexture, position.x - 30, position.y + 32, 60 * percentage, 8);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    @Override
    public TargetType getType() {
        return TargetType.BUILD;
    }

    public abstract void update(float dt);

    public abstract void fillContainer(int container);
}
