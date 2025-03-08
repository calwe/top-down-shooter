package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Entity {
    protected Vector2 pos;
    protected Vector2 momentum;
    protected float slide;

    protected Texture texture;
    protected int width;
    protected int height;
    protected Sprite sprite;

    public void input(OrthographicCamera camera) {

    }

    public void logic() {

        sprite.setPosition(pos.x, pos.y);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
