package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Player;

public class HealDrop extends EquipmentDrop{
    int healAmount;
    public HealDrop(Texture texture, String upgradeName, String upgradeDescription, int healAmount){
        super(texture, upgradeName, upgradeDescription);
        this.healAmount = healAmount;
    }

    @Override
    public void ApplyAffect(Player p){
        p.heal(healAmount);
    }
    @Override
    public EquipmentDrop getCopy(){
        return new HealDrop(texture, upgradeName, upgradeDescription, healAmount);
    }
}
