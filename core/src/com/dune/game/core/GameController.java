package com.dune.game.core;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

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
        ArrayList<Vector2> data = getMap ().getDataPositionObjects ();
        float distance;
        for (int i = 0; i < data.size (); i++) {
            distance = tmp.set (getTank ().position).dst (data.get(i));
            if (distance < getMap ().getWidthCircleTexture()) {
                getMap ().update (dt, i, true);
            }
        }
    }
}


