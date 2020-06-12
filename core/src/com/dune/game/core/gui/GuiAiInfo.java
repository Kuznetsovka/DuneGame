package com.dune.game.core.gui;



import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.StringBuilder;
import com.dune.game.core.users_logic.AiLogic;

public class GuiAiInfo extends Group {
    private AiLogic aiLogic;
    private Label money;
    private Label unitsCount;
    private StringBuilder tmpSB;
    private Label name;

    public GuiAiInfo(AiLogic aiLogic, Skin skin) {
        this.aiLogic = aiLogic;
        this.money = new Label("", skin, "simpleLabel");
        this.unitsCount = new Label("", skin, "simpleLabel");
        this.name = new Label("", skin, "redLabel");
        this.money.setPosition(10, 10);
        this.name.setPosition(160, 10);
        this.unitsCount.setPosition(210, 10);
        this.addActor(money);
        this.addActor(name);
        this.addActor(unitsCount);
        this.tmpSB = new StringBuilder();
    }

    public void update(float dt) {
        tmpSB.clear();
        name.setText ("AI");
        tmpSB.append("MINERALS: ").append(aiLogic.getMoney());
        money.setText(tmpSB);
        tmpSB.clear();
        tmpSB.append("UNITS: ").append(aiLogic.getUnitsCount()).append(" / ").append(aiLogic.getUnitsMaxCount());
        unitsCount.setText(tmpSB);
    }
}
