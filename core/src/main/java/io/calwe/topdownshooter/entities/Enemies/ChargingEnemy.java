package io.calwe.topdownshooter.entities.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.screens.Play;

public class ChargingEnemy extends Enemy {
    Sprite chargingSprite;
    final float attackCooldown = 4;
    float attackCooldownTimer;
    final float chargeUpTime = 0.5f;
    float chargeUpTimer = 0;
    final float chargeSpeed = 40;
    boolean preparingToCharge = false;
    boolean charging = false;
    final float chargeDuration = 0.7f;
    float chargeTimer = 0;
    Vector2 chargeDirection;


    // The constructor - initialize all the variables
    public ChargingEnemy(Texture texture, Animation<TextureRegion> enemyWalkAnimation, Sound hurtSound, Vector2 startPos, Player target, Texture[] damageParticles, Texture attackTelegraphTexture) {
        super(texture, enemyWalkAnimation, hurtSound, startPos, target, damageParticles);
        this.maxHealth = Math.round(25 * (1 + (0.33f*(Play.currentTier-1))));
        this.health = maxHealth;
        this.chargingSprite = new Sprite(attackTelegraphTexture, width, 128);
        this.attackCooldownTimer = attackCooldown;
        this.movementSpeed = 11f;
        this.damage = 20;
        this.knockback = 5f;
    }

    //This overrides Enemy's logic method
    @Override
    public void logic(){
        //If we are locking on and preparing to charge, count down the timer until we actually charge
        if (preparingToCharge){
            if (chargeUpTimer > chargeUpTime){
                chargeUpTimer = 0;
                charging = true;
                preparingToCharge = false;
            }
            chargeUpTimer += Gdx.graphics.getDeltaTime();
            elapsedTime = 0.0f;
        }
        //If we are charging, disable collisions so we dont get stuck behind other zombies and move forward at high speeds until the timer runs out
        else if (charging){
            if (chargeTimer <= chargeDuration){
                hasSolidCollision = false;
                momentum.add(chargeDirection);
            }
            else{
                hasSolidCollision = true;
                chargeTimer = 0;
                charging = false;
            }
            chargeTimer += Gdx.graphics.getDeltaTime();

        }
        // If we aren't preparing to charge or charging, move us towards the player until we are within 50 of them
        else{
            if (target.pos.dst(pos) > 50){
                momentum.add(getMovementToPlayer());
            }
            else{
                elapsedTime = 0.0f;
            }

            //If we are within 70 of the player, and the cooldown for the charge attack has elapsed, lock on to the player's current position, and prepare to charge
            if (target.pos.dst(pos) < 70){
                if (attackCooldownTimer >= attackCooldown){
                    chargeDirection = new Vector2(target.pos.x-pos.x,target.pos.y-pos.y);
                    chargeDirection.nor();
                    chargeDirection.scl(chargeSpeed * Gdx.graphics.getDeltaTime());
                    preparingToCharge = true;
                    attackCooldownTimer = 0;
                }
            }

            //Turn to face the player
            float angleToLook = (float)Math.atan2(target.pos.x-(pos.x), target.pos.y-(pos.y));
            // convert the angle given from radians to degrees, and rotate the enemy to look in that direction
            sprite.setRotation(angleToLook*-180f/(float)Math.PI);
            chargingSprite.setRotation(angleToLook*-180f/(float)Math.PI);
        }

        tryMove();
        //Reduce their momentum over time
        momentum.scl(slide);
        float angleToLook = (float)Math.atan2(target.pos.x-(pos.x), target.pos.y-(pos.y));
        // Add an offset to the sprite to account for the fact that the sprite is not centered in its image.
        Vector2 spriteOffset = new Vector2(0, 2).rotate(angleToLook*-180f/(float)Math.PI);
        spriteOffset.add(pos);
        sprite.setPosition(spriteOffset.x, spriteOffset.y);
        chargingSprite.setPosition(spriteOffset.x, spriteOffset.y);
        attackCooldownTimer += Gdx.graphics.getDeltaTime();
        //If we are really far away from the player, destroy this enemy since it will likely never catch up and thus is just a performance drain
        if (pos.dst(target.pos) > 300){
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
        sprite.setSize(width, height);
        // Render the sprite
        sprite.draw(batch);
        //Render the sprite with the red indicator of where its going to charge if it is preparing to charge, otherwise render the normal sprite
        if (preparingToCharge){
            sprite.setRegion(chargingSprite);
            sprite.setSize(width, 128);
            sprite.draw(batch);
        }
        else{
            sprite.setRegion(enemyTexture);
            sprite.setSize(width, height);
            sprite.draw(batch);
        }
        // Update how much time has passed since we started showing the animation
        elapsedTime += Gdx.graphics.getDeltaTime();
    }


    @Override
    //executed when the enemy runs out of health
    protected void die(){
        //Add to the player's score
        target.score += 150;
        //Remove this entity from the world
        Play.entitiesToRemove.add(this);
    }

    //Run when we collide with another entity
    @Override
    public void OnEntityCollision(Entity e){
        if (charging){
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
    }

}
