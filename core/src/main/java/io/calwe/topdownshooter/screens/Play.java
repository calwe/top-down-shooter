package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Enemy;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.entities.Weapon;

import java.util.*;

public class Play implements Screen {
    // libGDX uses floats for viewport width/height, so the scale should also be a float
    private static final float PIXEL_SCALE = 5f;

    private OrthographicCamera camera;

    private Map map;

    // A master list of every moving object in the world, so that they can all be updated and rendered easily
    private static List<Entity> entities;
    //Separate lists are required for adding and removing entities, as entities creating or destroying other entities
    //is generally handled in entities' logic function, which is run by a function iterating through each entity
    //and running their logic. Adding or removing from a list that is being iterated through can cause problems,
    //so entity creation and destruction is added to a list and handled at the end of the rendering loop, separately.
    public static List<Entity> entitiesToAdd = new ArrayList<>();
    public static List<Entity> entitiesToRemove = new ArrayList<>();
    //This is a dictionary, linking every weapon to its name so that they are easily accessible
    public static Dictionary<String, Weapon> weapons = new Hashtable<>();
    //This is used by the draw method of entities so that all entities can rendered in a single batch draw,
    // rather than in multiple batches
    public SpriteBatch batch;
    private Player player;

    private float spawnCooldown = 5f;
    private float spawnCooldownDecrease = 0.05f;
    private float minSpawnCooldown = 0.5f;
    private float lastSpawnedTime = 0.0f;

    @Override
    // show is called whenever this screen is shown
    // it essentially acts as a constructor for the screen
    public void show() {
        // Initialize the map, spritebatch, and entities array
        map = new Map();
        batch = new SpriteBatch();
        entities = new ArrayList<Entity>();

        //Load a dummy texture for things for which we do not yet have a texture
        Texture noTexture = new Texture("NoTexture.png");

        //Populate the weapons dictionary with weapons
        initializeWeapons(noTexture);

        // create a new orthographic (no 3d perspective) camera, and set its position to the center of the map
        camera = new OrthographicCamera();
        camera.position.set(Map.MAP_WIDTH * Map.TILE_SIZE / 2f, Map.MAP_HEIGHT * Map.TILE_SIZE / 2f, 0);

        // Add the player to the list of entities so he is updated and rendered, with a pistol in his inventory
        // and with his walk animation
        player = new Player(new Texture("player_single_frame.png"), getAnimatedPlayerTexture(), new Vector2(Map.MAP_WIDTH * Map.TILE_SIZE / 2f, Map.MAP_HEIGHT * Map.TILE_SIZE / 2f), new Weapon[]{
            weapons.get("Pistol"),
            null,
            null
        }, camera);
        entities.add(player);
    }

    private Animation<TextureRegion> getAnimatedPlayerTexture(){
        // load the spritesheet as a texture, then make a textureRegion out of that texture.
        TextureRegion playerTexture = new TextureRegion(new Texture("running.png"));
        //The number of sprites in the spritesheet showing each part of the walk animation
        int numFrames = 14;
        //Split the spritesheet into individual textureregions
        TextureRegion[][] playerAnimationTextures2D = playerTexture.split(playerTexture.getRegionWidth(), playerTexture.getRegionHeight()/numFrames);
        //split can only split spritesheets into 2d arrays of textureregions, so convert it to a 1d array
        TextureRegion[] playerAnimationTextures = new TextureRegion[numFrames];
        for (int i = 0; i < 14; i++) {
            playerAnimationTextures[i] = playerAnimationTextures2D[i][0];
        }
        //load all the textureregions into an animation, with a duration of 0.0357 per frame.
        // This sums up to the entire animation being about 0.5 seconds, which appears to work best visually.
        return new Animation<TextureRegion>(0.0357f, playerAnimationTextures);
    }

    private void initializeWeapons(Texture noTexture) {
        //Create each weapon and load it into the weapons dictionary
        Texture bulletTexture = new Texture("bullet.png");
        weapons.put("Pistol", new Weapon(new Texture("pistol-aiming.png"), new Texture("pistol-firing.png"), bulletTexture, 10, 2, 10, 30f, 1f, 0.2f, 5));
        weapons.put("SMG", new Weapon(new Texture("SMG-aiming.png"), new Texture("SMG-firing.png"), bulletTexture, 2, 10, 5, 50f, 0.5f, 0.1f, 5));
        weapons.put("Assault Rifle", new Weapon(noTexture, noTexture, bulletTexture, 4, 5, 15, 10f, 1.5f, 0.3f, 10));
        weapons.put("Sniper Rifle", new Weapon(noTexture, noTexture, bulletTexture, 30, 0.5f, 50, 0.1f, 2f, 0.5f, 15));
    }


    @Override
    // render is called once every frame
    public void render(float v) {
        // Handle input for every entity that requires input
        input();

        // Handle the logic for each entity - this includes movement and attacks for example
        logic();

        // clear the screen to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // recalculate camera projection and view matrices, in case its properties have changed (position, size, etc...)
        camera.update();

        // render the map to the camera
        map.render(camera);

        //draw each entity
        draw();

        handleEnemySpawning();

        //add all entities from the entitiesToAdd list to the main entities list
        //See the initialisation of entitiesToAdd for why this is necessary
        //Then empty the entitiesToAdd list, since everything in it has already been added.
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();

        //remove all entities from the entitiesToRemove list from the main entities list
        //See the initialisation of entitiesToRemove for why this is necessary
        //Then empty the entitiesToRemove list, since everything in it has already been removed.
        entities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();
    }

    public void handleEnemySpawning(){
        if (lastSpawnedTime + spawnCooldown < System.currentTimeMillis()){
            float spawnRadius = 100;
            Random random = new Random();
            Vector2 spawnPos = new Vector2(random.nextFloat(spawnRadius*2)-spawnRadius, random.nextFloat(spawnRadius*2)-spawnRadius);
            spawnPos.nor();
            spawnPos.scl(spawnRadius);
            spawnPos.add(player.pos);
            Entity newEnemy = new Enemy(new Texture("zombie.png"), getAnimatedPlayerTexture(), spawnPos, player);
            entitiesToAdd.add(newEnemy);
            lastSpawnedTime = System.currentTimeMillis();
            if (spawnCooldown > minSpawnCooldown){
                spawnCooldown -= spawnCooldownDecrease;
            }
        }
    }

    public void input() {
        // Iterate through each entity and call their input function to handle their input, passing in the camera
        // The camera is used for things like getting the coordinates of clicks on the screen
        for (Entity e : entities) {
            e.input(camera);
        }
    }

    public void logic() {
        // Iterate through each entity and call their logic function to handle
        // things like moving and attacking
        for (Entity e : entities) {
            e.logic();
        }
    }

    public void draw(){
        // Set the batch's drawing area to what the camera is currently showing
        batch.setProjectionMatrix(camera.combined);
        // Start loading things to render into the batch
        batch.begin();

        // Iterate through each entity and call their draw function to add them to the batch do draw passing in
        // the spritebatch so that all entities can be drawn at once
        for (Entity e : entities) {
            e.draw(batch);
        }
        //Finish loading all entities to be drawn into the spritebatch, so that it can then render them all to the screen
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
        // dispose of the spritebatch
        batch.dispose();
    }
}
