package com.dune.game.core.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.Building;
import com.dune.game.core.map.BattleMap;
import com.dune.game.core.GameController;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;
import com.dune.game.core.users_logic.BaseLogic;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class UnitsController {
    private GameController gc;
    private Vector2 tmp;
    private BattleTanksController battleTanksController;
    private HarvestersController harvestersController;
    private List<AbstractUnit> units;
    private List<AbstractUnit> playerUnits;
    private List<AbstractUnit> aiUnits;
    private List<AbstractUnit> selectUnits;

    public UnitsController(GameController gc) {
        this.gc = gc;
        this.battleTanksController = new BattleTanksController(gc);
        this.harvestersController = new HarvestersController(gc);
        this.units = new ArrayList<>();
        this.playerUnits = new ArrayList<>();
        this.aiUnits = new ArrayList<>();
        this.selectUnits = new ArrayList<> ();
        this.tmp = new Vector2 ();
        for (int i = 0; i < 7; i++) {
            setupBattleTank (gc.getPlayerLogic (),false);
        }
        setupHarvester (gc.getPlayerLogic (),false);
        setupHarvester (gc.getPlayerLogic (),false);
        for (int i = 0; i < 1; i++) {
            setupBattleTank (gc.getAiLogic (),false);
        }
        setupHarvester (gc.getAiLogic (),false);
    }

    private Vector2 choicePoint(BaseLogic baseLogic) {
        float X,Y;
        Vector2 basePosition = basePosition (baseLogic);
        do {
            X = MathUtils.random (0, gc.getMap ().getSizeX () - 1) * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2;
            Y = MathUtils.random (0, gc.getMap ().getSizeY () - 1) * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2;
        } while(! gc.getMap ().isCellPassable(tmp.set (X,Y),false) || tmp.set(X,Y).dst(basePosition)>200);
        return tmp;
    }

    public Vector2 basePosition(BaseLogic baseLogic) {
        Vector2 basePosition = new Vector2 ();

        for (int i = 0; i < gc.getBuildingsController ().getActiveList ().size (); i++) {
            Building b = gc.getBuildingsController ().getActiveList ().get(i);
            if (b.getBuildingType ()== Building.Type.STOCK && b.getOwnerLogic ().getOwnerType () == baseLogic.getOwnerType ()) {
                basePosition = b.getPosition ();
            }
        }
        return basePosition;
    }

    public void createBattleTank(BaseLogic baseLogic, float x, float y) {
        battleTanksController.setup(x, y, baseLogic);
    }

    public void createHarvester(BaseLogic baseLogic, float x, float y) {
        harvestersController.setup(x, y, baseLogic);
    }

    public void update(float dt) {
        battleTanksController.update(dt);
        harvestersController.update(dt);
        units.clear();
        aiUnits.clear();
        playerUnits.clear();
        units.addAll(battleTanksController.getActiveList());
        units.addAll(harvestersController.getActiveList());

        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getOwnerType() == Owner.AI) {
                aiUnits.add(units.get(i));
            }
            if (units.get(i).getOwnerType() == Owner.PLAYER) {
                playerUnits.add(units.get(i));
                if (units.get(i).isSelect ()){
                    selectUnits.add(units.get(i));
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        battleTanksController.render(batch);
        harvestersController.render(batch);
    }

    public void clearIndexSelected(int index){
        selectUnits.remove (index);
    }
    public AbstractUnit getNearestAiUnit(Vector2 point) {
        for (int i = 0; i < aiUnits.size(); i++) {
            AbstractUnit u = aiUnits.get(i);
            if (u.getPosition().dst(point) < 30) {
                return u;
            }
        }
        return null;
    }

    public int getNearestInxSelectToDest(Vector2 point) {
        float minTime = 100000f;
        int index=0;
        for (int i = 0; i < selectUnits.size (); i++) {
            float uDst = selectUnits.get (i).getPosition ().dst (point);
            float uTime = uDst / selectUnits.get (i).getSpeed ();
            if (uTime < minTime) {
                minTime = uTime;
                index = i;
            }
        }
        return index;
    }

    public <T> void collectTanks(List<T> out, List<AbstractUnit> srcList, UnitType unitType) {
        out.clear();
        for (int i = 0; i < srcList.size(); i++) {
            AbstractUnit au = srcList.get(i);
            if (au.getUnitType () == unitType) {
                out.add ((T) au);
            }
        }
    }

    public <T> void collectTanksExcludeOwner(List<T> out, BaseLogic ownerLogic, UnitType unitType) {
        out.clear();
        for (int i = 0; i < units.size(); i++) {
            AbstractUnit au = units.get(i);
            if (au.getUnitType() == unitType && au.getBaseLogic() != ownerLogic) {
                out.add((T) au);
            }
        }
    }

    public <T> void collectTanksByOwner(List<T> out, BaseLogic ownerLogic, UnitType unitType) {
        out.clear();
        for (int i = 0; i < units.size(); i++) {
            AbstractUnit au = units.get(i);
            if (au.getUnitType() == unitType && au.getBaseLogic() == ownerLogic) {
                out.add((T) au);
            }
        }
    }

    public void setupBattleTank(BaseLogic ownerLogic, boolean isCreateByPlayer) {
        boolean isEnough = true;
        if (isCreateByPlayer)
            isEnough = ownerLogic.subMoney(500);
        if (isEnough) {
            choicePoint (ownerLogic);
            createBattleTank (ownerLogic, tmp.x, tmp.y);
        }
    }

    public void setupHarvester(BaseLogic ownerLogic, boolean isCreateByPlayer) {
        boolean isEnough = true;
        if (isCreateByPlayer)
             isEnough = ownerLogic.subMoney(1000);
        if (isEnough) {
            choicePoint (ownerLogic);
            createHarvester (ownerLogic, tmp.x, tmp.y);
        }
    }
}