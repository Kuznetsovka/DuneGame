package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class GameController {
    private BattleMap map;
    private ProjectilesController projectilesController;
    private TanksController tanksController;

    public TanksController getTanksController() {
        return tanksController;
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
        this.map = new BattleMap();
        this.projectilesController = new ProjectilesController(this);
        this.tanksController = new TanksController(this);
        this.tanksController.setup(200, 200, Tank.Owner.PLAYER);
        this.tanksController.setup(400, 400, Tank.Owner.PLAYER);
    }

    public void update(float dt) {
        //Так как не вводим дополнительную кнопку не будет проблем с импортирование на андройд
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Select (Gdx.input.getX (), 720 - Gdx.input.getY ());
        }
        tanksController.update(dt);
        projectilesController.update(dt);
        map.update(dt);
        checkCollisions(dt);
    }

    private void Select(int x, int y) {
        boolean isTank = false;
        Vector2 tmp = new Vector2 ();
        for (Tank tank : tanksController.activeList) {
            if (isSelectTank (x, y, tmp, tank)) {
                isTank = true;
            }
        }
        if (isTank){
            for (Tank tank : tanksController.activeList) {
                tank.isSelected =(isSelectTank (x, y, tmp, tank));
            }
        }
    }

    private boolean isSelectTank(int x, int y, Vector2 tmp, Tank tank) {
        return tank.position.dst (tmp.set (x, y)) < 30.0f;
    }

    public void checkCollisions(float dt) {
    }
}
