package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Player extends Entity {
    //Input
    boolean mouseDown;
    Vector2 mouseCoords;

    //Inventory
    Weapon[] inventory;
    int currentInventorySlot = 0;

    //Statistics
    int health;
    float movementSpeed;
    OrthographicCamera camera;

    Animation<TextureRegion> playerWalkAnimation;
    float elapsedTime = 0.0f;


    public Player(Texture texture, Animation<TextureRegion> playerWalkAnimation, Vector2 startPos, Weapon[] inventory, OrthographicCamera camera) {
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.movementSpeed = 25f;
        this.texture = texture;
        this.mouseDown = false;
        this.playerWalkAnimation = playerWalkAnimation;
        this.mouseCoords = new Vector2(0,0);
        this.slide = 0.85f;
        this.width = 12;
        this.height = 10;
        this.inventory = inventory;
        this.camera = camera;
        this.sprite = new Sprite(texture, width, height);
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
        if (movement.x == 0.0f && movement.y == 0.0f) {
            elapsedTime = 0.0f;
        }
        return new Vector2(movement.x * movementSpeed * Gdx.graphics.getDeltaTime(), movement.y * movementSpeed * Gdx.graphics.getDeltaTime());
    }

    Vector2 getMousePosInGameWorld(OrthographicCamera camera) {
        Vector3 temp = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        return new Vector2(temp.x, temp.y);
    }

    private void handleInventorySwitching(){
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            if (inventory[0] != null) {
                currentInventorySlot = 0;
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            if (inventory[1] != null) {
                currentInventorySlot = 1;
            }

        }
        else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            if (inventory[2] != null) {
                currentInventorySlot = 2;
            }
        }
    }

    @Override
    public void input(OrthographicCamera camera){
        this.momentum.add(calculateMovement());
        handleInventorySwitching();
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
        float angleToLook = (float)Math.atan2(mouseCoords.x-(pos.x+width/2f), mouseCoords.y-(pos.y+height/2f));
        sprite.setRotation(angleToLook*-180f/(float)Math.PI);
        sprite.setPosition(pos.x, pos.y);
        camera.position.set(pos.x+(width/2f), pos.y+(height/2f), 0);
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = playerWalkAnimation.getKeyFrame(elapsedTime, true);
        sprite.setRegion(currentFrame);
        sprite.draw(batch);
        elapsedTime += Gdx.graphics.getDeltaTime();
    }
}
