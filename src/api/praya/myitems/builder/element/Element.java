// 
// Decompiled by Procyon v0.5.36
// 

package api.praya.myitems.builder.element;

import java.util.HashMap;

public class Element implements com.lymeowz.item.element.ElementDefinition
{
    private final String keyLore;
    private final ElementBoost boostBuild;
    private final ElementPotion potionBuild;
    private final HashMap<String, Double> resistance;
    
    public Element(final String keyLore, final ElementBoost boostBuild, final ElementPotion potionBuild, final HashMap<String, Double> resistance) {
        this.keyLore = keyLore;
        this.boostBuild = boostBuild;
        this.potionBuild = potionBuild;
        this.resistance = resistance;
    }
    
    public final String getKeyLore() {
        return this.keyLore;
    }
    
    public final ElementBoost getBoostBuild() {
        return this.boostBuild;
    }
    
    public final ElementPotion getPotionBuild() {
        return this.potionBuild;
    }
    
    public final HashMap<String, Double> getResistance() {
        return this.resistance;
    }

    @Override
    public final String loreKey() {
        return getKeyLore();
    }

    @Override
    public final double additionalDamageScale() {
        return getBoostBuild().getScaleBaseAdditionalDamage();
    }

    @Override
    public final double percentDamageScale() {
        return getBoostBuild().getScaleBasePercentDamage();
    }

    @Override
    public final java.util.Map<String, Double> resistanceSnapshot() {
        return java.util.Collections.unmodifiableMap(new HashMap<>(getResistance()));
    }
}
