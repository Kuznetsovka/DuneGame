package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class BattleMap{
    private TextureRegion grassTexture;
    private TextureRegion circleTexture;
    private ArrayList<Vector2> dataPositionObjects;
    private final int columns = 16; //Пока будут константами
    private final int rows = 9; //Пока будут константами
    private float widthCircleTexture;

    public float getWidthCircleTexture() {
        return widthCircleTexture;
    }

    public BattleMap() {
        this.grassTexture = Assets.getInstance ().getAtlas ().findRegion ("grass");
        this.circleTexture = Assets.getInstance ().getAtlas ().findRegion ("circle");
        this.widthCircleTexture = circleTexture.getRegionWidth ();
        this.dataPositionObjects = new ArrayList<> ();
        initData();
    }

    public ArrayList<Vector2> getDataPositionObjects() {
        return dataPositionObjects;
    }

    private void initData() {
        //От 1 чтобы не делать проверку на попадание в экран.
        for (int i = 1; i < columns; i++) {
            for (int j = 1; j < rows; j++) {
                int temp = MathUtils.random (1,5);
                if (temp==1){
                    dataPositionObjects.add(new Vector2(i*80,j*80));
                    /*Решение создания векторов не нравиться, но по другому не получилось.
                    Интересно будет посмотреть как правильнее.
                     */
                }
            }
        }
    }

    public void update(float dt, int index, boolean isCollision){
        //dt пока не нужно, но в будущем будет нужно, поэтому оставляю.
        if (isCollision) {
            dataPositionObjects.remove (index);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                batch.draw (grassTexture, i * 80, j * 80);
                for (Vector2 data : dataPositionObjects) {
                    batch.draw(circleTexture, data.x - 40, data.y - 40, widthCircleTexture/2, widthCircleTexture/2, widthCircleTexture, widthCircleTexture, 1, 1, 0);
                }
            }
        }
    }
}
