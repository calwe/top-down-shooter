package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Enemies.Enemy;
import io.calwe.topdownshooter.screens.Play;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bullet extends Entity {
    int damage;
    int critChance;
    float critMultiplier;
    float knockback;
    float lifeTime = 5;
    float timer = 0;
    List<Enemy> alreadyHit;
    boolean pierces;

    public Bullet(Texture texture, Vector2 startPos, int damage, int critChance, float critMultiplier, float knockback, boolean pierces){
        this.pierces = pierces;
        this.alreadyHit = new ArrayList<>();
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.width = 1;
        this.height = 2;
        this.sprite = new Sprite(texture, width, height);
        this.damage = damage;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.knockback = knockback;
        this.hasSolidCollision = false;
        this.boundsHeightReduction = 0;
        this.boundsWidthReduction = 0;
        //Generate the bullet's collider bounds
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }

    @Override
    public void logic() {
        //move the bullet based on its momentum
        tryMove();
        sprite.setPosition(pos.x, pos.y);
        //destroy the bullet after a certain amount of time has passed, to prevent it from existing forever
        // and slowing down the game
        timer += Gdx.graphics.getDeltaTime();
        if (timer > lifeTime){
            Play.entitiesToRemove.add(this);
        }
    }

    //Run when the bullet hits something
    @Override
    public void OnEntityCollision(Entity e){
        //If the thing we hit is an enemy
        if (e instanceof Enemy){
            Enemy enemy = (Enemy)e;
            if (!alreadyHit.contains(enemy)) {
                Random random = new Random();
                //Generate some hovering text showing the damage
                HitText hitText;
                Vector2 hitTextPos = new Vector2(enemy.pos.x + (enemy.width / 2f) + random.nextFloat(12) - 6, enemy.pos.y + enemy.height);
                //Decide whether the hit is a critical hit (x2 damage) and deal the damage to the enemy
                if (random.nextInt(100) <= critChance) {
                    enemy.takeDamage(Math.round(damage * critMultiplier));
                    hitText = new HitText(String.valueOf(damage * 2), hitTextPos, Color.YELLOW);
                } else {
                    enemy.takeDamage(damage);
                    hitText = new HitText(String.valueOf(damage), hitTextPos, Color.RED);
                }
                //Add the hovering text to the world
                Play.entitiesToAdd.add(hitText);
                // calculate and apply knockback to the enemy
                Vector2 knockbackDirection = new Vector2(enemy.pos.x - pos.x, enemy.pos.y - pos.y);
                knockbackDirection.nor();
                knockbackDirection.scl(knockback);
                enemy.applyKnockback(knockbackDirection);
                if (pierces) {
                    alreadyHit.add(enemy);
                } else {
                    //Destroy this bullet
                    Play.entitiesToRemove.add(this);
                }
            }
        }
        if (e instanceof Obstacle){
            //Destroy this bullet
            Play.entitiesToRemove.add(this);
        }
    }
}


