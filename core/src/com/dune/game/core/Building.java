package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.interfaces.Poolable;
import com.dune.game.core.interfaces.Selectable;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.map.BattleMap;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.TargetType;
import com.dune.game.core.users_logic.BaseLogic;
import com.dune.game.screens.utils.Assets;

public class Building extends GameObject implements Poolable, Targetable, Selectable {
    public enum Type {
        STOCK
    }

    // * * *
    // * P *
    //   E
    private BaseLogic ownerLogic;
    private Type typeBuilding;
    private TextureRegion texture;
    private TextureRegion textureEntrance;
    private TextureRegion progressbarTexture;
    private Vector2 textureWorldPosition;
    private boolean selected;
    private int hpMax;
    private int hp;
    private int cellX, cellY;

    public void selecting(boolean is){
        selected = is;
    }
    @Override
    public boolean isSelect() {
        return selected;
    }

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public Type getBuildingType() {
        return typeBuilding;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BUILDING;
    }

    public BaseLogic getOwnerLogic() {
        return ownerLogic;
    }

    public Building(GameController gc) {
        super (gc);
        this.texture = Assets.getInstance ().getAtlas ().findRegion ("base");
        this.textureEntrance = Assets.getInstance ().getAtlas ().findRegion ("entrance");
        this.progressbarTexture = Assets.getInstance ().getAtlas ().findRegion ("progressbar");
        this.textureWorldPosition = new Vector2 ();
    }

    public void setup(BaseLogic ownerLogic, int cellX, int cellY) {
        this.ownerLogic = ownerLogic;
        this.position.set (cellX * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, cellY * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2);
        this.cellX = cellX;
        this.cellY = cellY;
        this.hpMax = 1000;
        this.hp = this.hpMax;
        this.textureWorldPosition.set ((cellX - 1) * BattleMap.CELL_SIZE, cellY * BattleMap.CELL_SIZE);
        this.typeBuilding = Type.STOCK;
        gc.getMap().setupBuilding(cellX - 1, cellY, cellX + 1, cellY + 1, cellX, cellY - 1, this);
        gc.getMap ().setupBuildingEntrance (cellX, cellY - 1, this);
    }

    public Vector2 posEntrance() {
        return new Vector2 (cellX * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, (cellY - 1) * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2);
    }

    public void render(SpriteBatch batch) {
        if (ownerLogic.getOwnerType () == Owner.AI){
            batch.setColor (1.0f, 0.5f, 0.5f, 1.0f);
        }
        batch.draw (texture, textureWorldPosition.x, textureWorldPosition.y, BattleMap.CELL_SIZE * 3, BattleMap.CELL_SIZE * 2);
        batch.setColor (1.0f, 1.0f, 1.0f, 1.0f);
        batch.draw (textureEntrance, textureWorldPosition.x + BattleMap.CELL_SIZE, textureWorldPosition.y - BattleMap.CELL_SIZE, BattleMap.CELL_SIZE, BattleMap.CELL_SIZE);
        if (hp < hpMax) {

            batch.setColor (0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw (progressbarTexture, position.x - 32, position.y + 30, 64, 12);
            batch.setColor (0.0f, 1.0f, 0.0f, 1.0f);
            float percentage = (float) hp / hpMax;
            batch.draw (progressbarTexture, position.x - 30, position.y + 32, 60 * percentage, 8);
            batch.setColor (1.0f, 1.0f, 1.0f, 1.0f);
            for (int i = 0; i < 5; i++) {
                gc.getParticleController ().setup(position.x, MathUtils.random(position.y,position.y+10), MathUtils.random(-15.0f, 15.0f), MathUtils.random(-30.0f, 30.0f), 0.5f,
                        0.3f, 1.4f, 1, 1, 0, 1, 1, 0, 0, 0.5f);
        }
        }
    }

    public void update(float dt) {
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) {
            destroy ();
        }
    }

    public void destroy() {
        gc.getMap().setupBuilding(cellX - 1, cellY, cellX + 1, cellY + 1, cellX, cellY - 1, null);
    }
//    public void destroy() {
//        for (int i = cellX - 1; i <= cellX + 1; i++) {
//            for (int j = cellY; j <= cellY + 1; j++) {
//                gc.getMap ().unblockGroundCell (i, j);
//            }
//        }
//        gc.getMap ().setupBuildingEntrance (cellX, cellY - 1, null);
//    }
}