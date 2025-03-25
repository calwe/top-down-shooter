package io.calwe.topdownshooter.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HUD {
    private Stage stage;
    private Table table;

    public Inventory inventory;
    public Statistic healthUI;
    public Statistic scoreUI;

    public HUD(int health, int score) {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(0.2f);
        stage = new Stage(viewport);

        table = new Table();
        table.setFillParent(true);

        inventory = new Inventory();
        table.top();
        table.add(inventory).top().left().expandX();

        healthUI = new Statistic("health", health);
        scoreUI = new Statistic("score", score);
        table.add(healthUI);
        table.row();
        table.add(scoreUI).colspan(2).right();

        stage.addActor(table);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inventory.weaponSwitcher);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void updateStatistics(int health, int score) {
        healthUI.setValue(health);
        scoreUI.setValue(score);
    }

    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }
}
