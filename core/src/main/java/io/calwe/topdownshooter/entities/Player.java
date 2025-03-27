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
import io.calwe.topdownshooter.Weapon;
import io.calwe.topdownshooter.screens.Play;
import io.calwe.topdownshooter.ui.HUD;

import java.util.*;

// A subclass of entity, which is the player - the main character
public class Player extends Entity {
    //Input - whether the mouse is currently pressed down (for shooting)
    boolean mouseDown = false;
    //What the current location of the mouse is - so that the character can look at the mouse location
    Vector2 mouseCoords = new Vector2(0,0);

    //The character's inventory
    public Weapon[] inventory;
    // Which slot in the character's inventory they currently have selected
    int currentInventorySlot = 0;

    // The maximum amount of health the player can have, and how much health they start with
    public int maxHealth = 100;
    // The amount of health the player currently has remaining
    int health;
    //how fast the player move
    final float movementSpeed = 10f;

    // The player's score, based on how many zombies have been killed and how long the player has been alive
    public int score;

    // The camera - this is used to keep the camera's position over the player
    final OrthographicCamera camera;

    // The player's movement animation
    final Animation<TextureRegion> playerWalkAnimation;
    // A float that stores the time since the animation started playing so we can figure out what frame
    // of the animation to display
    float elapsedTime = 0.0f;

    //The player's current body image based on what weapon he is currently holding
    Texture playerTexture;

    //An array of textures for the particles that should be released when the player is damage
    final Texture[] damageParticles;

    // These are used so that upgrades can improve your capabilities - otherwise they would be hardcoded
    public float damageMultiplier = 1;
    public int saveAmmoChance = 0;
    public float critMultiplier = 2;
    public int additionalCritChance = 0;

    public HUD hud = new HUD(this.health, this.score);

    // The constructor - intialize all the variables
    public Player(Texture texture, Animation<TextureRegion> playerWalkAnimation, Vector2 startPos, Weapon[] inventory, Texture[] damageParticles, OrthographicCamera camera) {
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.playerWalkAnimation = playerWalkAnimation;
        this.slide = 0.85f;
        this.width = 12;
        this.layer = 15;
        this.height = 16;
        this.inventory = inventory;
        this.damageParticles = damageParticles;
        this.camera = camera;
        this.sprite = new Sprite(texture, width, height);
        this.health = maxHealth;
        this.boundsHeightReduction = 3;
        this.boundsWidthReduction = 3;
        //Calculate the player's collider bounds
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
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
        // Iterate through number keys up to the length of the inventory - e.g. if the inventory is 3 slots in size,
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
        tryMove();
        //Reduce their momentum over time
        momentum.scl(slide);

        //Set the player's current body image based on the weapon they are currently holding
        playerTexture = inventory[currentInventorySlot].texture;
        //Handle clicks registered - shoot the current weapon
        if (mouseDown){
            // Check what weapon we are currently using
            Weapon weapon = inventory[currentInventorySlot];

            // Find the angle between the player and the mouse
            float angleToLook = (float)Math.atan2(mouseCoords.x-(pos.x+width/2f), mouseCoords.y-(pos.y+height/2f));
            // Calculate the offset to account for the fact that the weapon is not at the center of the player
            float offsetRotation = angleToLook*-180f/(float)Math.PI;
            Vector2 weaponOffset = new Vector2(5, 6).rotateDeg(offsetRotation);

            // calculate the gun's position by finding the center of the player and adding the weapon offset
            Vector2 firingPos = new Vector2(pos.x+width/2f, pos.y+height/2f);
            firingPos.add(weaponOffset);

            // calculate the angle between the gun and the mouse position
            float bulletAngleToLook = (float)Math.atan2(mouseCoords.x-(firingPos.x), mouseCoords.y-(firingPos.y));
            float bulletRotation = bulletAngleToLook*-180f/(float)Math.PI;

            // attempt to fire the weapon at the mouse position
            boolean fireSuccessful = weapon.fire(firingPos, bulletRotation, damageMultiplier, critMultiplier, additionalCritChance, saveAmmoChance);
            if (fireSuccessful){
                //if the weapon fired, apply recoil and show the firing texture
                Vector2 direction = new Vector2(mouseCoords.x - (firingPos.x), mouseCoords.y - (firingPos.y));
                direction.nor();
                direction.scl(-0.01f*weapon.recoil);
                momentum.add(direction);
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
        Vector2 spriteOffset = new Vector2(0, 2).rotateDeg(angleToLook*-180f/(float)Math.PI);
        spriteOffset.add(pos);
        sprite.setPosition(spriteOffset.x, spriteOffset.y);

        //Check if the player has no health remaining - execute the die function if they don't
        if (health <= 0){
            die();
        }
        hud.updateStatistics(health, score);
    }

    //If the player is out of health, switch to the game over screen
    private void die(){
        Play.game.GameOver(score);
    }

    // This overrides entity's draw method so we can have animation
    @Override
    public void draw(SpriteBatch batch) {
        // calculated based on how much time has passed since we started playing the animation what frame of the
        // animation we should be showing
        TextureRegion currentFrame = playerWalkAnimation.getKeyFrame(elapsedTime, true);
        // Set the animation's current frame to the player sprite's image
        sprite.setRegion(currentFrame);
        sprite.setSize(width, height);
        // Render the player's animated legs
        sprite.draw(batch);
        //The player's body is slightly larger, so render it bigger
        sprite.setSize(width, 18);
        //Render the player's body with the weapon they are holding
        sprite.setRegion(playerTexture);
        sprite.draw(batch);
        // Update how much time has passed since we started showing the animation
        elapsedTime += Gdx.graphics.getDeltaTime();
    }

    //Deal damage to the player
    public void takeDamage(int damage){
        // subtract the damage dealt from their health
        health -= damage;
        Random random = new Random();
        //Generate blood particles and release them in random directions from the player
        for (int i = 0; i < 6; i++) {
            Particle p = new Particle(damageParticles[random.nextInt(damageParticles.length)], new Vector2(pos.x + (width/2f), pos.y + (height/2f)));
            Vector2 movement = new Vector2(random.nextFloat(2)-1, random.nextFloat(2)-1);
            movement.nor();
            movement.scl(1);
            p.momentum = movement;
            Play.entitiesToAdd.add(p);
        }
        //Play the player hurt sound
        Gdx.audio.newSound(Gdx.files.internal("playerHit.mp3")).play(1.5f);
    }

    //Add knockback to the player's momentum
    public void applyKnockback(Vector2 knockback){
        this.momentum.add(knockback);
    }

    //Add a weapon to the inventory, or swap out the currently held weapon if there is no space in the inventory
    // remaining
    public void addToInventory(Weapon weaponToAdd){
        //iterate through the inventory and add the weapon to the first empty slot
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null){
                inventory[i] = weaponToAdd;
                hud.inventory.slots[i].weaponInSlot = weaponToAdd;
                return;
            }
        }
        //if there are no empty slots in the inventory
        //drop the current weapon
        WeaponDrop droppedWeapon = new WeaponDrop(inventory[currentInventorySlot].copy(), new Vector2(pos.x + (width/2f), pos.y + height/2f));
        if (droppedWeapon.weapon.ammo > 0){
            Play.entitiesToAdd.add(droppedWeapon);
        }
        //And add the picked up weapon to our inventory in its place
        inventory[currentInventorySlot] = weaponToAdd;
        hud.inventory.slots[currentInventorySlot].weaponInSlot = weaponToAdd;
    }

    //Increase the player's current health by the amount provided, up to the limit of their maximum health
    public void heal(int healAmount){
        health += healAmount;
        if (health > maxHealth){
            health = maxHealth;
        }
    }

    public void resize(int width, int height) {
        hud.resize(width, height);
    }

    public void dispose() {
        hud.dispose();
    }
}
