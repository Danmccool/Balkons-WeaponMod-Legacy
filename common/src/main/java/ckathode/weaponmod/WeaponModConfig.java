package ckathode.weaponmod;

import java.util.HashMap;
import java.util.Map;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import static ckathode.weaponmod.BalkonsWeaponMod.MOD_ID;

@Config(name = MOD_ID)
public class WeaponModConfig implements ConfigData {

    @Category("settings")
    @Comment("Whether the cannon should do block damage.")
    public final boolean cannonDoesBlockDamage = true;
    @Category("settings")
    @Comment("Whether dynamite should do block damage.")
    public final boolean dynamiteDoesBlockDamage = true;
    @Category("settings")
    @Comment("Whether the mortar should do block damage.")
    public final boolean mortarDoesBlockDamage = true;
    @Category("settings")
    @Comment("Whether the knife can be thrown.")
    public final boolean canThrowKnife = true;
    @Category("settings")
    @Comment("Whether the spear can be thrown.")
    public final boolean canThrowSpear = true;
    @Category("settings")
    @Comment("Change this to 'false' to allow only the thrower/shooter of the projectile to " +
             "pick the item up. If set to 'true' everyone can pick the item up.")
    public final boolean allCanPickup = true;
    @Category("settings")
    @Comment("Show reload progress in hotbar.")
    public final boolean guiOverlayReloaded = true;
    @Category("settings")
    @Comment("Item model for entity (knife, spear, etc).")
    public final boolean itemModelForEntity = true;

    @Category("enable")
    @Comment("Enable/disable recipes for the different weapons (only works in Forge).")
    private final Map<String, Boolean> enableSettings = new HashMap<>();
    @Category("reloadTimes")
    @Comment("Change the reload times of the different weapons.")
    private final Map<String, Integer> reloadTimeSettings = new HashMap<>();

    private WeaponModConfig() {
        addEnableSetting("spear");
        addEnableSetting("halberd");
        addEnableSetting("battleaxe");
        addEnableSetting("knife");
        addEnableSetting("warhammer");
        addEnableSetting("flail");
        addEnableSetting("katana");
        addEnableSetting("boomerang");
        addEnableSetting("firerod");
        addEnableSetting("javelin");
        addEnableSetting("crossbow");
        addEnableSetting("blowgun");
        addEnableSetting("musket");
        addEnableSetting("blunderbuss");
        addEnableSetting("flintlock");
        addEnableSetting("dynamite");
        addEnableSetting("cannon");
        addEnableSetting("dummy");
        addEnableSetting("mortar");

        addReloadTimeSetting("musket", 30);
        addReloadTimeSetting("crossbow", 15);
        addReloadTimeSetting("blowgun", 10);
        addReloadTimeSetting("blunderbuss", 20);
        addReloadTimeSetting("flintlock", 15);
        addReloadTimeSetting("mortar", 50);
    }

    private void addEnableSetting(String weapon) {
        enableSettings.put(weapon, true);
    }

    public boolean isEnabled(String weapon) {
        Boolean enabled = enableSettings.get(weapon);
        return enabled == null || enabled;
    }

    private void addReloadTimeSetting(String weapon, int defaultTime) {
        reloadTimeSettings.put(weapon, defaultTime);
    }

    public int getReloadTime(String weapon) {
        Integer time = reloadTimeSettings.get(weapon);
        return (time == null) ? 0 : time;
    }

    public static WeaponModConfig get() {
        return AutoConfig.getConfigHolder(WeaponModConfig.class).getConfig();
    }

    public static void init() {
        AutoConfig.register(WeaponModConfig.class, JanksonConfigSerializer::new);
    }

}
