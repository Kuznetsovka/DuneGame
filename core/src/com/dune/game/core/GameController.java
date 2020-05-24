package com.dune.game.core;

import com.badlogic.gdx.math.Vector2;

public class GameController {
    private BattleMap map;
    private ProjectilesController projectilesController;
    private Tank tank;
    private Vector2 tmp;

    public Tank getTank() {
        return tank;
    }

    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    public BattleMap getMap() {
        return map;
    }

    // Инициализация игровой логики
    public GameController() {
        Assets.getInstance().loadAssets();
        this.tmp = new Vector2 ();
        this.map = new BattleMap();
        this.projectilesController = new ProjectilesController(this);
        this.tank = new Tank(this, 200, 200);;
    }

    public void update(float dt) {
        tank.update(dt);
        projectilesController.update(dt);
        checkCollisions(dt);
    }

    public void checkCollisions(float dt) {
        float[][] data = getMap ().getDataPosition ();
        float distance;
        for (int i = 0; i < data.length; i++) {
            distance = tmp.set(getTank ().position).dst(data[i][0] , 720-data[i][1]);
            if (distance<60.0f){
                getMap ().update(dt,data[i][0] , data[i][1],true);
            }

        }


    }
}


