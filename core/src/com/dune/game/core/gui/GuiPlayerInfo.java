package com.dune.game.core.gui;


import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.StringBuilder;
import com.dune.game.core.users_logic.PlayerLogic;

public class GuiPlayerInfo extends Group {
    private PlayerLogic playerLogic;
    private Label money;
    private Label unitsCount;
    private StringBuilder tmpSB;
    private Label name;

    public GuiPlayerInfo(PlayerLogic playerLogic, Skin skin) {
        this.playerLogic = playerLogic;
        this.money = new Label("", skin, "simpleLabel");
        this.name = new Label("", skin, "redLabel");
        this.unitsCount = new Label("", skin, "simpleLabel");
        this.money.setPosition(10, 10);
        this.name.setPosition(140, 10);
        this.unitsCount.setPosition(210, 10);
        this.addActor(money);
        this.addActor(name);
        this.addActor(unitsCount);
        this.tmpSB = new StringBuilder();
    }

    public void update(float dt) {
        tmpSB.clear();
        name.setText ("PLAYER");
        tmpSB.append("MINERALS: ").append(playerLogic.getMoney());
        money.setText(tmpSB);
        tmpSB.clear();
        tmpSB.append("UNITS: ").append(playerLogic.getUnitsCount()).append(" / ").append(playerLogic.getUnitsMaxCount());
        unitsCount.setText(tmpSB);
    }
}
