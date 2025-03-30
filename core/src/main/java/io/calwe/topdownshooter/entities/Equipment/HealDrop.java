package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import io.calwe.topdownshooter.entities.Player;

public class HealDrop extends EquipmentDrop{
    final int healAmount;
    public HealDrop(Texture texture, String upgradeName, String upgradeDescription, int healAmount){
        super(texture, upgradeName, upgradeDescription);
        this.healAmount = healAmount;
    }

    @Override
    public void ApplyAffect(Player p){
        //Restore the player's health
        p.heal(healAmount);
    }
    @Override
    public EquipmentDrop getCopy(){
        return new HealDrop(texture, upgradeName, upgradeDescription, healAmount);
    }
}
