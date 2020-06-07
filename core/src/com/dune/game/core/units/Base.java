package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.core.Assets;
import com.dune.game.core.GameController;

public class Base extends AbstractBuild {

    public Base(GameController gc) {
        super (gc);
        this.level = 1;
        this.texture = Assets.getInstance().getAtlas().findRegion("base");
        this.ownerType = Owner.PLAYER;
        this.buildType = BuildType.BASE;
        this.container = 1000;
    }

    @Override
    public void setup(Owner ownerType, float x, float y) {
        this.position.set(x, y);
        this.ownerType = ownerType;
        this.hp = this.hpMax;
    }

    @Override
    public void renderGui(SpriteBatch batch) {
        super.renderGui(batch);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void fillContainer(int count) {
        this.container+=count;
        gc.getPlayerLogic().addMoney (count);
    }

}
