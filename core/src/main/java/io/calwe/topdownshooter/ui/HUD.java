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

    public void create() {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(0.2f);
        stage = new Stage(viewport);

        table = new Table();
        table.setFillParent(true);

        inventory = new Inventory();
        table.top().left();
        table.add(inventory);

        stage.addActor(table);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inventory.weaponSwitcher);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
