package io.calwe.topdownshooter.entities.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.Obstacle;
import io.calwe.topdownshooter.entities.Particle;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.screens.Play;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Random;

public class Enemy extends Entity {

    // The maximum amount of health the player can have, and how much health they start with
    int maxHealth;
    // The amount of health the player currently has remaining
    int health;
    // The amount of damage done to the player on contact
    int damage;
    //The amount of knockback inflicted on the player on contact
    float knockback;
    //how fast the player move
    float movementSpeed;

    // The player's movement animation
    Animation<TextureRegion> enemyWalkAnimation;
    // A float that stores the time since the animation started playing so we can figure out what frame
    // of the animation to display
    float elapsedTime = 0.0f;

    //The body texture of the enemy
    Texture enemyTexture;

    //The player, so that we can track their position to move towards
    Player target;

    //The sound the zombie plays when it takes damage
    Sound hurtSound;

    //The blood particles the zombie releases when it takes damage
    Texture[] damageParticles;

    // The constructor - initialize all the variables
    public Enemy(Texture texture, Animation<TextureRegion> enemyWalkAnimation, Sound hurtSound, Vector2 startPos, Player target, Texture[] damageParticles) {
        this.maxHealth = Math.round(20 * (1 + (0.33f*(Play.currentTier-1))));
        this.health = maxHealth;
        this.damage = 10;
        this.knockback = 2f;
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.movementSpeed = 9f;
        this.enemyWalkAnimation = enemyWalkAnimation;
        this.slide = 0.85f;
        this.width = 12;
        this.height = 16;
        this.damageParticles = damageParticles;
        this.sprite = new Sprite(texture, width, height);
        this.target = target;
        this.enemyTexture = texture;
        this.hurtSound = hurtSound;
        this.boundsHeightReduction = 3;
        this.boundsWidthReduction = 3;
        //Generate the collider bounds
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }

    Vector2 getMovementToPlayer(){
        //Calculate the direction to the player, and move in that direction
        Vector2 movementToPlayer = new Vector2(target.pos.x-pos.x,target.pos.y-pos.y);
        movementToPlayer.nor();
        movementToPlayer.scl(movementSpeed*Gdx.graphics.getDeltaTime());
        Vector2 newPos = new Vector2(pos.x + ((width/2f)-boundsWidthReduction), pos.y + ((height/2f)-boundsHeightReduction));
        newPos.add(movementToPlayer);
        float angleToLook = (float)Math.atan2(target.pos.x-(pos.x), target.pos.y-(pos.y));
        //Check if there is anything blocking the way towards the player
        if (!colliderCast(
            new Vector2(pos.x + ((width/2f)-boundsWidthReduction), pos.y + ((height/2f)-boundsHeightReduction)),
            newPos,
            3
        )){
            // convert the angle given from radians to degrees, and rotate the enemy to look in that direction
            sprite.setRotation(angleToLook*-180f/(float)Math.PI);

            // Add an offset to the sprite to account for the fact that the sprite is not centered in its image.
            Vector2 spriteOffset = new Vector2(0, 2).rotate(angleToLook*-180f/(float)Math.PI);
            spriteOffset.add(pos);
            sprite.setPosition(spriteOffset.x, spriteOffset.y);
            return  movementToPlayer;
        }
        else{
            //If there is something in the way to get to the player, turn to the right and move in that direction instead.
            int rotation = 90;
//            if (pos.cpy().add(movementToPlayer.cpy().rotate(90)).dst(target.pos) > pos.cpy().add(movementToPlayer.cpy().rotate(-90)).dst(target.pos)){
//                rotation = -90;
//            }
            //Calculate the direction to the player, and move in that direction
            movementToPlayer = new Vector2(target.pos.x-pos.x,target.pos.y-pos.y);
            movementToPlayer.rotateDeg(rotation);
            movementToPlayer.nor();
            movementToPlayer.scl(movementSpeed*Gdx.graphics.getDeltaTime());


            // convert the angle given from radians to degrees, and rotate the enemy to look in that direction
            sprite.setRotation(angleToLook*-180f/(float)Math.PI + rotation);

            // Add an offset to the sprite to account for the fact that the sprite is not centered in its image.
            Vector2 spriteOffset = new Vector2(0, 2).rotate(angleToLook*-180f/(float)Math.PI);
            spriteOffset.rotate(rotation);
            spriteOffset.add(pos);
            sprite.setPosition(spriteOffset.x, spriteOffset.y);

        }
        if (Math.round(movementToPlayer.x*100f)/100f == 0.0f && Math.round(movementToPlayer.y*100f)/100f == 0.0f){
            elapsedTime = 0.0f;
        }
        return  movementToPlayer;
    }

    //Project the enemy's collider in a direction to check if moving in that direction will cause it to collide with anityhing
    boolean colliderCast(Vector2 startPos, Vector2 endPos, int increment){
        Vector2 direction = new Vector2(endPos.x-startPos.x,endPos.y-startPos.y);
        direction.nor();
        //Get all the possible things it could collide with
        Dictionary<Rectangle, Entity> collideableRects = Play.getOtherColliderRects(this);
        Object[] rects = Collections.list(collideableRects.keys()).toArray();
        Rectangle currentBounds = new Rectangle();
        // Iterate from the start positon to the end position in steps of increment
        for (int i = 0; i < startPos.dst(endPos)/increment; i++) {
            Vector2 coordToCheck = startPos.cpy();
            Vector2 tempDirection = direction.cpy();
            tempDirection.scl(increment);
            coordToCheck.add(tempDirection);
            currentBounds.x = coordToCheck.x - (width/2f) + boundsWidthReduction;
            currentBounds.y = coordToCheck.y  - (height/2f) + boundsHeightReduction;
            currentBounds.width = width-(boundsWidthReduction/2f);
            currentBounds.height = height-(boundsHeightReduction/2f);
            //Check if the collider overlaps at the current position
            for (Object rect : rects) {
                if (currentBounds.overlaps((Rectangle) rect)) {
                    //Check if the thing it is overlapping with is an Obstacle
                    //Because we want to collide with the player, and colliding
                    // with other zombies doesn't matter since theyll also be moving
                    if (collideableRects.get(rect) instanceof Obstacle) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //This overrides Entity's logic method
    @Override
    public void logic(){
        //Add moving towards the player to our current movement
        momentum.add(getMovementToPlayer());

        tryMove();
        //Reduce their momentum over time
        momentum.scl(slide);

        //If we are too far from the player to reasonably catch up, destroy this enemy as it is nothing but a resource drain
        if (pos.dst(target.pos) > 300){
            Play.entitiesToRemove.add(this);
        }
    }

    //executed when the enemy runs out of health
    protected void die(){
        //Add to the player's score
        Play.score += 100;
        //Remove this entity from the world
        Play.entitiesToRemove.add(this);
        //Play the zombie death sound.
        Gdx.audio.newSound(Gdx.files.internal("Enemies/zombieKilled.mp3")).play(0.3f);
    }

    // This overrides entity's draw method so we can have animation
    @Override
    public void draw(SpriteBatch batch) {
        // calculated based on how much time has passed since we started playing the animation what frame of the
        // animation we should be showing
        TextureRegion currentFrame = enemyWalkAnimation.getKeyFrame(elapsedTime, true);
        // Set the animation's current frame to the sprite's image
        sprite.setRegion(currentFrame);

        // Render the sprite
        sprite.draw(batch);

        sprite.setRegion(enemyTexture);
        sprite.draw(batch);
        // Update how much time has passed since we started showing the animation
        elapsedTime += Gdx.graphics.getDeltaTime();
    }

    //Run when we collide with another entity
    @Override
    public void OnEntityCollision(Entity e){
        //Check if the entity we collided with is the player
        if (e instanceof Player){
            Player player = (Player)e;
            //Deal damage to the player
            player.takeDamage(damage);
            //Knock the player away from the zombie
            Vector2 knockbackDirection = new Vector2(player.pos.x-pos.x,player.pos.y-pos.y);
            knockbackDirection.nor();
            knockbackDirection.scl(knockback);
            player.applyKnockback(knockbackDirection);
            //Remove this zombie from the world
            Play.entitiesToRemove.add(this);
        }
    }

    //Run to deal damage to the zombie
    public void takeDamage(int damage){
        //Subtract the damage from our health
        health -= damage;
        //Play the enemy damaged sound

        Random random = new Random();
        //Generate blood particles and release them from the enemy in random directions
        for (int i = 0; i < 6; i++) {
            Particle p = new Particle(damageParticles[random.nextInt(damageParticles.length)], new Vector2(pos.x + (width/2f), pos.y + (height/2f)));
            Vector2 movement = new Vector2(random.nextFloat(2)-1, random.nextFloat(2)-1);
            movement.nor();
            movement.scl(1);
            p.momentum = movement;
            Play.entitiesToAdd.add(p);
        }
        //Check if the zombie is out of health - if it is, execute the die function
        if (health <= 0){
            die();
        }
        else{
            //If the zombie is injured, play the zombie hurt sound.
            hurtSound.play(0.1f);
        }
    }

    //Apply knockback to the enemy
    public void applyKnockback(Vector2 knockback){
        this.momentum.add(knockback);
    }
}
