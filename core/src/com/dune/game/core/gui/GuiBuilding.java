package com.dune.game.core.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dune.game.screens.ScreenManager;
import com.dune.game.screens.utils.Assets;

public class GuiBuilding {
    private Stage stage;




    public void createGui(Vector2 position) {
        stage = new Stage (ScreenManager.getInstance().getViewport(), ScreenManager.getInstance().getBatch());
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        BitmapFont font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("simpleButton"), null, null, font24);

        final TextButton createTank = new TextButton("Create Tank", textButtonStyle);
        final TextButton createHarvester = new TextButton("Create Harvester", textButtonStyle);
        createTank.addListener(new ClickListener () {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               //dfg
            }
        });

        createHarvester.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               //jhb
            }
        });

        Group menuGroup = new Group();
        menuGroup.setPosition(position.x, position.y);
        createTank.setPosition(0, 0);
        createHarvester.setPosition(130, 0);
        menuGroup.addActor(createTank);
        menuGroup.addActor(createHarvester);
        stage.addActor(menuGroup);
        skin.dispose();
    }
}
