package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import io.calwe.topdownshooter.entities.Player;

public class CritChanceDrop extends EquipmentDrop{
    final int additionalCritChance;
    public CritChanceDrop(Texture texture, String upgradeName, String upgradeDescription, int additionalCritChance) {
        super(texture,  upgradeName, upgradeDescription);
        this.additionalCritChance = additionalCritChance;
    }

    @Override
    public void ApplyAffect(Player p){
        //Increase the player's crit chance
        p.additionalCritChance += this.additionalCritChance;
    }
    @Override
    public EquipmentDrop getCopy(){
        return new CritChanceDrop(texture, upgradeName, upgradeDescription, additionalCritChance);
    }
}
