package com.dune.game.core.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import com.dune.game.screens.utils.Assets;
import lombok.Getter;

@Getter
public class GuiBuilding extends Group {
    private TextButton createTankBtn;
    private TextButton createHarvesterBtn;
    private TextButton closeBtn;

    public GuiBuilding(Skin skin, TextButton.TextButtonStyle textButtonStyle) {
        BitmapFont font14 = Assets.getInstance().getAssetManager().get("fonts/font14.ttf");
        TextButton.TextButtonStyle textCloseBtnStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("shortButton"), null, null, font14);
        createTankBtn = new TextButton("Tank", textButtonStyle);
        createHarvesterBtn = new TextButton("Harvester", textButtonStyle);
        closeBtn = new TextButton("X", textCloseBtnStyle);
        addActor(createTankBtn);
        addActor(createHarvesterBtn);
        addActor(closeBtn);
        createTankBtn.setPosition(0, 0);
        createHarvesterBtn.setPosition(130, 0);
        closeBtn.setPosition (250,40);
    }
}
