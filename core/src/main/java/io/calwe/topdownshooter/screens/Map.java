package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
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
import io.calwe.topdownshooter.entities.Crate;
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


    public Map() {
        initialTime = System.currentTimeMillis();
        viewDistance = 20;
    }

    float seededRandomLocationValue(int x,int y) {
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
        StringBuilder xString = new StringBuilder(String.valueOf(x));
        StringBuilder yString = new StringBuilder(String.valueOf(y));
        while (xString.length() < 6) {
            xString.insert(0, "0");
        }
        while (yString.length() < 6) {
            yString.insert(0, "0");
        }
        Random r = new Random((Long.parseLong(xString + yString.toString())) * initialTime);
        return r.nextFloat();
    }

    public void tryToGenerateCrate(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords) {
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            Crate c = new Crate(new Texture("crate.png"), new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
            Play.entitiesToAdd.add(c);
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    public void tryToGenerateCar(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords) {
        Texture[] carTextures = new Texture[]{
            new Texture("World/Cars/BlackCar.png"),
            new Texture("World/Cars/BlueCar.png"),
            new Texture("World/Cars/GrayCar.png"),
            new Texture("World/Cars/GreenCar.png"),
            new Texture("World/Cars/RedCar.png"),
            new Texture("World/Cars/WhiteCar.png"),
        };
        Random rand = new Random();
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            Obstacle car = new Obstacle(carTextures[rand.nextInt(carTextures.length)], new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)), 1, 26, 43);
            Play.entitiesToAdd.add(car);
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    public void tryToGenerateTree(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords) {
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            WorldFeature tree = new WorldFeature(
                new Texture("World/TreeTop.png"),
                new Texture("World/TreeTopTransparent.png"),
                new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)),
                1,
                36,
                36,
                new Obstacle[]{
                    new Obstacle(new Texture("World/TreeTrunk.png"), new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)), 1, 12, 12)
                }
                );
            Play.entitiesToAdd.add(tree);
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    public void tryToGenerateHouse(float x, float y, List<Vector2> entitiesAlreadyGeneratedCoords) {
        if (!entitiesAlreadyGeneratedCoords.contains(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)))){
            tryToGenerateCrate(x, y,entitiesAlreadyGeneratedCoords);
            WorldFeature house = new WorldFeature(
                new Texture("World/roof.png"),
                new Texture("World/roofTransparent.png"),
                new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)),
                1,
                60,
                96,
                new Obstacle[]{
                    //Left wall
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+45), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+42), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+36), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+30), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+24), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+18), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+12), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+6), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)+0), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-6), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-12), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-18), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-24), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-30), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-36), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-42), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)-27, y*tileSize + (tileSize/2f)-45), 1, 6, 6),

                    //Right wall
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+45), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+42), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+36), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+30), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+24), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+18), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+12), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+6), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)+0), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-6), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-12), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-18), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-24), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-30), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-36), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-42), 1, 6, 6),
                    new Obstacle(new Texture("World/Wall.png"), new Vector2(x*tileSize + (tileSize/2f)+27, y*tileSize + (tileSize/2f)-45), 1, 6, 6),

                    //Floor
                    new Obstacle(new Texture("World/Floor.png"), new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)), 1, 60, 96, false, -5)
                }
            );
            Play.entitiesToAdd.add(house);
            entitiesAlreadyGeneratedCoords.add(new Vector2(x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f)));
        }
    }

    public void renderWorld(SpriteBatch batch, List<Vector2> entitiesAlreadyGeneratedCoords) {
        Texture tilesetTexture = new Texture(Gdx.files.internal("map_tileset.png"));
        // create a new empty tile set
        TiledMapTileSet tileSet = new TiledMapTileSet();
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
        for (int x = Math.round(Play.player.pos.x/tileSize)-viewDistance; x < Math.round(Play.player.pos.x/tileSize) + viewDistance; x++){
            for (int y = Math.round(Play.player.pos.y/tileSize)-viewDistance; y < Math.round(Play.player.pos.y/tileSize) + viewDistance; y++){
                int tileId = Math.round(seededRandomLocationValue(x, y)*(tileCount-1)) + 1;
                batch.draw(tileSet.getTile(tileId).getTextureRegion(), x*tileSize + (tileSize/2f), y*tileSize + (tileSize/2f));


                if (Math.ceil(seededRandomLocationValue(y, x) * 1000) == 999){
                    tryToGenerateCrate(x, y,entitiesAlreadyGeneratedCoords);
                }
                else if (Math.ceil(seededRandomLocationValue(y+100, x+100) * 1000) >= 997){
                    tryToGenerateTree(x, y, entitiesAlreadyGeneratedCoords);
                }
                else if (Math.ceil(seededRandomLocationValue(y+200, x+200) * 1000) == 999){
                    tryToGenerateCar(x, y, entitiesAlreadyGeneratedCoords);
                }
                else if (Math.ceil(seededRandomLocationValue(y+300, x+300) * 1000) == 999){
                    tryToGenerateHouse(x, y, entitiesAlreadyGeneratedCoords);
                }
            }
        }
    }
}
