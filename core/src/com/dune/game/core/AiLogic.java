package com.dune.game.core;

import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Owner;
import com.dune.game.core.units.UnitType;

public class AiLogic {
    public GameController gc;

    public void update(float dt) {
        for (int i = 0; i < gc.getSelectedUnits().size(); i++) {
            AbstractUnit u = gc.getSelectedUnits().get(i);
            if (u.getOwnerType() == Owner.PLAYER) {
                unitProcessing(u);
            }
        }
    }

    public void unitProcessing(AbstractUnit unit) {
        if (unit.getUnitType() == UnitType.HARVESTER) {
            unit.commandMoveTo(gc.getMouse());
            return;
        }
        if (unit.getUnitType() == UnitType.BATTLE_TANK) {
            AbstractUnit aiUnit = gc.getUnitsController().getNearestAiUnit(gc.getMouse());
            if (aiUnit == null) {
                unit.commandMoveTo(gc.getMouse());
            } else {
                unit.commandAttack(aiUnit);
            }
        }
    }
}
