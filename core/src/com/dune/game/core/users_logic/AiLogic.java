package com.dune.game.core.users_logic;

import com.dune.game.core.Building;
import com.dune.game.core.GameController;
import com.dune.game.core.units.BattleTank;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;

public class AiLogic extends BaseLogic {
    private float timer;

    public AiLogic(GameController gc) {
        this.gc = gc;
        this.money = 1000;
        this.unitsMaxCount = 100;
        this.ownerType = Owner.AI;
        this.timer = 10000.0f;
    }

    public void update(float dt) {
        timer += dt;
        this.unitsCount = gc.getUnitsController().getAiUnits().size ();
        if (timer > 2.0f) {
            timer = 0.0f;
            gc.getUnitsController().collectTanksByOwner(tmpAiBattleTanks, this, UnitType.BATTLE_TANK);
            gc.getUnitsController().collectTanksExcludeOwner(tmpPlayerHarvesters, this, UnitType.HARVESTER);
            gc.getUnitsController().collectTanksByOwner(tmpAiHarvesters, this, UnitType.HARVESTER);
            gc.getUnitsController().collectTanksExcludeOwner(tmpPlayerBattleTanks, this, UnitType.BATTLE_TANK);
            for (int i = 0; i < tmpAiBattleTanks.size(); i++) {
                BattleTank aiBattleTank = tmpAiBattleTanks.get(i);
                if (aiBattleTank.getTarget() != null) {
                    continue;
                }
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
                            aiHarvester.commandMoveTo(b.posEntrance (),true);
                        }
                    }
                }
            }
        }
    }
}
