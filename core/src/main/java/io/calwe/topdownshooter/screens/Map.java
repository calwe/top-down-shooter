package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

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
    private final OrthogonalTiledMapRenderer renderer;
    private final TiledMap map;

    public Map() {
        // turn the tile set texture into a libGDX TiledMapTileSet
        TiledMapTileSet tileSet = createTileSet();
        // using this TiledMapTileSet, create a TiledMap by selecting a random texture for each tile
        map = createMap(tileSet);
        // create a renderer for this TiledMap
        renderer = new OrthogonalTiledMapRenderer(map);
    }

    private TiledMapTileSet createTileSet() {
        // get the texture from the path string
        Texture tilesetTexture = new Texture(Gdx.files.internal(TILE_SET_TEXTURE));

        // create a new empty tile set
        TiledMapTileSet tileSet = new TiledMapTileSet();
        // loop through each square region in the texture, and add this to the tile set
        for (int i = 0; i < TILE_COUNT; i++) {
            // get the correct region
            TextureRegion region = new TextureRegion(tilesetTexture, 0, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            // turn the region into a tile
            TiledMapTile tile = new StaticTiledMapTile(region);
            // set the id of the tile to i+1, as tile IDs must start at 1
            tile.setId(i + 1);
            // put the tile into the tileSet, at the correct position
            tileSet.putTile(i + 1, tile);
        }

        return tileSet;
    }

    private TiledMap createMap(TiledMapTileSet tileSet) {
        // create an empty tiled map
        TiledMap map = new TiledMap();
        // create the first layer in the tile map, with the right size
        TiledMapTileLayer layer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, TILE_SIZE, TILE_SIZE);

        Random random = new Random();
        // loop through each position on the tiled map and set it to a random tile from the tile set
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                // get a random tile id
                int tileId = random.nextInt(TILE_COUNT) + 1;
                // create a cell
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                // set the cells tile to the correct ID
                cell.setTile(tileSet.getTile(tileId));
                // put this cell into the layer at the correct position
                layer.setCell(x, y, cell);
            }
        }
        // add the layer to the map
        map.getLayers().add(layer);

        return map;
    }

    public void render(Camera camera) {
        // set the bounds for which the render renders in
        renderer.setView(
            camera.combined,
            0, 0,
            MAP_WIDTH * TILE_SIZE,
            MAP_HEIGHT * TILE_SIZE
        );
        // render the map
        renderer.render();
    }

    public void dispose() {
        // gracefully delete the map and renderer
        map.dispose();
        renderer.dispose();
    }
}
