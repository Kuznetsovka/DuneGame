package com.dune.game.core;

import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Owner;
import com.dune.game.core.units.UnitType;

public class AiLogic {
    public GameController gc;

    public AiLogic(GameController gc) {
        this.gc = gc;
    }

    public void update(float dt) {
        try {
            for (int i = 0; i < gc.getUnitsController ().getAiUnits ().size (); i++) {
                AbstractUnit u = gc.getUnitsController ().getAiUnits ().get (i);
                if (u.getUnitType () == UnitType.BATTLE_TANK && u.getTarget () == null) {
                    battleProcessing (u);
                }
                if (u.getUnitType () == UnitType.HARVESTER && u.getTarget () == null) {
                    harvesterProcessing (u);
                }
            }
        } catch (IndexOutOfBoundsException e) {

        }
    }

    private void harvesterProcessing(AbstractUnit unit) {
        for (int i = 0; i < gc.getUnitsController ().getAiHarvesterUnits ().size (); i++) {
            Targetable tmp = gc.getMap ().getNearestResource (unit.getPosition ());
            if (tmp!=null)
                unit.commandAttack (tmp);
        }
    }

    public void battleProcessing(AbstractUnit unit) {
        AbstractUnit playerUnit = gc.getUnitsController().getNearestCompetitorUnit (unit.getPosition (), Owner.PLAYER);
        if (playerUnit != null) {
            for (int j = 0; j < gc.getUnitsController ().getAiTankUnits ().size (); j++) {
                gc.getUnitsController ().getAiTankUnits ().get (j).commandAttack (playerUnit);
            }
        }

    }
}
