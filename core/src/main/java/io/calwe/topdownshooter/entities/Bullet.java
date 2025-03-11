package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

class Bullet extends Entity {
    int damage;
    int critChance;
    float knockback;

    Bullet(Texture texture, Vector2 startPos, int damage, int critChance, float knockback){
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
    }
}


