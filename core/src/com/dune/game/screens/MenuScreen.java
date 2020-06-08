package com.dune.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dune.game.core.Assets;

public class MenuScreen extends AbstractScreen {
    private Stage stage;
    private TextButton startBtn;

    public MenuScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        createGuiMenuInput();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw ();
        update(delta);
    }

    public void update(float dt) {
        stage.act(dt);
    }

    public void createGuiMenuInput() {
        stage = new Stage (ScreenManager.getInstance().getViewport(), ScreenManager.getInstance().getBatch());
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        BitmapFont font14 = Assets.getInstance().getAssetManager().get("fonts/font14.ttf");
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("smButton"), null, null, font14);
        startBtn = new TextButton("Start", textButtonStyle);
        startBtn.addListener(new ClickListener () {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });

        final TextButton endBtn = new TextButton("End", textButtonStyle);
        endBtn.addListener(new ClickListener () {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        Group menuGroup = new Group();
        startBtn.setPosition(0, 0);
        endBtn.setPosition(130, 0);
        menuGroup.addActor(startBtn);
        menuGroup.addActor(endBtn);
        menuGroup.setPosition(ScreenManager.HALF_WORLD_WIDTH-130, ScreenManager.HALF_WORLD_HEIGHT-20);
        stage.addActor(menuGroup);
        skin.dispose();
    }

    @Override
    public void dispose() {
    }
}