package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.calwe.topdownshooter.Shotgun;
import io.calwe.topdownshooter.Weapon;
import io.calwe.topdownshooter.entities.*;
import io.calwe.topdownshooter.entities.Enemies.ChargingEnemy;
import io.calwe.topdownshooter.entities.Enemies.Enemy;
import io.calwe.topdownshooter.entities.Enemies.ExplodingEnemy;
import io.calwe.topdownshooter.entities.Enemies.RangedEnemy;
import io.calwe.topdownshooter.entities.Equipment.*;

import java.util.*;
import java.util.List;

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
    public static Dictionary<String, Weapon> commonWeapons = new Hashtable<>();
    public static Dictionary<String, Weapon> uncommonWeapons = new Hashtable<>();
    public static Dictionary<String, Weapon> rareWeapons = new Hashtable<>();
    public static Dictionary<String, Weapon> epicWeapons = new Hashtable<>();
    public static Dictionary<String, Weapon> legendaryWeapons = new Hashtable<>();

    public static EquipmentDrop[] equipment;
    //This is used by the draw method of entities so that all entities can rendered in a single batch draw,
    // rather than in multiple batches
    public SpriteBatch batch;
    public static Player player;

    //How long between a zombies spawning in
    private final float spawnCooldown = 5f;
    //Stores the timer used to calculated when spawnCooldown has elapsed
    private float timer = 0f;

    // The player's score, based on how many zombies have been killed and how long the player has been alive
    public static int score;
    //Used to periodically increase the score based on how long the player has been alive
    private float scoreIncreaseTimer = 0f;

    public static int currentTier = 1;

    private FillViewport viewport;

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

        equipment = new EquipmentDrop[]{
            new CritChanceDrop(new Texture("Equipment/RedDotSight.png"), "Red dot sight", "Increases your critical hit chance by 7%.", 7),
            new CritDamageDrop(noTexture, "Armor piercing bullets", "Increases your critical hit damage multiplier by 0.5.", 0.5f),
            new DamageDrop(new Texture("Equipment/ammo.png"), "Hollow points", "Increases your damage by 20%", 0.2f),
            new ExtraInventoryDrop(noTexture, "Backpack", "Allows you to carry an additional weapon."),
            new HealDrop(new Texture("Equipment/medkit.png"), "Medkit", "Restores 25 health.", 25),
            new HealthDrop(new Texture("Equipment/FlakVest.png"), "Kevlar vest", "Increases your current and maximum health by 10.", 10),
            new SaveAmmoDrop(new Texture("Equipment/Magazine.png"), "Extended magazine", "Gives you a 8% chance not to consume ammo when firing a weapon.", 8)
        };

        // create a new orthographic (no 3d perspective) camera, and set its position to the center of the map
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FillViewport(400, 200, camera);
        //camera.position.set(Map.MAP_WIDTH * Map.TILE_SIZE / 2f, Map.MAP_HEIGHT * Map.TILE_SIZE / 2f, 0);

        // Add the player to the list of entities so he is updated and rendered, with a pistol in his inventory
        // and with his walk animation, and a red particle that is released when he is damaged
        player = new Player(
            new Texture("player_single_frame.png"),
            getAnimatedPlayerTexture(),
            new Vector2(Map.MAP_WIDTH * Map.TILE_SIZE / 2f, Map.MAP_HEIGHT * Map.TILE_SIZE / 2f),
            new Weapon[]{
                commonWeapons.get("Pistol").copy(),
                null,
                null
            },
            new Texture[]{
                new Texture("bloodParticle.png")
            },
            camera
        );
        entities.add(player);
        Crate c = new Crate(new Texture("crate.png"), new Vector2(Map.MAP_WIDTH * Map.TILE_SIZE / 2f, Map.MAP_HEIGHT * Map.TILE_SIZE / 2f));
        entities.add(c);
    }

    //Get the humanoid walk animation
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

    //Get the humanoid walk animation
    private Animation<TextureRegion> getAnimatedZombieTexture(){
        // load the spritesheet as a texture, then make a textureRegion out of that texture.
        TextureRegion playerTexture = new TextureRegion(new Texture("zombieRunning.png"));
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
        Sound fireSound = Gdx.audio.newSound(Gdx.files.internal("gunshot.mp3"));
        Sound emptySound = Gdx.audio.newSound(Gdx.files.internal("noAmmo.mp3"));

        commonWeapons.put("Pistol", new Weapon(new Texture("pistol-aiming.png"), new Texture("PistolSideOn.png"), bulletTexture, fireSound, emptySound, 12, 10, 2.5f, 10, 10f, 1f, 1f, 5));
        commonWeapons.put("SMG", new Weapon(new Texture("SMG-aiming.png"), new Texture("SMGSideOn.png"), bulletTexture, fireSound, emptySound, 60, 3, 10, 5, 15f, 0.5f, 0.4f, 5));
        commonWeapons.put("Assault Rifle", new Weapon(new Texture("assaultRifle-aiming.png"), new Texture("AssaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 50,  5, 5, 15, 5f, 1.5f, 0.7f, 10));
        commonWeapons.put("Sniper Rifle", new Weapon(new Texture("sniper-aiming.png"), new Texture("sniperSideOn.png"), bulletTexture, fireSound, emptySound, 7, 40, 0.5f, 50, 0.1f, 2f, 3f, 10));
        commonWeapons.put("Shotgun", new Shotgun(new Texture("shotgun-aiming.png"), new Texture("shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 8, 8, 1, 5, 8, 2, 1, 5));

        uncommonWeapons.put("Pistol", new Weapon(new Texture("pistol-aiming.png"), new Texture("PistolSideOn.png"), bulletTexture, fireSound, emptySound, 12, 13, 2.5f, 10, 10f, 1f, 1f, 5));
        uncommonWeapons.put("SMG", new Weapon(new Texture("SMG-aiming.png"), new Texture("SMGSideOn.png"), bulletTexture, fireSound, emptySound, 60, 4, 10, 5, 15f, 0.5f, 0.4f, 5));
        uncommonWeapons.put("Assault Rifle", new Weapon(new Texture("assaultRifle-aiming.png"), new Texture("AssaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 50,  7, 5, 15, 5f, 1.5f, 0.7f, 10));
        uncommonWeapons.put("Sniper Rifle", new Weapon(new Texture("sniper-aiming.png"), new Texture("sniperSideOn.png"), bulletTexture, fireSound, emptySound, 7, 53, 0.5f, 50, 0.1f, 2f, 3f, 10));
        uncommonWeapons.put("Shotgun", new Shotgun(new Texture("shotgun-aiming.png"), new Texture("shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 8, 11, 1, 5, 8, 2, 1, 5));

        rareWeapons.put("Pistol", new Weapon(new Texture("pistol-aiming.png"), new Texture("PistolSideOn.png"), bulletTexture, fireSound, emptySound, 12, 17, 2.5f, 10, 10f, 1f, 1f, 5));
        rareWeapons.put("SMG", new Weapon(new Texture("SMG-aiming.png"), new Texture("SMGSideOn.png"), bulletTexture, fireSound, emptySound, 60, 5, 10, 5, 15f, 0.5f, 0.4f, 5));
        rareWeapons.put("Assault Rifle", new Weapon(new Texture("assaultRifle-aiming.png"), new Texture("AssaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 50,  8, 5, 15, 5f, 1.5f, 0.7f, 10));
        rareWeapons.put("Sniper Rifle", new Weapon(new Texture("sniper-aiming.png"), new Texture("sniperSideOn.png"), bulletTexture, fireSound, emptySound, 7, 67, 0.5f, 50, 0.1f, 2f, 3f, 10));
        rareWeapons.put("Shotgun", new Shotgun(new Texture("shotgun-aiming.png"), new Texture("shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 8, 13, 1, 5, 8, 2, 1, 5));

        epicWeapons.put("Pistol", new Weapon(new Texture("pistol-aiming.png"), new Texture("PistolSideOn.png"), bulletTexture, fireSound, emptySound, 12, 20, 2.5f, 10, 10f, 1f, 1f, 5));
        epicWeapons.put("SMG", new Weapon(new Texture("SMG-aiming.png"), new Texture("SMGSideOn.png"), bulletTexture, fireSound, emptySound, 60, 6, 10, 5, 15f, 0.5f, 0.4f, 5));
        epicWeapons.put("Assault Rifle", new Weapon(new Texture("assaultRifle-aiming.png"), new Texture("AssaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 50,  10, 5, 15, 5f, 1.5f, 0.7f, 10));
        epicWeapons.put("Sniper Rifle", new Weapon(new Texture("sniper-aiming.png"), new Texture("sniperSideOn.png"), bulletTexture, fireSound, emptySound, 7, 80, 0.5f, 50, 0.1f, 2f, 3f, 10));
        epicWeapons.put("Shotgun", new Shotgun(new Texture("shotgun-aiming.png"), new Texture("shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 8, 16, 1, 5, 8, 2, 1, 5));

        legendaryWeapons.put("Pistol", new Weapon(new Texture("pistol-aiming.png"), new Texture("PistolSideOn.png"), bulletTexture, fireSound, emptySound, 12, 23, 2.5f, 10, 10f, 1f, 1f, 5));
        legendaryWeapons.put("SMG", new Weapon(new Texture("SMG-aiming.png"), new Texture("SMGSideOn.png"), bulletTexture, fireSound, emptySound, 60, 7, 10, 5, 15f, 0.5f, 0.4f, 5));
        legendaryWeapons.put("Assault Rifle", new Weapon(new Texture("assaultRifle-aiming.png"), new Texture("AssaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 50,  12, 5, 15, 5f, 1.5f, 0.7f, 10));
        legendaryWeapons.put("Sniper Rifle", new Weapon(new Texture("sniper-aiming.png"), new Texture("sniperSideOn.png"), bulletTexture, fireSound, emptySound, 7, 93, 0.5f, 50, 0.1f, 2f, 3f, 10));
        legendaryWeapons.put("Shotgun", new Shotgun(new Texture("shotgun-aiming.png"), new Texture("shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 8, 19, 1, 5, 8, 2, 1, 5));
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

        //draw each entity
        draw();

        //check if new enemies need to be spawned in
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

        //periodically increase the score
        if (scoreIncreaseTimer > 1){
            score += 10;
            scoreIncreaseTimer = 0;
        }
        scoreIncreaseTimer += Gdx.graphics.getDeltaTime();

        //sort the entities in the entities by layer, so that entities with a lower layer will be rendered under
        // entities with a higher layer
        entities.sort(new Comparator<Entity>() {
            public int compare(Entity entity1, Entity entity2) {
                if (entity1.layer > entity2.layer) return 1;
                if (entity1.layer < entity2.layer) return -1;
                return 0;
            }
        });

        if (score > 1000){
            currentTier = 5;
        }
        else if (score > 750){
            currentTier = 4;
        }
        else if (score > 500){
            currentTier = 3;
        }
        else if (score > 250){
            currentTier = 2;
        }

    }

    public void handleEnemySpawning(){
        //Check if the cooldown to spawn a new enemy has elapsed
        if (timer >= spawnCooldown-(currentTier*0.5f)){
            //how far away from a player zombies should spawn
            float spawnRadius = 100;
            Random random = new Random();
            //Find a new random direction
            Vector2 spawnPos = new Vector2(random.nextFloat(spawnRadius*2)-spawnRadius, random.nextFloat(spawnRadius*2)-spawnRadius);
            spawnPos.nor();
            //Multiply the direction by the radius and add the player's position to spawn the zombie 100 away
            // from the player in a random direction
            spawnPos.scl(spawnRadius);
            spawnPos.add(player.pos);
            // Create a new enemy, with the appropriate textures and sounds, and spawn him at the generated position
            Entity newEnemy;
            int enemyChoice = random.nextInt(100);
            if (enemyChoice >= 85){
                newEnemy = new ExplodingEnemy(
                    new Texture("Enemies/blueZombie.png"),
                    getAnimatedZombieTexture(),
                    Gdx.audio.newSound(Gdx.files.internal("Enemies/zombieHit.mp3")),
                    spawnPos,
                    player,
                    new Texture[]{
                        new Texture("bloodParticle.png"),
                        new Texture("Enemies/zombieParticle.png")
                    },
                    new Texture("Enemies/zombieProjectile.png"),
                    1.5f
                );
            }
            else if (enemyChoice >= 70){
                newEnemy = new ChargingEnemy(
                    new Texture("Enemies/redZombie.png"),
                    getAnimatedZombieTexture(),
                    Gdx.audio.newSound(Gdx.files.internal("Enemies/zombieHit.mp3")),
                    spawnPos,
                    player,
                    new Texture[]{
                        new Texture("bloodParticle.png"),
                        new Texture("Enemies/zombieParticle.png")
                    },
                    new Texture("Enemies/ChargingZombieLockingOn.png"),
                    4,
                    0.5f,
                    40,
                    0.7f
                );
            }
            else if (enemyChoice >= 55){
                newEnemy = new RangedEnemy(
                    new Texture("Enemies/greyZombie.png"),
                    getAnimatedZombieTexture(),
                    Gdx.audio.newSound(Gdx.files.internal("Enemies/zombieHit.mp3")),
                    spawnPos,
                    player,
                    new Texture[]{
                        new Texture("bloodParticle.png"),
                        new Texture("Enemies/zombieParticle.png")
                    },
                    2,
                    2f,
                    new Texture("Enemies/zombieProjectile.png")
                );
            }
            else{
                newEnemy = new Enemy(
                    new Texture("Enemies/orangeZombie.png"),
                    getAnimatedZombieTexture(),
                    Gdx.audio.newSound(Gdx.files.internal("Enemies/zombieHit.mp3")),
                    spawnPos,
                    player,
                    new Texture[]{
                        new Texture("bloodParticle.png"),
                        new Texture("Enemies/zombieParticle.png")
                    }
                );
            }

            //Add him to the entities list so he will be updated
            entitiesToAdd.add(newEnemy);
            //reset the cooldown for spawning enemies
            timer = 0;
        }
        //increment the timer
        timer += Gdx.graphics.getDeltaTime();
    }

    public void input() {
        // Iterate through each entity and call their input function to handle their input, passing in the camera
        // The camera is used for things like getting the coordinates of clicks on the screen
        for (Entity e : entities) {
            e.input(camera);
        }
    }

    //Get the collision bounds for every entity other than the one given, so an entity can check for collisions
    // with all other entities
    public static Dictionary<Rectangle, Entity> getOtherColliderRects(Entity entity) {
        //Create a new dictionary of entities and their bounds
        Dictionary<Rectangle, Entity> collideableRects = new Hashtable<>();
        //Loop through each entity and add them and their bounds to the dictionary
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e != entity){
                collideableRects.put(e.bounds, e);
            }
        }
        return collideableRects;
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
        map.renderWorld(batch);
        // Iterate through each entity and call their draw function to add them to the batch do draw passing in
        // the spritebatch so that all entities can be drawn at once
        for (Entity e : entities) {
            e.draw(batch);
        }
        //Finish loading all entities to be drawn into the spritebatch, so that it can then render them all to the screen
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        // dispose of the spritebatch
        batch.dispose();
    }
}
