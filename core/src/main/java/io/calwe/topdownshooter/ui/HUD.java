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
    // the stage stores all the actors
    private Stage stage;
    // the table is the common way to structure a scene2d ui
    private Table table;

    public Inventory inventory;
    public Statistic healthUI;
    public Statistic scoreUI;

    public HUD(int health, int score) {
        // create a viewport which scales with the screen
        ScreenViewport viewport = new ScreenViewport();
        // 0.2 units per pixel = 5 pixels per unit
        viewport.setUnitsPerPixel(0.2f);
        stage = new Stage(viewport);

        table = new Table();
        // the root table should fill the entire viewport
        table.setFillParent(true);

        inventory = new Inventory();
        // begin rendering from the top of the table
        table.top();
        // add the inventory, relative to the top left of the cell.
        // expand the X so that it fills the top row
        // pad the inventory so it doesnt touch the edge
        table.add(inventory).top().left().expandX().pad(4f, 2f, 2f, 2f);

        healthUI = new Statistic("health", health);
        scoreUI = new Statistic("score", score);
        // add the health to the top row
        // as the inventory has been "expanded", this will go to the end of the row.
        table.add(healthUI);
        // create a new row
        table.row();
        // add the score UI to the right. colspan(2) is used to make it take 2 columns,
        // as all rows in a table must have an equal number of columns
        table.add(scoreUI).colspan(2).right();

        stage.addActor(table);

        // the input multiplexer allows us to send input events to multiple places
        // this is needed as the stage treats inputs diffently (actors only recieve events if focussed)
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inventory.weaponSwitcher);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void resize(int width, int height) {
        // update the viewport size if the screen size changes
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
