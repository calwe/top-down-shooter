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

import java.awt.image.BufferedImage;
import java.util.Random;

public class Map {
    // the tile set texture contains each possible tile for the map in a stitched together column
    // assets in libGDX are stored in the assets/ folder
    public static final String TILE_SET_TEXTURE = "map_tileset.png";
    // width/height of each tile in pixels. each tile must be square
    public static final int TILE_SIZE = 12;
    // number of tiles in the texture
    public static final int TILE_COUNT = 5;

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
        if (x > 0){
            x *= 2;
        }
        else{
            x = (Math.abs(x) * 2) + 1;
        }
        if (y > 0){
            x *= 2;
        }
        else{
            y = (Math.abs(y) * 2) + 1;
        }
        String xString = String.valueOf(x);
        String yString = String.valueOf(y);
        while (xString.length() < 5) {
            xString = "0" + xString;
        }
        while (yString.length() < 5) {
            yString = "0" + yString;
        }
        Random r = new Random((long) (Integer.valueOf(xString + yString)) * initialTime);
        return r.nextFloat();
    }

    public void renderWorld(SpriteBatch batch){
        Texture tilesetTexture = new Texture(Gdx.files.internal("map_tileset.png"));
        int tileCount = 5;
        int tileSize = 12;
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
                batch.draw(tileSet.getTile(tileId).getTextureRegion(), x*tileSize, y*tileSize);
            }
        }
    }
}
