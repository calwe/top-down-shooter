package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import io.calwe.topdownshooter.Game;
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

    private OrthographicCamera camera;
    private FillViewport viewport;

    private Map map;

    // A master list of every moving object in the world, so that they can all be updated and rendered easily
    public static final List<Entity> entities = new ArrayList<>();
    //Separate lists are required for adding and removing entities, as entities creating or destroying other entities
    //is generally handled in entities' logic function, which is run by a function iterating through each entity
    //and running their logic. Adding or removing from a list that is being iterated through can cause problems,
    //so entity creation and destruction is added to a list and handled at the end of the rendering loop, separately.
    public static final List<Entity> entitiesToAdd = new ArrayList<>();
    public static final List<Entity> entitiesToRemove = new ArrayList<>();
    //This is a dictionary, linking every weapon to its name so that they are easily accessible
    public static final Dictionary<String, Weapon> commonWeapons = new Hashtable<>();
    public static final Dictionary<String, Weapon> uncommonWeapons = new Hashtable<>();
    public static final Dictionary<String, Weapon> rareWeapons = new Hashtable<>();
    public static final Dictionary<String, Weapon> epicWeapons = new Hashtable<>();
    public static final Dictionary<String, Weapon> legendaryWeapons = new Hashtable<>();

    //List of all the places world entites have already been generated
    private final List<Vector2> entitysAlreadyGeneratedCoords = new ArrayList<>();

    //All the equipment drop options
    public static EquipmentDrop[] equipment;
    //This is used by the draw method of entities so that all entities can rendered in a single batch draw,
    // rather than in multiple batches
    public final SpriteBatch batch = new SpriteBatch();

    //The player
    public static Player player;

    //How long between a zombies spawning in
    private final float spawnCooldown = 5f;
    //Stores the timer used to calculated when spawnCooldown has elapsed
    private float timer = 0f;

    //Used to periodically increase the score based on how long the player has been alive
    private float scoreIncreaseTimer = 0f;

    //The current tier of play, which determines the power of weapon drops and enemies
    public static int currentTier = 1;

    public static Game game;

    //The enemy cap at each tier
    private final int[] enemyCapAtTier = new int[]{
        3,
        4,
        5,
        6,
        7,
    };

    //Whether the game is paused
    private boolean paused = false;

    //All the textures
    static Texture blueZombieTexture;
    static Texture redZombieTexture;
    static Texture orangeZombieTexture;
    static Texture greyZombieTexture;
    static Texture zombieProjectileTexture;
    static Texture chargingZombieLockingOnTexture;

    static Animation<TextureRegion> animatedZombieTexture;

    static Texture[] zombieParticles;

    //The sounds
    static Sound zombieHurtSound;
    static Sound backgroundMusic;


    @Override
    // show is called whenever this screen is shown
    // it essentially acts as a constructor for the screen
    public void show() {
        // Initialize the map, spritebatch, and entities array
        map = new Map();
        //Load the textures and sounds
        loadTextures();
        zombieHurtSound = Gdx.audio.newSound(Gdx.files.internal("Enemies/zombieHit.mp3"));

        //Populate the weapons dictionary with weapons
        initializeWeapons();

        //Load the equipment drops
        equipment = new EquipmentDrop[]{
            new CritChanceDrop(new Texture("Equipment/redDotSight.png"), "Red dot sight", "Crit chance +7%.", 7),
            new CritDamageDrop(new Texture("Equipment/APRounds.png"), "Armor piercing bullets", "Crit damage +25%", 0.5f),
            new DamageDrop(new Texture("Equipment/ammo.png"), "Hollow points", "Damage +20%", 0.2f),
            new HealDrop(new Texture("Equipment/medkit.png"), "Medkit", "Heal 25%", 25),
            new HealthDrop(new Texture("Equipment/flakVest.png"), "Kevlar vest", "Max health +10%", 10),
            new SaveAmmoDrop(new Texture("Equipment/magazine.png"), "Extended magazine", "8% chance to save ammo.", 8),
            new BombDrop(new Texture("Equipment/bomb.png"), "Bomb", "Clear all enemies on the screen. ")
        };

        // create a new orthographic (no 3d perspective) camera, and set its position to the center of the map
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //Create a viewport
        viewport = new FillViewport(400, 200, camera);

        // Add the player to the list of entities so he is updated and rendered, with a pistol in his inventory
        // and with his walk animation, and a red particle that is released when he is damaged
        player = new Player(
            new Texture("player_single_frame.png"),
            getAnimatedPlayerTexture(),
            new Vector2(0, 0),
            new Weapon[3],
            new Texture[]{
                new Texture("bloodParticle.png")
            },
            camera
        );
        player.addToInventory(commonWeapons.get("Pistol").copy());
        //Add the player to the list of entities
        entities.add(player);

        //Start the background and loop it
        backgroundMusic = Gdx.audio.newSound(Gdx.files.internal("backgroundMusic.mp3"));
        long id = backgroundMusic.play(0.1f);
        backgroundMusic.setLooping(id, true);
    }

    private void loadTextures() {
        blueZombieTexture = new Texture("Enemies/blueZombie.png");
        redZombieTexture = new Texture("Enemies/redZombie.png");
        orangeZombieTexture = new Texture("Enemies/orangeZombie.png");
        greyZombieTexture = new Texture("Enemies/greyZombie.png");
        zombieProjectileTexture = new Texture("Enemies/zombieProjectile.png");
        chargingZombieLockingOnTexture = new Texture("Enemies/chargingZombieLockingOn.png");
        zombieParticles = new Texture[]{
            new Texture("bloodParticle.png"),
            new Texture("Enemies/zombieParticle.png")
        };
        animatedZombieTexture = getAnimatedZombieTexture();
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
        for (int i = 0; i < numFrames; i++) {
            playerAnimationTextures[i] = playerAnimationTextures2D[i][0];
        }
        //load all the textureregions into an animation, with a duration of 0.0357 per frame.
        // This sums up to the entire animation being about 0.5 seconds, which appears to work best visually.
        return new Animation<TextureRegion>(0.0357f, playerAnimationTextures);
    }

    //Get the zombie walk animation
    private Animation<TextureRegion> getAnimatedZombieTexture(){
        // load the spritesheet as a texture, then make a textureRegion out of that texture.
        TextureRegion zombieTexture = new TextureRegion(new Texture("Enemies/zombieRunning.png"));
        //The number of sprites in the spritesheet showing each part of the walk animation
        int numFrames = 14;
        //Split the spritesheet into individual textureregions
        TextureRegion[][] zombieAnimationTextures2D = zombieTexture.split(zombieTexture.getRegionWidth(), zombieTexture.getRegionHeight()/numFrames);
        //split can only split spritesheets into 2d arrays of textureregions, so convert it to a 1d array
        TextureRegion[] zombieAnimationTextures = new TextureRegion[numFrames];
        for (int i = 0; i < numFrames; i++) {
            zombieAnimationTextures[i] = zombieAnimationTextures2D[i][0];
        }
        //load all the textureregions into an animation, with a duration of 0.0357 per frame.
        // This sums up to the entire animation being about 0.5 seconds, which appears to work best visually.
        return new Animation<TextureRegion>(0.0357f, zombieAnimationTextures);
    }

    //Create each weapon and load it into the weapons dictionary
    private void initializeWeapons() {
        //Load the bullet texture all weapons use
        Texture bulletTexture = new Texture("Weapons/bullet.png");
        //Load the sounds the weapons use
        Sound fireSound = Gdx.audio.newSound(Gdx.files.internal("Weapons/gunshot.mp3"));
        Sound emptySound = Gdx.audio.newSound(Gdx.files.internal("Weapons/noAmmo.mp3"));

        //Initialise the common weapons
        commonWeapons.put("Pistol", new Weapon(new Texture("Weapons/pistolAiming.png"), new Texture("Weapons/pistolSideOn.png"), bulletTexture, fireSound, emptySound, 18, 10, 2.5f, 10, 10f, 1f, 1f, 5));
        commonWeapons.put("SMG", new Weapon(new Texture("Weapons/SMGAiming.png"), new Texture("Weapons/SMGSideOn.png"), bulletTexture, fireSound, emptySound, 90, 3, 10, 5, 15f, 0.5f, 0.4f, 5));
        commonWeapons.put("Assault Rifle", new Weapon(new Texture("Weapons/assaultRifleAiming.png"), new Texture("Weapons/assaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 60,  5, 5, 15, 5f, 1.5f, 0.7f, 7));
        commonWeapons.put("Sniper Rifle", new Weapon(new Texture("Weapons/sniperAiming.png"), new Texture("Weapons/sniperSideOn.png"), bulletTexture, fireSound, emptySound, 8, 22, 0.75f, 75, 0.1f, 2f, 3f, 7, true));
        commonWeapons.put("Shotgun", new Shotgun(new Texture("Weapons/shotgunAiming.png"), new Texture("Weapons/shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 10, 6, 1, 5, 10, 2, 1, 5));

        //Initialise the uncommon weapons
        uncommonWeapons.put("Pistol", new Weapon(new Texture("Weapons/pistolAiming.png"), new Texture("Weapons/pistolSideOn.png"), bulletTexture, fireSound, emptySound, 18, 13, 2.5f, 10, 10f, 1f, 1f, 5));
        uncommonWeapons.put("SMG", new Weapon(new Texture("Weapons/SMGAiming.png"), new Texture("Weapons/SMGSideOn.png"), bulletTexture, fireSound, emptySound, 90, 4, 10, 5, 15f, 0.5f, 0.4f, 5));
        uncommonWeapons.put("Assault Rifle", new Weapon(new Texture("Weapons/assaultRifleAiming.png"), new Texture("Weapons/assaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 60,  7, 5, 15, 5f, 1.5f, 0.7f, 7));
        uncommonWeapons.put("Sniper Rifle", new Weapon(new Texture("Weapons/sniperAiming.png"), new Texture("Weapons/sniperSideOn.png"), bulletTexture, fireSound, emptySound, 8, 29, 0.75f, 75, 0.1f, 2f, 3f, 7, true));
        uncommonWeapons.put("Shotgun", new Shotgun(new Texture("Weapons/shotgunAiming.png"), new Texture("Weapons/shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 10, 8, 1, 5, 10, 2, 1, 5));

        //Initialise the rare weapons
        rareWeapons.put("Pistol", new Weapon(new Texture("Weapons/pistolAiming.png"), new Texture("Weapons/pistolSideOn.png"), bulletTexture, fireSound, emptySound, 18, 17, 2.5f, 10, 10f, 1f, 1f, 5));
        rareWeapons.put("SMG", new Weapon(new Texture("Weapons/SMGAiming.png"), new Texture("Weapons/SMGSideOn.png"), bulletTexture, fireSound, emptySound, 90, 5, 10, 5, 15f, 0.5f, 0.4f, 5));
        rareWeapons.put("Assault Rifle", new Weapon(new Texture("Weapons/assaultRifleAiming.png"), new Texture("Weapons/assaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 60,  8, 5, 15, 5f, 1.5f, 0.7f, 7));
        rareWeapons.put("Sniper Rifle", new Weapon(new Texture("Weapons/sniperAiming.png"), new Texture("Weapons/sniperSideOn.png"), bulletTexture, fireSound, emptySound, 8, 37, 0.75f, 75, 0.1f, 2f, 3f, 7, true));
        rareWeapons.put("Shotgun", new Shotgun(new Texture("Weapons/shotgunAiming.png"), new Texture("Weapons/shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 10, 10, 1, 5, 10, 2, 1, 5));

        //Initialise the epic weapons
        epicWeapons.put("Pistol", new Weapon(new Texture("Weapons/pistolAiming.png"), new Texture("Weapons/pistolSideOn.png"), bulletTexture, fireSound, emptySound, 18, 20, 2.5f, 10, 10f, 1f, 1f, 5));
        epicWeapons.put("SMG", new Weapon(new Texture("Weapons/SMGAiming.png"), new Texture("Weapons/SMGSideOn.png"), bulletTexture, fireSound, emptySound, 90, 6, 10, 5, 15f, 0.5f, 0.4f, 5));
        epicWeapons.put("Assault Rifle", new Weapon(new Texture("Weapons/assaultRifleAiming.png"), new Texture("Weapons/assaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 60,  10, 5, 15, 5f, 1.5f, 0.7f, 7));
        epicWeapons.put("Sniper Rifle", new Weapon(new Texture("Weapons/sniperAiming.png"), new Texture("Weapons/sniperSideOn.png"), bulletTexture, fireSound, emptySound, 8, 44, 0.75f, 75, 0.1f, 2f, 3f, 7, true));
        epicWeapons.put("Shotgun", new Shotgun(new Texture("Weapons/shotgunAiming.png"), new Texture("Weapons/shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 10, 12, 1, 5, 10, 2, 1, 5));

        //Initialise the legendary weapons
        legendaryWeapons.put("Pistol", new Weapon(new Texture("Weapons/pistolAiming.png"), new Texture("Weapons/pistolSideOn.png"), bulletTexture, fireSound, emptySound, 18, 23, 2.5f, 10, 10f, 1f, 1f, 5));
        legendaryWeapons.put("SMG", new Weapon(new Texture("Weapons/SMGAiming.png"), new Texture("Weapons/SMGSideOn.png"), bulletTexture, fireSound, emptySound, 90, 7, 10, 5, 15f, 0.5f, 0.4f, 5));
        legendaryWeapons.put("Assault Rifle", new Weapon(new Texture("Weapons/assaultRifleAiming.png"), new Texture("Weapons/assaultRifleSideOn.png"), bulletTexture, fireSound, emptySound, 60,  12, 5, 15, 5f, 1.5f, 0.7f, 7));
        legendaryWeapons.put("Sniper Rifle", new Weapon(new Texture("Weapons/sniperAiming.png"), new Texture("Weapons/sniperSideOn.png"), bulletTexture, fireSound, emptySound, 8, 51, 0.75f, 75, 0.1f, 2f, 3f, 7, true));
        legendaryWeapons.put("Shotgun", new Shotgun(new Texture("Weapons/shotgunAiming.png"), new Texture("Weapons/shotgunSideOn.png"), bulletTexture, fireSound, emptySound, 10, 14, 1, 5, 10, 2, 1, 5));
    }


    @Override
    // render is called once every frame
    public void render(float v) {
        try{
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
                paused = !paused;
            }
            // Handle input for every entity that requires input
            input();
            //Carry out the logic and spawning only if we aren't paused
            if (!paused){
                // Handle the logic for each entity - this includes movement and attacks for example
                logic();

                //check if new enemies need to be spawned in
                handleEnemySpawning();
            }
            // clear the screen to black
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // recalculate camera projection and view matrices, in case its properties have changed (position, size, etc...)
            camera.update();

            //draw each entity
            draw();

            player.hud.render();

            //add all entities from the entitiesToAdd list to the main entities list
            //See the initialisation of entitiesToAdd for why this is necessary
            //Then empty the entitiesToAdd list, since everything in it has already been added.
            entities.addAll(entitiesToAdd);
            entitiesToAdd.clear();

            //remove all entities from the entitiesToRemove list from the main entities list
            //See the initialisation of entitiesToRemove for why this is necessary
            //Then empty the entitiesToRemove list, since everything in it has already been removed.
            for (Entity e : entitiesToRemove) {
                entities.remove(e);
            }
            entitiesToRemove.clear();

            //If we aren't paused
            if (!paused){
                //periodically increase the score every second
                if (scoreIncreaseTimer > 1){
                    player.score += 10;
                    scoreIncreaseTimer = 0;
                }
                scoreIncreaseTimer += Gdx.graphics.getDeltaTime();
            }

            arrangeEntitiesByLayer();

            updateCurrentTier();

        }
        //Log any errors
        catch (Exception error){
            error.printStackTrace();
        }
    }

    // Iterate through all entities and count the number of enemy entities that exist
    int getNumberOfEnemies(){
        int numEnemies = 0;
        for (Entity e : entities) {
            if (e instanceof Enemy){
                numEnemies++;
            }
        }
        return numEnemies;
    }

    void updateCurrentTier(){
        //Update the current tier in case the score has gone over the boundary into the next tier
        if (player.score > 3500){
            currentTier = 5;
        }
        else if (player.score > 2000){
            currentTier = 4;
        }
        else if (player.score > 1000){
            currentTier = 3;
        }
        else if (player.score > 500){
            currentTier = 2;
        }
    }

    void arrangeEntitiesByLayer(){
        //sort the entities in the entities by layer, so that entities with a lower layer will be rendered under
        // entities with a higher layer
        entities.sort(new Comparator<Entity>() {
            public int compare(Entity entity1, Entity entity2) {
                if (entity1.layer > entity2.layer) return 1;
                if (entity1.layer < entity2.layer) return -1;
                return 0;
            }
        });
    }

    public void handleEnemySpawning(){
        //Check if the cooldown to spawn a new enemy has elapsed
        if (timer >= spawnCooldown-(currentTier*0.5f)){
            if (getNumberOfEnemies() < enemyCapAtTier[currentTier-1]){
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
                //15% chance to spawn a blue exploding zombie
                if (enemyChoice >= 85){
                    newEnemy = new ExplodingEnemy(
                        blueZombieTexture,
                        animatedZombieTexture,
                        zombieHurtSound,
                        spawnPos,
                        player,
                        zombieParticles,
                        zombieProjectileTexture
                    );
                }
                //15% chance to spawn a red charging zombie
                else if (enemyChoice >= 70){
                    newEnemy = new ChargingEnemy(
                        redZombieTexture,
                        animatedZombieTexture,
                        zombieHurtSound,
                        spawnPos,
                        player,
                        zombieParticles,
                        chargingZombieLockingOnTexture
                    );
                }
                //15% chance to spawn a grey ranged zombie
                else if (enemyChoice >= 55){
                    newEnemy = new RangedEnemy(
                        greyZombieTexture,
                        animatedZombieTexture,
                        zombieHurtSound,
                        spawnPos,
                        player,
                        zombieParticles,
                        zombieProjectileTexture
                    );
                }
                //55% to spawn an orange normal zombie
                else{
                    newEnemy = new Enemy(
                        orangeZombieTexture,
                        animatedZombieTexture,
                        zombieHurtSound,
                        spawnPos,
                        player,
                        zombieParticles
                    );
                }

                //Add him to the entities list so he will be updated
                entitiesToAdd.add(newEnemy);
            }
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
            if (e.pos.dst(player.pos) < 300f) {
                e.logic();
            }
        }
    }

    public void draw(){
        // Set the batch's drawing area to what the camera is currently showing
        batch.setProjectionMatrix(camera.combined);
        // Start loading things to render into the batch
        batch.begin();
        map.renderWorld(batch, entitysAlreadyGeneratedCoords);
        // Iterate through each entity and call their draw function to add them to the batch do draw passing in
        // the spritebatch so that all entities can be drawn at once
        for (Entity e : entities) {
            if (e.pos.dst(player.pos) < 300f){
                e.draw(batch);
            }
        }
        //Finish loading all entities to be drawn into the spritebatch, so that it can then render them all to the screen
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        //Resize the viewport
        viewport.update(width, height);
	    player.resize(width, height);
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
        // dispose of the spritebatch and all the textures and sounds
        batch.dispose();
        player.dispose();
        backgroundMusic.dispose();
        blueZombieTexture.dispose();
        redZombieTexture.dispose();
        orangeZombieTexture.dispose();
        greyZombieTexture.dispose();
        zombieProjectileTexture.dispose();
        chargingZombieLockingOnTexture.dispose();
        zombieHurtSound.dispose();
        map.dispose();
    }
}
