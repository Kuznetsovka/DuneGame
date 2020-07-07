package com.dune.game.core.users_logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dune.game.core.map.BattleMap;
import com.dune.game.core.Building;
import com.dune.game.core.GameController;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;

public class PlayerLogic extends BaseLogic {

    public PlayerLogic(GameController gc) {
        this.gc = gc;
        this.money = 1000;
        this.unitsMaxCount = 100;
        this.ownerType = Owner.PLAYER;
    }


    public void update(float dt) {
        this.unitsCount = gc.getUnitsController().getPlayerUnits ().size ();
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            if (gc.getSelectedUnits().size()>1){
                pointInFormation ();
            }
            for (int i = 0; i < gc.getSelectedUnits().size(); i++) {
                AbstractUnit u = gc.getSelectedUnits().get(i);
                if (u.getOwnerType() == Owner.PLAYER) {
                    unitProcessing(u);
                }
            }
        }
        resetContainer();
    }

    private void resetContainer() {
        gc.getUnitsController().collectTanks(tmpPlayerHarvesters, gc.getUnitsController().getPlayerUnits (), UnitType.HARVESTER);
        for (int i = 0; i < tmpPlayerHarvesters.size(); i++) {
            Harvester h = tmpPlayerHarvesters.get(i);
            if (h.isFull()) {
                for (int j = 0; j <gc.getBuildingsController ().getActiveList ().size () ; j++) {
                    Building b = gc.getBuildingsController ().getActiveList ().get(j);
                    if(h.getBaseLogic () == b.getOwnerLogic ()){
                        h.commandMoveTo(b.posEntrance (),true);
                    }
                }
            }
        }
    }

    public void unitProcessing(AbstractUnit unit) {
        if (!gc.isMultiSelect ())
            tmp = gc.getMouse ();
        else
            tmp = unit.getDestination();
        if (unit.getUnitType() == UnitType.HARVESTER) {
            Harvester h = (Harvester) unit;
            if (!h.isFull()) {
                unit.commandMoveTo (tmp, true);
            }
            return;
        }
        if (unit.getUnitType() == UnitType.BATTLE_TANK) {
            AbstractUnit aiUnit = gc.getUnitsController().getNearestAiUnit(gc.getMouse());
            if (aiUnit != null) {
                unit.commandAttack(aiUnit);
                return;
            }

            int mouseCellX = (int) (gc.getMouse().x / BattleMap.CELL_SIZE);
            int mouseCellY = (int) (gc.getMouse().y / BattleMap.CELL_SIZE);
            Building b = gc.getMap().getBuildingFromCell(mouseCellX, mouseCellY);
            if (b != null) {
                unit.commandAttack(b);
                return;
            }
            unit.commandMoveTo (tmp, true);
        }
    }

    private void pointInFormation() {
        tmp = gc.getMouse ();
        int n = (int)Math.ceil(Math.sqrt (gc.getSelectedUnits ().size()));
        // центр
        int i = n / 2;
        int j = n / 2;
        // задаем границы движения
        int min_i = i; int max_i = i; // влево вправо
        int min_j = j; int max_j = j; // вверх вниз
        int d = 0; // сначала пойдем влево
        while(gc.getUnitsController ().getSelectUnits ().size()>1) {
            int indexUnit = gc.getUnitsController ().getNearestInxSelectToDest(tmp);
            AbstractUnit u = gc.getUnitsController ().getSelectUnits ().get(indexUnit);
            u.newDestination(tmp);
            gc.getUnitsController ().clearIndexSelected (indexUnit);
            switch (d) {
                case 0:
                    i -= 1;  // движение влево
                    tmp.set (tmp.x-u.CORE_SIZE,tmp.y);
                    if (i < min_i) { // проверка выхода за заполненную центральную часть слева
                        d = 1; // меняем направление
                        min_i = i; // увеличиваем заполненную часть влево
                    }
                    break;
                case 1:  // движение вверх проверка сверху
                    tmp.set (tmp.x,tmp.y+u.CORE_SIZE);
                    j -= 1;
                    if (j < min_j) {
                        d = 2;
                        min_j = j;
                    }
                    break;
                case 2:  // движение вправо проверка справа
                    tmp.set (tmp.x+u.CORE_SIZE,tmp.y);
                    i += 1;
                    if (i > max_i) {
                        d = 3;
                        max_i = i;
                    }
                    break;
                case 3:  // движение вниз проверка снизу
                    tmp.set (tmp.x,tmp.y-u.CORE_SIZE);
                    j += 1;
                    if (j > max_j) {
                        d = 0;
                        max_j = j;
                    }
            }
        }
    }
}
