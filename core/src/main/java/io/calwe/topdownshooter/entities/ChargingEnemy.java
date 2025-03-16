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

import java.util.Random;

public class ChargingEnemy extends Enemy{
    Sprite chargingSprite;
    float attackCooldown;
    float attackCooldownTimer;
    float chargeUpTime;
    float chargeUpTimer;
    float chargeSpeed;
    boolean preparingToCharge;
    boolean charging;
    float chargeDuration;
    float chargeTimer;
    Vector2 chargeDirection;

    // The constructor - initialize all the variables
    public ChargingEnemy(Texture texture, Animation<TextureRegion> enemyWalkAnimation, Sound hurtSound, Vector2 startPos, Player target, Texture[] damageParticles, Texture attackTelegraphTexture, float attackCooldown,  float chargeUpTime, float chargeSpeed, float chargeDuration) {
        super(texture, enemyWalkAnimation, hurtSound, startPos, target, damageParticles);
        this.chargingSprite = new Sprite(attackTelegraphTexture, width, 128);
        this.attackCooldown = attackCooldown;
        this.attackCooldownTimer = attackCooldown;
        this.chargeUpTime = chargeUpTime;
        this.chargeUpTimer = 0;
        this.chargeTimer = 0;
        this.movementSpeed = 11f;
        this.damage = 20;
        this.knockback = 5f;
        this.chargeDuration = chargeDuration;
        this.chargeSpeed = chargeSpeed;
        this.preparingToCharge = false;
        this.charging = false;
    }

    //This overrides Enemy's logic method
    @Override
    public void logic(){
        if (preparingToCharge){
            if (chargeUpTimer > chargeUpTime){
                chargeUpTimer = 0;
                charging = true;
                preparingToCharge = false;
            }
            chargeUpTimer += Gdx.graphics.getDeltaTime();
            elapsedTime = 0.0f;
        }
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
        else{
            if (target.pos.dst(pos) > 50){
                //Calculate the direction to the player, and move in that direction
                Vector2 movementToPlayer = new Vector2(target.pos.x-pos.x,target.pos.y-pos.y);
                movementToPlayer.nor();
                movementToPlayer.scl(movementSpeed * Gdx.graphics.getDeltaTime());
                momentum.add(movementToPlayer);
            }
            else{
                elapsedTime = 0.0f;
            }

            if (target.pos.dst(pos) < 55 ){
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
        //Check if the zombie is out of health - if it is, execute the die function
        if (health <= 0){
            die();
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
        if (preparingToCharge){
            //chargingSprite.draw(batch);
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
        Play.score += 150;
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
