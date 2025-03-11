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

// A sublass of entity, which is the player - the main character
public class Player extends Entity {
    //Input - whether the mouse is currently pressed down (for shooting)
    boolean mouseDown;
    //What the current location of the mouse is - so that the character can look at the mouse location
    Vector2 mouseCoords;

    //The character's inventory
    Weapon[] inventory;
    // What slot in the character's inventory they currently have selected
    int currentInventorySlot = 0;

    // The maximum amount of health the player can have, and how much health they start with
    int maxHealth;
    // The amount of health the player currently has remaining
    int health;
    //how fast the player move
    float movementSpeed;

    // The camera - this is used to keep the camera's position over the player
    OrthographicCamera camera;

    // The player's movement animation
    Animation<TextureRegion> playerWalkAnimation;
    // A float that stores the time since the animation started playing so we can figure out what frame
    // of the animation to display
    float elapsedTime = 0.0f;

    Texture playerTexture;

    // The constructor - intialize all the variables
    public Player(Texture texture, Animation<TextureRegion> playerWalkAnimation, Vector2 startPos, Weapon[] inventory, OrthographicCamera camera) {
        this.maxHealth = 100;
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.movementSpeed = 25f;
        this.mouseDown = false;
        this.playerWalkAnimation = playerWalkAnimation;
        this.mouseCoords = new Vector2(0,0);
        this.slide = 0.85f;
        this.width = 12;
        this.height = 16;
        this.inventory = inventory;
        this.camera = camera;
        this.sprite = new Sprite(texture, width, height);
        this.health = maxHealth;
    }

    private Vector2 calculateMovementFromInputs(){
        //Initialise the movement vector and set it to zero
        Vector2 movement = Vector2.Zero;
        movement.set(0,0);
        //Add our inputs to the movement vector - ASWD or right left up and down arrows.
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
        //Normalise the movement so that its total magnitude is 1, unless we have no movement at all
        //This is to prevent the character from moving faster when moving diagonally
        movement.nor();
        // If the character is not moving, reset the timer for the walking animation to prevent it from playing
        if (movement.x == 0.0f && movement.y == 0.0f) {
            elapsedTime = 0.0f;
        }
        // Calculate the actual movement speed by multiplying the normalised inputs by the player movement speed, and
        // the deltaTime. This is the time since the last frame. Doing this prevents the character from moving at
        // different speeds at different frame rates
        return new Vector2(movement.x * movementSpeed * Gdx.graphics.getDeltaTime(), movement.y * movementSpeed * Gdx.graphics.getDeltaTime());
    }

    //When we get the mouse coordinates using the Gdx.input.getX() and getY(), we get the coordinates on the
    // screen, not in the world. This function gets the world coordinates for us.
    Vector2 getMousePosInGameWorld(OrthographicCamera camera) {
        //Convert the screen coordinates to world coordinates and store it as a vector3
        Vector3 temp = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        //We are working in a 2d world, and thus use vector2s not vector3s, so convert the vector3 to a
        // vector2 and return it
        return new Vector2(temp.x, temp.y);
    }

    private void handleInventorySwitching(){
        //Inventory slot switching can only handle inventories up to 9 slots long, because it iterates through
        // number keys, starting at 1.
        // Iterate through number keys up to the length of the inventory (e.g. if the inventory is 3 slots in size,
        // iterate through numbers 1-3. Check if any of these keys are pressed. If they are and that slot is not
        // empty, switch to that slot
        for (int i = 0; i < inventory.length; i++) {
            if (Gdx.input.isKeyPressed(8+i)) {
                if (inventory[i] != null) {
                    currentInventorySlot = i;
                }
            }
        }
    }

    //This overrides Entity's input method.
    @Override
    public void input(OrthographicCamera camera){
        // Take in the movement inputs and add the resulting movement to our momentum
        this.momentum.add(calculateMovementFromInputs());
        // Check if we are pressing a number key to switch inventory slot
        handleInventorySwitching();
        //update the mouseCoords value with the current location of the mouse
        mouseCoords.set(getMousePosInGameWorld(camera));
        // update the mouseDown value with the current status of the mouse
        mouseDown = Gdx.input.isTouched();
    }

    //This overrides Entity's logic method
    @Override
    public void logic(){

        //Update the character's position according to how far their momentum is causing them to move
        pos = new Vector2(pos.x + momentum.x, pos.y + momentum.y);
        //Reduce their momentum over time
        momentum.scl(slide);


        playerTexture = inventory[currentInventorySlot].texture;
        //Handle clicks registered - shoot the current weapon
        if (mouseDown){
            // Check what weapon we are currently using
            Weapon weapon = inventory[currentInventorySlot];

            // Find the angle between the player and the mouse
            float angleToLook = (float)Math.atan2(mouseCoords.x-(pos.x+width/2f), mouseCoords.y-(pos.y+height/2f));
            // Calculate the offset to account for the fact that the weapon is not at the center of the player
            float offsetRotation = angleToLook*-180f/(float)Math.PI;
            Vector2 weaponOffset = new Vector2(5, 6).rotate(offsetRotation);

            // calculate the gun's position by finding the center of the player and adding the weapon offset
            Vector2 firingPos = new Vector2(pos.x+width/2f, pos.y+height/2f);
            firingPos.add(weaponOffset);
            // calculate the direction vector between the gun and the mouse position
            Vector2 direction = new Vector2(mouseCoords.x - (firingPos.x), mouseCoords.y - (firingPos.y));
            direction.nor();
            // calculate the angle between the gun and the mouse position
            float bulletAngleToLook = (float)Math.atan2(mouseCoords.x-(firingPos.x), mouseCoords.y-(firingPos.y));
            float bulletRotation = bulletAngleToLook*-180f/(float)Math.PI;

            // attempt to fire the weapon at the mouse position
            boolean fireSuccessful = weapon.fire(firingPos, direction, bulletRotation);
            if (fireSuccessful){
                //if the weapon fired, apply recoil and show the firing texture
                direction.scl(-0.01f*weapon.recoil);
                momentum.add(direction);
                playerTexture = inventory[currentInventorySlot].firingTexture;
            }
            // reset mouseDown
            mouseDown = false;
        }

        // Calculate the angle the player will need to be turning towards to face the mouse, taking into account that
        // the pos.x and pos.y coordinates are from the bottom left corner of the player not the center
        float angleToLook = (float)Math.atan2(mouseCoords.x-(pos.x+width/2f), mouseCoords.y-(pos.y+height/2f));
        // convert the angle given from radians to degrees, and rotate the player to look in that direction
        sprite.setRotation(angleToLook*-180f/(float)Math.PI);

        // move the camera so it remains always centered on the player (taking into account that pos is the bottom
        // left corner of the player
        camera.position.set(pos.x+(width/2f), pos.y+(height/2f), 0);

        // Add an offset to the sprite to account for the fact that the player sprite is not centered in its image.
        Vector2 spriteOffset = new Vector2(0, 2).rotate(angleToLook*-180f/(float)Math.PI);
        spriteOffset.add(pos);
        sprite.setPosition(spriteOffset.x, spriteOffset.y);

    }

    // This overrides entity's draw method so we can have animation
    @Override
    public void draw(SpriteBatch batch) {
        // calculated based on how much time has passed since we started playing the animation what frame of the
        // animation we should be showing
        TextureRegion currentFrame = playerWalkAnimation.getKeyFrame(elapsedTime, true);
        // Set the animation's current frame to the player sprite's image
        sprite.setRegion(currentFrame);

        // Render the player sprite
        sprite.draw(batch);

        sprite.setRegion(playerTexture);
        sprite.draw(batch);
        // Update how much time has passed since we started showing the animation
        elapsedTime += Gdx.graphics.getDeltaTime();
    }
}
