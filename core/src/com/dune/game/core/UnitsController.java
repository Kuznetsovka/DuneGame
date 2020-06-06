package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Owner;
import com.dune.game.core.units.UnitType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnitsController {
    private GameController gc;
    private BattleTanksController battleTanksController;
    private HarvestersController harvestersController;
    private List<AbstractUnit> units;
    private List<AbstractUnit> playerUnits;
    private List<AbstractUnit> aiUnits;
    private List<AbstractUnit> aiHarvesterUnits;
    private List<AbstractUnit> aiTankUnits;

    public List<AbstractUnit> getAiHarvesterUnits() {
        return aiHarvesterUnits;
    }

    public List<AbstractUnit> getAiTankUnits() {
        return aiTankUnits;
    }

    public List<AbstractUnit> getUnits() {
        return units;
    }

    public List<AbstractUnit> getPlayerUnits() {
        return playerUnits;
    }

    public List<AbstractUnit> getAiUnits() {
        return aiUnits;
    }

    public UnitsController(GameController gc) {
        this.gc = gc;
        this.battleTanksController = new BattleTanksController(gc);
        this.harvestersController = new HarvestersController(gc);
        this.units = new ArrayList<>();
        this.playerUnits = new ArrayList<>();
        this.aiUnits = new ArrayList<>();
        this.aiHarvesterUnits = new ArrayList<> ();
        this.aiTankUnits = new ArrayList<> ();
        for (int i = 0; i < 5; i++) {
            createBattleTank(Owner.PLAYER, MathUtils.random(80, 1200), MathUtils.random(80, 640));
        }
        for (int i = 0; i < 2; i++) {
            createHarvester(Owner.PLAYER, MathUtils.random(80, 1200), MathUtils.random(80, 640));
        }
        for (int i = 0; i < 2; i++) {
            createBattleTank(Owner.AI, MathUtils.random(80, 1200), MathUtils.random(80, 640));
        }
        for (int i = 0; i < 2; i++) {
            createHarvester(Owner.AI, MathUtils.random(80, 1200), MathUtils.random(80, 640));
        }
    }

    public void createBattleTank(Owner owner, float x, float y) {
        battleTanksController.setup(x, y, owner);
    }

    public void createHarvester(Owner owner, float x, float y) {
        harvestersController.setup(x, y, owner);
    }

    public void update(float dt) {
        battleTanksController.update(dt);
        harvestersController.update(dt);
        units.clear();
        aiUnits.clear();
        playerUnits.clear();
        aiTankUnits.clear();
        aiHarvesterUnits.clear();
        units.addAll(battleTanksController.getActiveList());
        units.addAll(harvestersController.getActiveList());
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getOwnerType() == Owner.AI) {
                aiUnits.add(units.get(i));
                if (units.get(i).getUnitType () == UnitType.BATTLE_TANK){
                    aiTankUnits.add(units.get(i));
                }
                if (units.get(i).getUnitType () == UnitType.HARVESTER){
                    aiHarvesterUnits.add(units.get(i));
                }
            }
            if (units.get(i).getOwnerType() == Owner.PLAYER) {
                playerUnits.add(units.get(i));
            }
        }
    }

    public void render(SpriteBatch batch) {
        battleTanksController.render(batch);
        harvestersController.render(batch);
    }

    public AbstractUnit getNearestCompetitorUnit(Vector2 point) {
        for (int i = 0; i < aiUnits.size(); i++) {
            AbstractUnit u = aiUnits.get(i);
            if (u.getPosition().dst(point) < 30) {
                return u;
            }
        }
        return null;
    }

    public AbstractUnit getNearestCompetitorUnit(Vector2 point, Owner typeCompetitor) {
        List<AbstractUnit> competitorList = new ArrayList<> ();
        List<Float> dst = new ArrayList<> ();
        if (typeCompetitor==Owner.AI)
            competitorList.addAll(aiUnits);
        else
            competitorList.addAll(playerUnits);

        for (int i = 0; i < competitorList.size(); i++) {
            AbstractUnit u = competitorList.get(i);
            dst.add (u.getPosition().dst(point));
        }
        return competitorList.get(dst.indexOf (Collections.min(dst)));
    }

}