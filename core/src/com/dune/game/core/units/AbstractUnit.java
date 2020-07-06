package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.*;
import com.dune.game.core.interfaces.Poolable;
import com.dune.game.core.interfaces.Selectable;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.map.BattleMap;
import com.dune.game.core.map.Cell;
import com.dune.game.core.units.types.*;
import com.dune.game.core.users_logic.BaseLogic;
import com.dune.game.screens.utils.Assets;
import lombok.Getter;

@Getter
public abstract class AbstractUnit extends GameObject implements Poolable, Targetable, Selectable {
    protected BaseLogic baseLogic;
    protected UnitType unitType;
    protected Owner ownerType;
    protected Weapon weapon;

    private final TextureRegion selectorTexture = Assets.getInstance().getAtlas().findRegion("selector");

    protected Vector2 destination;
    protected int realCellXDestination, realCellYDestination;
    protected TextureRegion[] textures;
    protected TextureRegion weaponTexture;

    protected TextureRegion progressbarTexture;
    protected int hp;
    protected int hpMax;
    protected float angle;
    protected float speed;
    protected float rotationSpeed;

    protected float moveTimer;
    protected float lifeTime;
    protected float timePerFrame;
    protected int container;
    protected int containerCapacity;

    protected Targetable target;
    protected float minDstToActiveTarget;

    public static int CORE_SIZE = 64;
    public static int CORE_SIZE_D2 = CORE_SIZE / 2;
    private boolean isMove = false;

    @Override
    public TargetType getTargetType() {
        return TargetType.UNIT;
    }


    public boolean takeDamage(int damage) {
        if (!isActive()) {
            return false;
        }
        hp -= damage;
        if (hp <= 0) {
            return true;
        }
        return false;
    }

    public void moveBy(Vector2 value) {
        boolean stayStill = false;
        if (position.dst(destination) < 3.0f) {
            stayStill = true;
        }
        tmp.set(position).add(value);
        position.add(value);
        if (stayStill) {
            destination.set(position);
        }
    }

    @Override
    public boolean isSelect() {
        return gc.isUnitSelected(this);
    }
    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public AbstractUnit(GameController gc) {
        super(gc);
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 180.0f;
        this.destination = new Vector2();
    }

    public abstract void setup(BaseLogic baseLogic, float x, float y);

    private int getCurrentFrameIndex() {
        return (int) (moveTimer / timePerFrame) % textures.length;
    }

    public void update(float dt) {
        gc.getMap().unblockCell(getCellX(), getCellY(), Cell.BlockType.UNIT);
        lifeTime += dt;

//        if (target != null) {
//            commandMoveTo(target.getPosition(), false);
//            if (position.dst(target.getPosition()) < minDstToActiveTarget) {
//                commandMoveTo(position, false);
//            }
//        }
        if (target == null) {
            weapon.setAngle(rotateTo(weapon.getAngle(), angle, 180.0f, dt));
        }

        if (gc.getMap().getResourceCount(position) > 0 && getUnitType ().equals (UnitType.HARVESTER) && container!=containerCapacity) {
            updateHarvesting();
        }
        updateWeapon(dt);
        if (!gc.getMap().isCellPassable(destination, false)) {
            gc.getPathFinder().buildRoute(getCellX(), getCellY(), realCellXDestination, realCellYDestination, destination);
        }

        if (position.dst(destination) > speed * dt) {
            float angleTo = tmp.set(destination).sub(position).angle();
            angle = rotateTo(angle, angleTo, rotationSpeed, dt);
            moveTimer += dt;
            tmp.set(speed, 0).rotate(angle);
            weapon.setAngle (angle);
            position.mulAdd(tmp, dt);
            if ((position.dst(destination) < 120.0f && Math.abs(angleTo - angle) > 10)) {
                position.mulAdd(tmp, -dt);
                isMove = true;
            }

            if (Math.abs(angleTo - angle) < rotationSpeed * dt) {
                angle = angleTo;
            }
            if (Math.abs(angleTo - angle) < 1) {
                position.mulAdd(tmp, dt);
            }

//            if (!gc.getMap().isCellGroundPassable(position)) {
//                tmp.set(position).sub(getCellX() * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, getCellY() * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2).nor().scl(2);
//                position.add(tmp);
//            }
        } else {
            isMove = false;
            position.set(destination);
            if (!(getCellX() == realCellXDestination && getCellY() == realCellYDestination)) {
                gc.getPathFinder().buildRoute(getCellX(), getCellY(), realCellXDestination, realCellYDestination, destination);
                isMove = true;
            }
            if (target != null) {
                if (position.dst(target.getPosition()) < minDstToActiveTarget) {
                    isMove = false;
                    resetMoveTo();
                } else {
                    isMove = true;
                    commandMoveTo(target.getPosition(), false);
                }
            }
        }

        checkBounds();
        gc.getMap().blockCell(getCellX(), getCellY(), Cell.BlockType.UNIT);
    }

    private void updateHarvesting() {
        float particlePositionX;
        float particlePositionY;
        Vector2 point = new Vector2();
        for (int i = 0; i < 15; i++) {
            particlePositionX = position.x + (25 * MathUtils.cosDeg(angle)) + MathUtils.random(-5, 15);
            particlePositionY = position.y + (25 * MathUtils.sinDeg(angle)) + MathUtils.random(-5, 15);
            gc.getParticleController().setup(
                    particlePositionX,
                    particlePositionY,
                    position.x - particlePositionX, position.y - particlePositionY,
                    1f, 0.5f, 0.2f,
                    0f, 0.4f, 0.7f, 0.7f,
                    0f, 0.6f, 0.5f, 0.5f);
        }
        for (int i = 0; i < 40; i++) {
            while (!(Math.abs(point.dst(position)) < 50 && Math.abs(point.dst(position)) > 35)) {
                point.x = position.x + MathUtils.random(-50, 50);
                point.y = position.y + MathUtils.random(-50, 50);
            }
            gc.getParticleController().setup(point.x, point.y,
                    MathUtils.random(-15, 15), MathUtils.random(15, 15), 0.1f, 0.5f, 0.3f,
                    0f, 0.4f, 0.7f, 0.7f,
                    0f, 0.6f, 0.5f, 0.5f);
            point.set(-100, - 100);
        }
    }

    public void resetMoveTo() {
        realCellXDestination = getCellX();
        realCellYDestination = getCellY();
        destination.set(realCellXDestination * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, realCellYDestination * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2);
    }

    public void commandMoveTo(Vector2 point, boolean resetTarget) {
        int cellPointX = (int) (point.x / BattleMap.CELL_SIZE);
        int cellPointY = (int) (point.y / BattleMap.CELL_SIZE);
        if (!gc.getMap().isCellPassable(cellPointX, cellPointY, false)) {
            exit:
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (gc.getMap().isCellPassable(cellPointX + i, cellPointY + j, false)) {
                        cellPointX += i;
                        cellPointY += j;
                        break exit;
                    }
                    if (i == 1 && j == 1) {
                        cellPointX = getCellX();
                        cellPointY = getCellY();
                    }
                }
            }
        }
        realCellXDestination = cellPointX;
        realCellYDestination = cellPointY;
        gc.getPathFinder().buildRoute(getCellX(), getCellY(), realCellXDestination, realCellYDestination, destination);
        if (resetTarget) {
            target = null;
        }
    }

    public abstract void commandAttack(Targetable target);

    public abstract void updateWeapon(float dt);

    public void checkBounds() {
        if (position.x < CORE_SIZE_D2) {
            position.x = CORE_SIZE_D2;
        }
        if (position.y < CORE_SIZE_D2) {
            position.y = CORE_SIZE_D2;
        }
        if (position.x > BattleMap.MAP_WIDTH_PX - CORE_SIZE_D2) {
            position.x = BattleMap.MAP_WIDTH_PX - CORE_SIZE_D2;
        }
        if (position.y > BattleMap.MAP_HEIGHT_PX - CORE_SIZE_D2) {
            position.y = BattleMap.MAP_HEIGHT_PX - CORE_SIZE_D2;
        }
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
        batch.draw(textures[getCurrentFrameIndex()], position.x - CORE_SIZE_D2, position.y - CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE, CORE_SIZE, 1, 1, angle);

        batch.draw(weaponTexture, position.x - CORE_SIZE_D2, position.y - CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE, CORE_SIZE, 1, 1, weapon.getAngle());

        if (isMove) {
            for (int i = 0; i < 20; i++) {
                float x = position.x + (realCellXDestination * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2 - position.x) / 20.0f * i;
                float y = position.y + (realCellYDestination * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2 - position.y) / 20.0f * i;
                batch.draw (selectorTexture, x - CORE_SIZE_D2, y - CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE, CORE_SIZE, 0.2f, 0.2f, 0.0f);
            }
        }

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

    public float rotateTo(float srcAngle, float angleTo, float rSpeed, float dt) {
        if (Math.abs(srcAngle - angleTo) > 3.0f) {
            if ((srcAngle > angleTo && Math.abs(srcAngle - angleTo) <= 180.0f) || (srcAngle < angleTo && Math.abs(srcAngle - angleTo) > 180.0f)) {
                srcAngle -= rSpeed * dt;
            } else {
                srcAngle += rSpeed * dt;
            }
        }
        if (srcAngle < 0.0f) {
            srcAngle += 360.0f;
        }
        if (srcAngle > 360.0f) {
            srcAngle -= 360.0f;
        }
        return srcAngle;
    }

    public void newDestination(Vector2 tmp) {
        this.destination.set(tmp);
    }
}