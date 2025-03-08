package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Player extends Entity {
    //Input
    boolean mouseDown;
    Vector2 mouseCoords;

    //Inventory
    Weapon[] inventory;
    int currentInventorySlot = 0;

    int direction;

    //Statistics
    int health;
    float movementSpeed;
    OrthographicCamera camera;

    public Player(Texture texture, Vector2 startPos, Weapon[] inventory, OrthographicCamera camera) {
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.movementSpeed = 25f;
        this.direction = 0;
        this.texture = texture;
        this.mouseDown = false;
        this.mouseCoords = new Vector2(0,0);
        this.slide = 0.85f;
        this.width = 15;
        this.height = 15;
        this.inventory = inventory;
        this.camera = camera;
        this.sprite = new Sprite(texture);
    }

    private Vector2 calculateMovement(){
        Vector2 movement = Vector2.Zero;
        movement.set(0,0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.x += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.y -= 1;
        }
        movement.nor();
        return new Vector2(movement.x * movementSpeed * Gdx.graphics.getDeltaTime(), movement.y * movementSpeed * Gdx.graphics.getDeltaTime());
    }

    Vector2 getMousePosInGameWorld(OrthographicCamera camera) {
        Vector3 temp = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        return new Vector2(temp.x, temp.y);
    }

    @Override
    public void input(OrthographicCamera camera){
        this.momentum.add(calculateMovement());
        mouseCoords.set(getMousePosInGameWorld(camera));
        if (Gdx.input.isTouched()) { // If the user has clicked or tapped the screen
            mouseDown = true;
        }
    }

    @Override
    public void logic(){
        //Move the character
        pos = new Vector2(pos.x + momentum.x, pos.y + momentum.y);
        //Reduce their momentum over time
        momentum.scl(slide);

        //Handle clicks registered
        if (mouseDown){

            mouseDown = false;
        }
        
        sprite.setPosition(pos.x, pos.y);
        camera.position.set(pos.x+(width/2f), pos.y+(height/2f), 0);
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
