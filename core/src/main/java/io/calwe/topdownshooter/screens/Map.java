package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Crate;
import io.calwe.topdownshooter.entities.Landmine;
import io.calwe.topdownshooter.entities.Obstacle;
import io.calwe.topdownshooter.entities.WorldFeature;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Map {
    // the tile set texture contains each possible tile for the map in a stitched together column
    // assets in libGDX are stored in the assets/ folder
    public static final String TILE_SET_TEXTURE = "map_tileset.png";
    // width/height of each tile in pixels. each tile must be square
    public static final int tileSize = 12;
    // number of tiles in the texture
    public static final int tileCount = 5;


    // size of the map in tiles
    public static final int MAP_WIDTH = 64;
    public static final int MAP_HEIGHT = 64;

    // libGDX contains a few different tile map renderers, such as isometric or hexagonal
    // for a top-down game, the orthogonal map renderer is suitable
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap map;

    private final long initialTime;

    private final int viewDistance;

    static Texture crateTexture;
    static Texture treeTexture;
    static Texture treeTransparentTexture;
    static Texture trunkTexture;
    static Texture houseTexture;
    static Texture houseTransparentTexture;
    static Texture wallTexture;
    static Texture floorTexture;
    static Texture landmineTexture;
    public static Animation<TextureRegion> explosionAnimation;
    static Texture[] carTextures;

    static TiledMapTileSet tileSet;


    public Map() {
        //The time the map was first loaded
        initialTime = System.currentTimeMillis();
        //The distance away map tiles are loaded from the player
        viewDistance = 20;

        //Load all of the textures
        treeTexture = new Texture("World/TreeTop.png");
        treeTransparentTexture = new Texture("World/TreeTopTransparent.png");
        trunkTexture = new Texture("World/TreeTrunk.png");
        crateTexture = new Texture("crate.png");
        carTextures = new Texture[]{
            new Texture("World/Cars/BlackCar.png"),
            new Texture("World/Cars/BlueCar.png"),
            new Texture("World/Cars/GrayCar.png"),
            new Texture("World/Cars/GreenCar.png"),
            new Texture("World/Cars/RedCar.png"),
            new Texture("World/Cars/WhiteCar.png"),
        };
        houseTexture = new Texture("World/roof.png");
        houseTransparentTexture = new Texture("World/roofTransparent.png");
        wallTexture = new Texture("World/Wall.png");
        floorTexture = new Texture("World/Floor.png");
        landmineTexture = new Texture("World/landmine.png");
        explosionAnimation = getLandmineExplosionAnimation();

        //Create the grass tileset
        Texture tilesetTexture = new Texture(Gdx.files.internal("map_tileset.png"));
        // create a new empty tile set
        tileSet = new TiledMapTileSet();
        // loop through each square region in the texture, and add this to the tile set
        for (int i = 0; i < 5; i++) {
            // get the correct region
            TextureRegion region = new TextureRegion(tilesetTexture, 0, i * tileSize, tileSize, tileSize);
            // turn the region into a tile
            TiledMapTile tile = new StaticTiledMapTile(region);
            // set the id of the tile to i+1, as tile IDs must start at 1
            tile.setId(i + 1);
            // put the tile into the tileSet, at the correct position
            tileSet.putTile(i + 1, tile);
        }
    }

    //Get a random value based on coordinates and the starttime as the seed - so that parts of the world remain
    // consistent each time they are generated
    float seededRandomLocationValue(int x,int y) {
        //Convert all positive numbers into even numbers and all negative numbers into odd numbers, because odd numbers
        // don't work with my system, so we need all numbers to be even but different
        if (x >= 0){
            x *= 2;
        }
        else{
            x = (Math.abs(x) * 2) + 1;
        }
        if (y >= 0){
            x *= 2;
        }
        else{
            y = (Math.abs(y) * 2) + 1;
        }
        //Build the seed for the random out of two parts - the first 6 characters of the seed are from the x coordinate,
        // the second 6 the y coordinate, both padded with zeroes to make them six characters long
        StringBuilder xString = new StringBuilder(String.valueOf(x));
        StringBuilder yString = new StringBuilder(String.valueOf(y));
        while (xString.length() < 6) {
            xString.insert(0, "0");
        }
        while (yString.length() < 6) {
            yString.insert(0, "0");
        }
        //Generate a new random using the seed and the start time, then get a new random float using it.
        Random r = new Random((Long.parseLong(xString + yString.toString())) * initialTime);
        return r.nextFloat();
    }

    //Generate a crate at the provided coords if nothing has already been generated there
    public void tryToGenerateCrate(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords) {
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            Crate c = new Crate(crateTexture, new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
            Play.entitiesToAdd.add(c);
            //Add the current coords to the list of coords things have already been generated at
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    //Generate a car of a random color  at the provided coords if nothing has already been generated there
    public void tryToGenerateCar(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords) {
        Random rand = new Random();
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            Obstacle car = new Obstacle(carTextures[rand.nextInt(carTextures.length)], new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)), 1, 26, 43);
            Play.entitiesToAdd.add(car);
            //Add the current coords to the list of coords things have already been generated at
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    //Generate a tree at the provided coords if nothing has already been generated there
    public void tryToGenerateTree(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords) {
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            WorldFeature tree = new WorldFeature(
                treeTexture,
                treeTransparentTexture,
                new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)),
                1,
                36,
                36,
                new Obstacle[]{
                    new Obstacle(trunkTexture, new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)), 1, 12, 12)
                }
                );
            Play.entitiesToAdd.add(tree);
            //Add the current coords to the list of coords things have already been generated at
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    //Generate a house at the provided coords if nothing has already been generated there
    public void tryToGenerateHouse(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords) {
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            tryToGenerateCrate(x, y,entitiesAlreadyGeneratedCoords);
            WorldFeature house = new WorldFeature(
                houseTexture,
                houseTransparentTexture,
                new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)),
                1,
                60,
                96,
                new Obstacle[]{
                    //Left wall
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+45), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+42), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+36), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+30), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+24), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+18), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+12), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+6), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+0), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-6), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-12), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-18), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-24), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-30), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-36), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-42), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-45), 1, 6, 6),

                    //Right wall
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+45), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+42), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+36), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+30), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+24), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+18), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+12), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+6), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+0), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-6), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-12), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-18), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-24), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-30), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-36), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-42), 1, 6, 6),
                    new Obstacle(wallTexture, new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-45), 1, 6, 6),

                    //Floor
                    new Obstacle(floorTexture, new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)), 1, 60, 96, false, -5)
                }
            );
            Play.entitiesToAdd.add(house);
            //Add the current coords to the list of coords things have already been generated at
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    //Get the landmine explosion animation
    private Animation<TextureRegion> getLandmineExplosionAnimation(){
        // load the spritesheet as a texture, then make a textureRegion out of that texture.
        TextureRegion texture = new TextureRegion(new Texture("World/explosionAnimation.png"));
        //The number of sprites in the spritesheet showing each part of the animation
        int numFrames = 4;
        //Split the spritesheet into individual textureregions
        TextureRegion[][] AnimationTextures2D = texture.split(texture.getRegionWidth(), texture.getRegionHeight()/numFrames);
        //split can only split spritesheets into 2d arrays of textureregions, so convert it to a 1d array
        TextureRegion[] AnimationTextures = new TextureRegion[numFrames];
        for (int i = 0; i < numFrames; i++) {
            AnimationTextures[i] = AnimationTextures2D[i][0];
        }
        //load all the textureregions into an animation, with a duration of 0.0357 per frame.
        // This sums up to the entire animation being about 0.5 seconds, which appears to work best visually.
        return new Animation<TextureRegion>(0.0357f, AnimationTextures);
    }

    //Generate a landmine at the provided coords if nothing has already been generated there
    public void tryToGenerateLandmine(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords){
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            Landmine landmine = new Landmine(landmineTexture, new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)), explosionAnimation, 10);
            Play.entitiesToAdd.add(landmine);
            //Add the current coords to the list of coords things have already been generated at
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    public void renderWorld(SpriteBatch batch, List<Vector2> entitiesAlreadyGeneratedCoords) {
        // Iterate through the tiles around the player, and load them and randomly generate crates, trees, landmines, cars and houses
        for (int x = Math.round(Play.player.pos.x/tileSize)-viewDistance; x < Math.round(Play.player.pos.x/tileSize) + viewDistance; x++){
            for (int y = Math.round(Play.player.pos.y/tileSize)-viewDistance; y < Math.round(Play.player.pos.y/tileSize) + viewDistance; y++){
                //Generate a random tile based the coordinates
                int tileId = Math.round(seededRandomLocationValue(x, y)*(tileCount-1)) + 1;
                batch.draw(tileSet.getTile(tileId).getTextureRegion(), x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f));

                //Randomly generate features
                //3/2000 chance to generate a crate
                if (Math.ceil(seededRandomLocationValue(y, x) * 2000) >= 1998){
                    tryToGenerateCrate(x, y,entitiesAlreadyGeneratedCoords);
                }
                //1/250 chance to generate a tree
                else if (Math.ceil(seededRandomLocationValue(y+100, x+100) * 1000) >= 997){
                    tryToGenerateTree(x, y, entitiesAlreadyGeneratedCoords);
                }
                //1/2000 chance to generate a car
                else if (Math.ceil(seededRandomLocationValue(y+200, x+200) * 2000) == 999){
                    tryToGenerateCar(x, y, entitiesAlreadyGeneratedCoords);
                }
                //1/2000 chance to generate a house
                else if (Math.ceil(seededRandomLocationValue(y+300, x+300) * 2000) == 999){
                    tryToGenerateHouse(x, y, entitiesAlreadyGeneratedCoords);
                }
                // 3/1000 chance to generate a landmine
                else if (Math.ceil(seededRandomLocationValue(y+400, x+400) * 1000) >= 998){
                    tryToGenerateLandmine(x, y, entitiesAlreadyGeneratedCoords);
                }
            }
        }
    }

    public void dispose(){
        //dispose of all the loaded textures
        treeTexture.dispose();
        treeTransparentTexture.dispose();
        trunkTexture.dispose();
        crateTexture.dispose();
        houseTexture.dispose();
        houseTransparentTexture.dispose();
        wallTexture.dispose();
        floorTexture.dispose();
        landmineTexture.dispose();
    }
}
