package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

public class Enemy extends Entity {

    // The maximum amount of health the player can have, and how much health they start with
    int maxHealth;
    // The amount of health the player currently has remaining
    int health;
    // The amount of damage done to the player on contact
    int damage;
    float knockback;
    //how fast the player move
    float movementSpeed;

    // The player's movement animation
    Animation<TextureRegion> enemyWalkAnimation;
    // A float that stores the time since the animation started playing so we can figure out what frame
    // of the animation to display
    float elapsedTime = 0.0f;

    Texture enemyTexture;

    Player target;

    Sound hurtSound;

    // The constructor - initialize all the variables
    public Enemy(Texture texture, Animation<TextureRegion> enemyWalkAnimation, Sound hurtSound, Vector2 startPos, Player target) {
        this.maxHealth = 50;
        this.damage = 8;
        this.knockback = 2f;
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.movementSpeed = 5f;
        this.enemyWalkAnimation = enemyWalkAnimation;
        this.slide = 0.85f;
        this.width = 12;
        this.height = 16;
        this.sprite = new Sprite(texture, width, height);
        this.health = maxHealth;
        this.target = target;
        this.enemyTexture = texture;
        this.hurtSound = hurtSound;
        this.boundsHeightReduction = 3;
        this.boundsWidthReduction = 3;
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }

    //This overrides Entity's logic method
    @Override
    public void logic(){
        Vector2 movementToPlayer = new Vector2(target.pos.x-pos.x,target.pos.y-pos.y);
        movementToPlayer.nor();
        movementToPlayer.scl(movementSpeed*Gdx.graphics.getDeltaTime());
        momentum.add(movementToPlayer);

        tryMove();
        //Reduce their momentum over time
        momentum.scl(slide);

        float angleToLook = (float)Math.atan2(target.pos.x-(pos.x), target.pos.y-(pos.y));
        // convert the angle given from radians to degrees, and rotate the enemy to look in that direction
        sprite.setRotation(angleToLook*-180f/(float)Math.PI);

        // Add an offset to the sprite to account for the fact that the sprite is not centered in its image.
        Vector2 spriteOffset = new Vector2(0, 2).rotate(angleToLook*-180f/(float)Math.PI);
        spriteOffset.add(pos);
        sprite.setPosition(spriteOffset.x, spriteOffset.y);
        if (health <= 0){
            Play.entitiesToRemove.add(this);
        }
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

    @Override
    public void OnEntityCollision(Entity e){
        if (e instanceof Player){
            Player player = (Player)e;
            player.takeDamage(damage);
            Vector2 knockbackDirection = new Vector2(player.pos.x-pos.x,player.pos.y-pos.y);
            knockbackDirection.nor();
            knockbackDirection.scl(knockback);
            player.applyKnockback(knockbackDirection);
            Play.entitiesToRemove.add(this);
        }
    }

    public void takeDamage(int damage){
        health -= damage;
        hurtSound.play(0.1f);
    }

    public void applyKnockback(Vector2 knockback ){
        this.momentum.add(knockback);
    }
}
