package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import io.calwe.topdownshooter.entities.Player;

public class HealthDrop extends EquipmentDrop{
    final int healthIncreaseAmount;
    public HealthDrop(Texture texture, String upgradeName, String upgradeDescription, int healthIncreaseAmount){
        super(texture, upgradeName, upgradeDescription);
        this.healthIncreaseAmount = healthIncreaseAmount;
    }

    @Override
    public void ApplyAffect(Player p){
        //Increase the player's max health and current health by healthIncreaseAmount
        p.maxHealth += healthIncreaseAmount;
        p.heal(healthIncreaseAmount);
    }
    @Override
    public EquipmentDrop getCopy(){
        return new HealthDrop(texture, upgradeName, upgradeDescription, healthIncreaseAmount);
    }
}
