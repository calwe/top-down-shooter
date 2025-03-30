package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import io.calwe.topdownshooter.entities.Player;

public class DamageDrop extends EquipmentDrop{
    final float damageMultiplier;
    public DamageDrop(Texture texture, String upgradeName, String upgradeDescription, float damageMultiplier){
        super(texture, upgradeName, upgradeDescription);
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    public void ApplyAffect(Player p){
        // increase the player's damage
        p.damageMultiplier += damageMultiplier;
    }
    @Override
    public EquipmentDrop getCopy(){
        return new DamageDrop(texture, upgradeName, upgradeDescription, damageMultiplier);
    }
}
