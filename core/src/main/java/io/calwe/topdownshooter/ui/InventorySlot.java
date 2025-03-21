package io.calwe.topdownshooter.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import io.calwe.topdownshooter.Weapon;

public class InventorySlot extends Actor {
    public static final float TEXTURE_SIZE = 12.0f;
    public static final float SCALE = 5.0f;

    TextureRegion unselectedTextureRegion;
    TextureRegion selectedTextureRegion;

    TextureRegion slotRegion;

    public Weapon weaponInSlot;
    boolean selected;

    public InventorySlot() {
        unselectedTextureRegion = new TextureRegion(new Texture("inventory-slot.png"));
        selectedTextureRegion = new TextureRegion(new Texture("inventory-sel-slot.png"));

        setBounds(selectedTextureRegion.getRegionX(), selectedTextureRegion.getRegionY(),
            selectedTextureRegion.getRegionWidth(), selectedTextureRegion.getRegionHeight());
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        if (selected) {
            batch.draw(selectedTextureRegion, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        } else {
            batch.draw(unselectedTextureRegion, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }

        if (weaponInSlot != null) {
            batch.draw(weaponInSlot.sideOn, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0,
                weaponInSlot.sideOn.getWidth(), weaponInSlot.sideOn.getHeight(), false, false);
        }
        batch.setColor(Color.WHITE);
    }
}
