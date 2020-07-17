package com.dune.game.core;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dune.game.core.controllers.BuildingsController;
import com.dune.game.core.controllers.ParticleController;
import com.dune.game.core.controllers.ProjectilesController;
import com.dune.game.core.controllers.UnitsController;
import com.dune.game.core.gui.GuiAiInfo;
import com.dune.game.core.gui.GuiBuilding;
import com.dune.game.core.gui.GuiPlayerInfo;
import com.dune.game.core.map.BattleMap;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.users_logic.AiLogic;
import com.dune.game.core.users_logic.PlayerLogic;
import com.dune.game.core.utils.Collider;
import com.dune.game.screens.ScreenManager;
import com.dune.game.screens.utils.Assets;
import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameController {
    private static final float CAMERA_SPEED = 240.0f;
    private BattleMap map;
    private GuiPlayerInfo guiPlayerInfo;
    private GuiAiInfo guiAiInfo;
    private PlayerLogic playerLogic;
    private AiLogic aiLogic;
    private ProjectilesController projectilesController;
    private ParticleController particleController;
    private UnitsController unitsController;
    private BuildingsController buildingsController;
    private PathFinder pathFinder;
    private Vector2 tmp;
    private Vector2 selectionStart;
    private Vector2 selectionEnd;
    private Vector2 mouse;
    private Collider collider;
    private Vector2 pointOfView;
    private float worldTimer;
    private boolean paused;
    private List<AbstractUnit> selectedUnits;
    private Stage stage;
    private boolean isMultiSelect;
    private GuiBuilding guiBuilding;

//    private Music music;
//    private Sound sound;

    public GameController() {
        this.mouse = new Vector2();
        this.tmp = new Vector2();

        this.map = new BattleMap();

        this.aiLogic = new AiLogic(this);
        this.playerLogic = new PlayerLogic(this);

        this.buildingsController = new BuildingsController(this);
        this.buildingsController.setup(3, 3, playerLogic);
        this.buildingsController.setup(14, 8, aiLogic);

        this.collider = new Collider(this);
        this.selectionStart = new Vector2(-1, -1);
        this.selectionEnd = new Vector2(-1, -1);
        this.selectedUnits = new ArrayList<>();
        this.pathFinder = new PathFinder(map);
        this.projectilesController = new ProjectilesController(this);
        this.particleController = new ParticleController();
        this.unitsController = new UnitsController(this);
        this.pointOfView = new Vector2(ScreenManager.HALF_WORLD_WIDTH, ScreenManager.HALF_WORLD_HEIGHT);
//        this.music = Gdx.audio.newMusic(Gdx.files.internal("1.mp3"));
//        this.sound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        createGuiAndPrepareGameInput();
    }

    public void update(float dt) {
        if (!paused) {
            worldTimer += dt;
            ScreenManager.getInstance().pointCameraTo(getPointOfView());
            mouse.set(Gdx.input.getX(), Gdx.input.getY());
            ScreenManager.getInstance().getViewport().unproject(mouse);
            unitsController.update(dt);
            playerLogic.update(dt);
            aiLogic.update(dt);
            buildingsController.update(dt);
            projectilesController.update(dt);
            map.update(dt);
            collider.checkCollisions();
            particleController.update(dt);
            guiPlayerInfo.update(dt);
            guiAiInfo.update(dt);
        }
        ScreenManager.getInstance().resetCamera();
        stage.act(dt);
        changePOV(dt);
    }

    public void changePOV(float dt) {
        if (Gdx.input.getY() < 10) {
            pointOfView.y += CAMERA_SPEED * dt;
            if (pointOfView.y + ScreenManager.HALF_WORLD_HEIGHT > BattleMap.MAP_HEIGHT_PX) {
                pointOfView.y = BattleMap.MAP_HEIGHT_PX - ScreenManager.HALF_WORLD_HEIGHT;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getY() > 710) {
            pointOfView.y -= CAMERA_SPEED * dt;
            if (pointOfView.y < ScreenManager.HALF_WORLD_HEIGHT) {
                pointOfView.y = ScreenManager.HALF_WORLD_HEIGHT;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getX() < 10) {
            pointOfView.x -= CAMERA_SPEED * dt;
            if (pointOfView.x < ScreenManager.HALF_WORLD_WIDTH) {
                pointOfView.x = ScreenManager.HALF_WORLD_WIDTH;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getX() > 1270) {
            pointOfView.x += CAMERA_SPEED * dt;
            if (pointOfView.x + ScreenManager.HALF_WORLD_WIDTH > BattleMap.MAP_WIDTH_PX) {
                pointOfView.x = BattleMap.MAP_WIDTH_PX - ScreenManager.HALF_WORLD_WIDTH;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }

    }

    public boolean isUnitSelected(AbstractUnit abstractUnit) {
        return selectedUnits.contains(abstractUnit);
    }

    public InputProcessor prepareInput() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    selectionStart.set(mouse);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    isMultiSelect = false;
                    tmp.set(mouse);

                    if (tmp.x < selectionStart.x) {
                        float t = tmp.x;
                        tmp.x = selectionStart.x;
                        selectionStart.x = t;
                    }
                    if (tmp.y > selectionStart.y) {
                        float t = tmp.y;
                        tmp.y = selectionStart.y;
                        selectionStart.y = t;
                    }

                    selectedUnits.clear();
                    if (Math.abs(tmp.x - selectionStart.x) > 20 & Math.abs(tmp.y - selectionStart.y) > 20) {
                        for (int i = 0; i < unitsController.getPlayerUnits().size(); i++) {
                            AbstractUnit t = unitsController.getPlayerUnits().get(i);
                            if (t.getPosition().x > selectionStart.x && t.getPosition().x < tmp.x
                                    && t.getPosition().y > tmp.y && t.getPosition().y < selectionStart.y
                            ) {
                                selectedUnits.add(t);
                            }
                        }
                    } else {
                        for (int i = 0; i < unitsController.getUnits().size(); i++) {
                            AbstractUnit t = unitsController.getUnits().get(i);
                            if (t.getPosition().dst(tmp) < 30.0f) {
                                selectedUnits.add(t);
                            }
                        }
                        Building b=map.getBuildingFromPoint(tmp);
                        if (b!=null && b.getOwnerLogic () == playerLogic && b.getBuildingType () == Building.Type.STOCK) {
                            guiBuilding.setVisible (true);
                            guiBuilding.setPosition(tmp.x,tmp.y);
                        }
                    }
                    if (selectedUnits.size()>1)
                        isMultiSelect = true;

                    selectionStart.set(-1, -1);
                }
                return true;
            }
        };
    }

    public void createGuiAndPrepareGameInput() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), ScreenManager.getInstance().getBatch());
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, prepareInput()));
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        BitmapFont font14 = Assets.getInstance().getAssetManager().get("fonts/font14.ttf");
        Label.LabelStyle labelStyle = new Label.LabelStyle(font14, Color.WHITE);
        Label.LabelStyle labelStyle2 = new Label.LabelStyle(font14, Color.RED);
        skin.add("simpleLabel", labelStyle);
        skin.add("redLabel", labelStyle2);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("smButton"), null, null, font14);

        final TextButton menuBtn = new TextButton("Menu", textButtonStyle);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        final TextButton pauseBtn = new TextButton("Pause", textButtonStyle);
        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = !paused;
            }
        });

        menuBtn.setPosition(0, 0);
        pauseBtn.setPosition(130, 0);
        Group menuGroup = new Group();
        menuGroup.addActor(menuBtn);
        menuGroup.addActor(pauseBtn);
        menuGroup.setPosition(900, 10);
        guiBuilding = new GuiBuilding (skin,textButtonStyle);
        guiBuilding.setVisible (false);
        guiBuilding.getCreateTankBtn().addListener(new ClickListener () {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getUnitsController ().setupBattleTank (playerLogic,true);

            }
        });

        guiBuilding.getCreateHarvesterBtn().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getUnitsController ().setupHarvester (playerLogic,true);

            }
        });

        guiBuilding.getCloseBtn ().addListener(new ClickListener () {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                guiBuilding.setVisible (false);

            }
        });

        guiPlayerInfo = new GuiPlayerInfo(playerLogic, skin);
        guiPlayerInfo.setPosition(0, 700);
        guiAiInfo = new GuiAiInfo (aiLogic, skin);
        guiAiInfo.setPosition(900, 700);
        stage.addActor(guiAiInfo);
        stage.addActor(guiPlayerInfo);
        stage.addActor(guiBuilding);
        skin.dispose();
    }
}