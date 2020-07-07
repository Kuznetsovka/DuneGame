package com.dune.game.core.users_logic;

import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.GameController;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.types.Owner;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class BaseLogic {
    protected Owner ownerType;
    protected GameController gc;
    protected List<Harvester> tmpPlayerHarvesters;
    protected List<BattleTank> tmpPlayerBattleTanks;
    protected List<BattleTank> tmpAiBattleTanks;
    protected List<Harvester> tmpAiHarvesters;
    protected List<AbstractUnit> tmpPlayers;
    protected Vector2 tmp;
    protected int money;
    protected int unitsCount;
    protected int unitsMaxCount;

    public BaseLogic() {
        this.tmpAiBattleTanks = new ArrayList<> ();
        this.tmpPlayerHarvesters = new ArrayList<>();
        this.tmpPlayerBattleTanks = new ArrayList<>();
        this.tmpPlayers = new ArrayList<> ();
        this.tmpAiHarvesters = new ArrayList<> ();
        this.tmp = new Vector2();
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    protected  <T extends AbstractUnit> T findNearestTarget(AbstractUnit currentTank, List<T> possibleTargetList) {
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

    protected  <T extends Targetable> T findNearestResources(AbstractUnit abstractUnit, List<T> cellResource) {
        T target = null;
        float minDist = 1000000.0f;
        for (int i = 0; i < cellResource.size(); i++) {
            T possibleTarget = cellResource.get(i);
            float currentDst = abstractUnit.getPosition().dst(possibleTarget.getPosition ());
            if (currentDst < minDist) {
                target = possibleTarget;
                minDist = currentDst;
            }
        }
        return target;
    }
}
