package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class BattleMap{
    private TextureRegion grassTexture;
    private TextureRegion circleTexture;
    private int[][] data;
    private float[][] dataPosition;
    private final int columns = 16; //Пока будут константами
    private final int rows = 9;
    private boolean isCollision;

    public BattleMap() {
        this.grassTexture = Assets.getInstance ().getAtlas ().findRegion ("grass");
        this.circleTexture = Assets.getInstance ().getAtlas ().findRegion ("circle");
        initData();
    }

    public float[][] getDataPosition() {
        return dataPosition;
    }

    private void initData() {
        int count=0;
        data = new int[columns][rows];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                data[i][j] = MathUtils.random (1,5);
                if (data[i][j] == 1){
                    count++;
                }
            }
        }
        dataPosition = new float[count+1][2];
    }

    public void update(float dt, float x,float y, boolean isCollision){
        if (isCollision)
            data[(int) (x/80)][(int) (y/80)] = 1;


    }

    public void render(SpriteBatch batch) {
        int count=0;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                batch.draw (grassTexture, i * 80, j * 80);
                if (data[i][j]==1) {
                    batch.draw (circleTexture, i * 80, j * 80);
                    dataPosition[count][0] = i * 80;
                    dataPosition[count][1] = j * 80;
                    count++;
                }
            }
        }
    }
}
