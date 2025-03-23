package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import io.calwe.topdownshooter.Weapon;
import io.calwe.topdownshooter.entities.Enemies.Enemy;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.screens.Play;

import java.util.Collections;

public class BombDrop extends EquipmentDrop {
    public BombDrop(Texture texture, String name, String description){
        super(texture, name, description);
    }

    @Override
    public void ApplyAffect(Player p){
        for (Entity e : Play.entities) {
            if (e instanceof Enemy){
                Play.entitiesToRemove.add(e);
            }
        }
    }
    @Override
    public EquipmentDrop getCopy(){
        return new BombDrop(texture, upgradeName, upgradeDescription);
    }
}
