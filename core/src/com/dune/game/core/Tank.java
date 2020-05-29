package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Tank extends GameObject implements Poolable {
    public enum Owner {
        PLAYER, AI
    }
    private BitmapFont font10;
    private Owner ownerType;
    private Weapon weapon;
    private Vector2 destination;
    private TextureRegion[] textures;
    private TextureRegion progressbarTexture;
    private int hp;
    private float angle;
    private float speed;
    private float rotationSpeed;
    protected boolean isSelected;
    private float moveTimer;
    private float timePerFrame;
    private int container;
    private int limitContainer;

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public Tank(GameController gc) {
        super(gc);
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.font10 = Assets.getInstance().getAssetManager().get("fonts/font10.ttf");
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 90.0f;
        this.limitContainer = 5;
        this.isSelected = false;
    }

    public void setup(Owner ownerType, float x, float y) {
        this.textures = Assets.getInstance().getAtlas().findRegion("tankanim").split(64,64)[0];
        this.position.set(x, y);
        this.ownerType = ownerType;
        this.speed = 120.0f;
        this.hp = 100;
        this.weapon = new Weapon(Weapon.Type.HARVEST, 1.0f, 1);
        this.destination = new Vector2(position);
    }

    private int getCurrentFrameIndex() {
        return (int) (moveTimer / timePerFrame) % textures.length;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void update(float dt) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && isSelected) {
                destination.set (Gdx.input.getX (), 720 - Gdx.input.getY ());
        }

        if (position.dst(destination) > 30.0f ) {
            float angleTo = tmp.set(destination).sub(position).angle();
            if (Math.abs(angle - angleTo) > 3.0f) {
                if (angle > angleTo) {
                    if (Math.abs(angle - angleTo) <= 180.0f) {
                        angle -= rotationSpeed * dt;
                    } else {
                        angle += rotationSpeed * dt;
                    }
                } else {
                    if (Math.abs(angle - angleTo) <= 180.0f) {
                        angle += rotationSpeed * dt;
                    } else {
                        angle -= rotationSpeed * dt;
                    }
                }
            }
            if (angle < 0.0f) {
                angle += 360.0f;
            }
            if (angle > 360.0f) {
                angle -= 360.0f;
            }

            moveTimer += dt;
            tmp.set(speed, 0).rotate(angle);
            position.mulAdd(tmp, dt);
            if (position.dst(destination) < 120.0f && Math.abs(angleTo - angle) > 10) {
                position.mulAdd(tmp, -dt);
            }
        }
        updateWeapon(dt);
        checkBounds();
    }

    public void updateWeapon(float dt) {
        if (weapon.getType() == Weapon.Type.HARVEST) {
            if (gc.getMap().getResourceCount(this) > 0 && container < limitContainer) {
                int result = weapon.use(dt);
                if (result > -1) {
                    container += gc.getMap().harvestResource(this, result);
                }
            } else {
                weapon.reset();
            }
        }
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
        if (weapon.getType() == Weapon.Type.HARVEST && weapon.getUsageTimePercentage() > 0.0f) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 32, position.y + 30, 64, 20);
            batch.setColor(1.0f, 1.0f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 30, position.y + 32, 60 * weapon.getUsageTimePercentage(), 16);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            font10.draw(batch, Integer.toString (container), position.x - 32, position.y + 44, 64, 1, false);
        }
        if (container==limitContainer) {
            font10.draw (batch, "FULL", position.x - 32, position.y + 44, 64, 1, false);
            //batch.setColor(0.8f, 0.2f, 0.67f, 0.8f);
            batch.setColor(1.0f, 1.0f, 0.0f, 1.0f);
            batch.draw(textures[getCurrentFrameIndex()], position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            batch.draw(textures[getCurrentFrameIndex()], position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
        }
        //для понимания, что танк выбран
        if (isSelected){
            batch.setColor(1.0f, 0.0f, 0.0f, 0.5f);
            batch.draw(textures[getCurrentFrameIndex()], position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

    }
}
