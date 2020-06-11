package com.dune.game.core.users_logic;

import com.dune.game.core.Building;
import com.dune.game.core.GameController;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;

import java.util.ArrayList;
import java.util.List;

public class AiLogic extends BaseLogic {
    private float timer;

    private List<BattleTank> tmpAiBattleTanks;
    private List<Harvester> tmpPlayerHarvesters;
    private List<Harvester> tmpPlayerBattleTanks;
    private List<Harvester> tmpAiHarvesters;

    public AiLogic(GameController gc) {
        this.gc = gc;
        this.money = 1000;
        this.unitsCount = 10;
        this.unitsMaxCount = 100;
        this.ownerType = Owner.AI;
        this.tmpAiBattleTanks = new ArrayList<>();
        this.tmpPlayerHarvesters = new ArrayList<>();
        this.tmpAiHarvesters = new ArrayList<>();
        this.tmpPlayerBattleTanks = new ArrayList<>();
        this.timer = 10000.0f;
    }

    public void update(float dt) {
        timer += dt;
        if (timer > 2.0f) {
            timer = 0.0f;
            gc.getUnitsController().collectTanks(tmpAiBattleTanks, gc.getUnitsController().getAiUnits(), UnitType.BATTLE_TANK);
            gc.getUnitsController().collectTanks(tmpPlayerHarvesters, gc.getUnitsController().getPlayerUnits(), UnitType.HARVESTER);
            gc.getUnitsController().collectTanks(tmpAiHarvesters, gc.getUnitsController().getAiUnits (), UnitType.HARVESTER);
            gc.getUnitsController().collectTanks(tmpPlayerBattleTanks, gc.getUnitsController().getPlayerUnits(), UnitType.BATTLE_TANK);
            for (int i = 0; i < tmpAiBattleTanks.size(); i++) {
                BattleTank aiBattleTank = tmpAiBattleTanks.get(i);
                aiBattleTank.commandAttack(findNearestTarget(aiBattleTank, tmpPlayerBattleTanks));
            }
            for (int i = 0; i < tmpAiHarvesters.size(); i++) {
                Harvester aiHarvester = tmpAiHarvesters.get(i);
                if (!aiHarvester.isFull()) {
                    aiHarvester.commandAttack (findNearestResources (aiHarvester, gc.getMap ().cellResource ()));
                } else {
                    for (int j = 0; j <gc.getBuildingsController ().getActiveList ().size () ; j++) {
                        Building b = gc.getBuildingsController ().getActiveList ().get(j);
                        if(aiHarvester.getBaseLogic () == b.getOwnerLogic ()){
                            aiHarvester.commandMoveTo(b.posEntrance ());
                        }
                    }


                }

            }
        }
    }

    private <T extends Targetable> T findNearestResources(Harvester aiHarvester, List<T> cellResource) {
        T target = null;
        float minDist = 1000000.0f;
        for (int i = 0; i < cellResource.size(); i++) {
            T possibleTarget = cellResource.get(i);
            float currentDst = aiHarvester.getPosition().dst(possibleTarget.getPosition ());
            if (currentDst < minDist) {
                target = possibleTarget;
                minDist = currentDst;
            }
        }
        return target;
    }

    public <T extends AbstractUnit> T findNearestTarget(AbstractUnit currentTank, List<T> possibleTargetList) {
        T target = null;
        float minDist = 1000000.0f;
        for (int i = 0; i < possibleTargetList.size(); i++) {
            T possibleTarget = possibleTargetList.get(i);
            float currentDst = currentTank.getPosition().dst(possibleTarget.getPosition());
            if (currentDst < minDist) {
                target = possibleTarget;
                minDist = currentDst;
            }
        }
        return target;
    }
}
