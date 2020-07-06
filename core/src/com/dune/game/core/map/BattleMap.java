package com.dune.game.core.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.Building;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.units.types.TargetType;
import com.dune.game.screens.utils.Assets;

import java.util.ArrayList;
import java.util.List;

public class BattleMap implements GameMap {

    public static final int COLUMNS_COUNT = 24;
    public static final int ROWS_COUNT = 16;
    public static final int CELL_SIZE = 60;
    public static final int MAP_WIDTH_PX = COLUMNS_COUNT * CELL_SIZE;
    public static final int MAP_HEIGHT_PX = ROWS_COUNT * CELL_SIZE;
    private TextureRegion grassTexture;
    private TextureRegion resourceTexture;
    private Cell[][] cells;

    public BattleMap() {
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.resourceTexture = Assets.getInstance().getAtlas().findRegion("resr");
        this.cells = new Cell[COLUMNS_COUNT][ROWS_COUNT];
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    //    public void blockGroundCell(int cellX, int cellY) {
//        cells[cellX][cellY].blockGroundPass();
//    }
//
//    public void unblockGroundCell(int cellX, int cellY) {
//        cells[cellX][cellY].unblockGroundPass();
//    }
//
//    public void blockAirCell(int cellX, int cellY) {
//        cells[cellX][cellY].blockAirPass();
//    }
//
//    public void unblockAirCell(int cellX, int cellY) {
//        cells[cellX][cellY].unblockAirPass();
//    }
//
//
//    public boolean isCellGroundPassable(Vector2 position) {
//        int cellX = (int) (position.x / BattleMap.CELL_SIZE);
//        int cellY = (int) (position.y / BattleMap.CELL_SIZE);
//        if (cellX < 0 || cellY < 0 || cellX >= COLUMNS_COUNT || cellY >= ROWS_COUNT) {
//            return false;
//        }
//        return cells[cellX][cellY].groundPassable;
//    }

    public void blockCell(int cellX, int cellY, Cell.BlockType blockType) {
        cells[cellX][cellY].block(blockType);
    }

    public void unblockCell(int cellX, int cellY, Cell.BlockType blockType) {
        cells[cellX][cellY].unblock(blockType);
    }

    public void setupBuilding(int startX, int startY, int endX, int endY, int entranceX, int entranceY, Building building) {
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                cells[i][j].buildingCore = building;
                if (building != null) {
                    blockCell(i, j, Cell.BlockType.BUILDING);
                } else {
                    unblockCell(i, j, Cell.BlockType.BUILDING);
                }
            }
        }
        cells[entranceX][entranceY].buildingEntrance = building;
    }

    public void setupBuildingEntrance(int cellX, int cellY, Building building) {
        cells[cellX][cellY].buildingEntrance = building;
    }

    public Building getBuildingEntrance(int cellX, int cellY) {
        return cells[cellX][cellY].buildingEntrance;
    }

    public Building getBuildingFromCell(int cellX, int cellY) {
        if (cellX < 0 || cellY < 0 || cellX >= COLUMNS_COUNT || cellY >= ROWS_COUNT) {
            return null;
        }
        return cells[cellX][cellY].buildingCore;
    }

    public int getResourceCount(Vector2 point) {
        int cx = (int) (point.x / CELL_SIZE);
        int cy = (int) (point.y / CELL_SIZE);
        return cells[cx][cy].resource;
    }

    public List<Cell> cellResource(){
        List l = new ArrayList ();
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                if (cells[i][j].resource != 0)
                    l.add (cells[i][j]);
            }
        }
        return l;
    }

    public int harvestResource(Vector2 point, int power) {
        int value = 0;
        int cx = (int) (point.x / CELL_SIZE);
        int cy = (int) (point.y / CELL_SIZE);
        if (cells[cx][cy].resource >= power) {
            value = power;
            cells[cx][cy].resource -= power;
        } else {
            value = cells[cx][cy].resource;
            cells[cx][cy].resource = 0;
        }
        return value;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
//                if (cells[i][j].groundPassable) {
                batch.draw(grassTexture, i * CELL_SIZE, j * CELL_SIZE);
//                }
                cells[i][j].render(batch, resourceTexture);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j].update(dt);
            }
        }
    }

    @Override
    public int getSizeX() {
        return COLUMNS_COUNT;
    }

    @Override
    public int getSizeY() {
        return ROWS_COUNT;
    }


    @Override
    public boolean isCellPassable(Vector2 position, boolean isFlyable) {
        int cellX = (int)(position.x / BattleMap.CELL_SIZE);
        int cellY = (int)(position.y / BattleMap.CELL_SIZE);
        return isCellPassable(cellX, cellY, isFlyable);
    }

    @Override
    public int getCellCost(int cellX, int cellY) {
        return 1;
    }

    @Override
    public boolean isCellPassable(int cellX, int cellY, boolean isFlyable) {
        if (cellX < 0 || cellY < 0 || cellX >= COLUMNS_COUNT || cellY >= ROWS_COUNT) {
            return false;
        }
        if (cells[cellX][cellY].groundPassable && !cells[cellX][cellY].blockByTank) {
            return true;
        }
        if (cells[cellX][cellY].airPassable && isFlyable) {
            return true;
        }
        return false;
    }

}
