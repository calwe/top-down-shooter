package io.calwe.topdownshooter.entities.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.screens.Play;

public class RangedEnemy extends Enemy {
    final float projectileSpeed = 2.5f;
    final float attackCooldown = 2;
    float attackCooldownTimer = 0;
    final Texture projectileTexture;

    // The constructor - initialize all the variables
    public RangedEnemy(Texture texture, Animation<TextureRegion> enemyWalkAnimation, Sound hurtSound, Vector2 startPos, Player target, Texture[] damageParticles, Texture projectileTexture) {
        super(texture, enemyWalkAnimation, hurtSound, startPos, target, damageParticles);
        this.maxHealth = Math.round(25 * (1 + (0.33f*(Play.currentTier-1))));
        this.health = maxHealth;
        this.projectileTexture = projectileTexture;
    }


    //This overrides Enemy's logic method
    @Override
    public void logic() {
        //If the enemy is more than 60 away, move towards the player
        if (target.pos.dst(pos) > 60) {
            momentum.add(getMovementToPlayer());
        } else {
            elapsedTime = 0.0f;
        }

        //If we are less than 120 away, and the attack is not on cooldown, launch a projectile at the player
        if (target.pos.dst(pos) < 120) {
            if (attackCooldownTimer >= attackCooldown) {
                EnemyProjectile p = new EnemyProjectile(projectileTexture, new Vector2(pos.x + (width / 2f), pos.y + (height / 2f)), damage, knockback);
                //Turn the bullet so it is facing towards the target
                float angleToLook = (float) Math.atan2(target.pos.x - (pos.x), target.pos.y - (pos.y));
                float rotation = angleToLook * -180f / (float) Math.PI;
                p.sprite.setRotation(rotation);
                // apply the movement to the projectile
                p.momentum.set(new Vector2(0, projectileSpeed).rotateDeg(rotation));
                // Add the projectile to the entitiesToAdd list so it can be added to the master entities list, and rendered and have its logic handled
                Play.entitiesToAdd.add(p);
                //Reset the attack cooldown
                attackCooldownTimer = 0;
            }
        }
        tryMove();
        //Reduce their momentum over time
        momentum.scl(slide);
        //Turn to face the player
        float angleToLook = (float) Math.atan2(target.pos.x - (pos.x), target.pos.y - (pos.y));
        // convert the angle given from radians to degrees, and rotate the enemy to look in that direction
        sprite.setRotation(angleToLook * -180f / (float) Math.PI);

        // Add an offset to the sprite to account for the fact that the sprite is not centered in its image.
        Vector2 spriteOffset = new Vector2(0, 2).rotateDeg(angleToLook * -180f / (float) Math.PI);
        spriteOffset.add(pos);
        sprite.setPosition(spriteOffset.x, spriteOffset.y);

        //Increase the attack cooldown timer over time
        attackCooldownTimer += Gdx.graphics.getDeltaTime();
        //Check if the zombie is out of health - if it is, execute the die function
        if (health <= 0) {
            die();
        }
    }

    @Override
    //executed when the enemy runs out of health
    protected void die(){
        //Add to the player's score
        target.score += 150;
        //Remove this entity from the world
        Play.entitiesToRemove.add(this);
        //If the zombie is too far away from the player to reasonably catch up or be seen, it is nothing but a drain on
        // resources and should be removed
        if (pos.dst(target.pos) > 300){
            Play.entitiesToRemove.add(this);
        }
    }

    //Run when we collide with another entity
    @Override
    public void OnEntityCollision(Entity e){
        //Don't do anything - this enemy doesn't deal damage on collision since it has ranged attacks instead.
    }

}
