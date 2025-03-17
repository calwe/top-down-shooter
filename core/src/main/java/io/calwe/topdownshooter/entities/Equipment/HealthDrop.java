package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Player;

public class HealthDrop extends EquipmentDrop{
    int healthIncreaseAmount;
    public HealthDrop(Texture texture, String upgradeName, String upgradeDescription, int healthIncreaseAmount){
        super(texture, upgradeName, upgradeDescription);
        this.healthIncreaseAmount = healthIncreaseAmount;
    }

    @Override
    public void ApplyAffect(Player p){
        p.maxHealth += healthIncreaseAmount;
        p.heal(healthIncreaseAmount);
    }
    @Override
    public EquipmentDrop getCopy(){
        return new HealthDrop(texture, upgradeName, upgradeDescription, healthIncreaseAmount);
    }
}
