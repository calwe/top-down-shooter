package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Enemies.Enemy;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.Landmine;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.screens.Play;
import io.calwe.topdownshooter.screens.Map;

public class BombDrop extends EquipmentDrop {
    public BombDrop(Texture texture, String name, String description){
        super(texture, name, description);
    }

    @Override
    public void ApplyAffect(Player p){
        //Iterate through each enemy and spawn a landmine on them to cause them to blow up, then play an explosion sound effect
        for (Entity e : Play.entities) {
            if (e instanceof Enemy){
                Landmine explosion = new Landmine(new Texture("World/landmine.png"), new Vector2(e.pos.x, e.pos.y), Map.explosionAnimation, 0);
                explosion.detonate();
                Play.entitiesToAdd.add(explosion);
                Gdx.audio.newSound(Gdx.files.internal("explosion.mp3")).play(0.3f);
            }
        }
    }
    @Override
    public EquipmentDrop getCopy(){
        return new BombDrop(texture, upgradeName, upgradeDescription);
    }
}
