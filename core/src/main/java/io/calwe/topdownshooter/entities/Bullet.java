package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

import java.util.Random;

public class Bullet extends Entity {
    int damage;
    int critChance;
    float knockback;
    float lifeTime = 5;
    float timer = 0;

    public Bullet(Texture texture, Vector2 startPos, int damage, int critChance, float knockback){
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);

        //The bullet shouldn't slow down over time
        this.slide = 1;

        this.width = 1;
        this.height = 2;
        this.sprite = new Sprite(texture, width, height);
        this.damage = damage;
        this.critChance = critChance;
        this.knockback = knockback;
        this.hasSolidCollision = false;
        this.boundsHeightReduction = 0;
        this.boundsWidthReduction = 0;
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }

    @Override
    public void logic() {
        tryMove();
        //Reduce their momentum over time
        momentum.scl(slide);
        sprite.setPosition(pos.x, pos.y);
        timer += Gdx.graphics.getDeltaTime();
        if (timer > lifeTime){
            Play.entitiesToRemove.add(this);
        }
    }

    @Override
    public void OnEntityCollision(Entity e){
        if (e instanceof Enemy){
            Enemy enemy = (Enemy)e;
            Random random = new Random();
            HitText hitText;
            Vector2 hitTextPos = new Vector2(enemy.pos.x + (enemy.width/2f) + random.nextFloat(12)-6, enemy.pos.y + enemy.height);
            if (random.nextInt(100) <= critChance){
                enemy.takeDamage(damage*2);
                hitText = new HitText(String.valueOf(damage*2), hitTextPos, Color.YELLOW);
            }
            else{
                enemy.takeDamage(damage);
                hitText = new HitText(String.valueOf(damage), hitTextPos, Color.RED);
            }
            Play.entitiesToAdd.add(hitText);
            Vector2 knockbackDirection = new Vector2(enemy.pos.x-pos.x,enemy.pos.y-pos.y);
            knockbackDirection.nor();
            knockbackDirection.scl(knockback);
            enemy.applyKnockback(knockbackDirection);
            Play.entitiesToRemove.add(this);
        }
    }
}


