package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.entities.Weapon;

import java.util.Random;

public class Play implements Screen {
    // libGDX uses floats for viewport width/height, so the scale should also be a float
    private static final float PIXEL_SCALE = 5f;

    private OrthographicCamera camera;

    private Map map;

    public static Array<Entity> entities;
    public SpriteBatch batch;

    @Override
    // show is called whenever this screen is shown
    // it essentially acts as a constructor for the screen
    public void show() {
        // create the map
        map = new Map();
        batch = new SpriteBatch();
        entities = new Array<>();
        Texture noTexture = new Texture("NoTexture.png");
        // create a new orthographic (no 3d perspective) camera, and set its position to the center of the map
        camera = new OrthographicCamera();
        camera.position.set(Map.MAP_WIDTH * Map.TILE_SIZE / 2f, Map.MAP_HEIGHT * Map.TILE_SIZE / 2f, 0);
        entities.add(new Player(new Texture("player_single_frame.png"), new Vector2(Map.MAP_WIDTH * Map.TILE_SIZE / 2f, Map.MAP_HEIGHT * Map.TILE_SIZE / 2f), new Weapon[10], camera));
    }

    @Override
    // render is called once every frame
    public void render(float v) {
        input();
        logic();
        // clear the screen to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // recalculate camera projection and view matrices, in case its properties have changed (position, size, etc...)
        camera.update();

        // render the map to the camera
        map.render(camera);

        draw();
    }

    public void input() {
        for (Entity e : entities) {
            e.input(camera);
        }
    }
    public void logic() {
        for (Entity e : entities) {
            e.logic();
        }
    }

    public void draw(){
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Entity e : entities) {
            e.draw(batch);
        }
        batch.end();
    }

    @Override
    // resize is called whenever the window is resized
    public void resize(int width, int height) {
        // resize the camera viewport to match the window size
        // dividing the width and height by a scale zooms the camera
        camera.viewportWidth = width / PIXEL_SCALE;
        camera.viewportHeight = height / PIXEL_SCALE;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    // dispose is called when this screen is destroyed, acting as a "destructor" for the screen
    public void dispose() {
        // dispose of the map
        map.dispose();
    }
}
