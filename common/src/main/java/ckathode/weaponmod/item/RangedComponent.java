package ckathode.weaponmod.item;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.ReloadHelper;
import ckathode.weaponmod.ReloadHelper.ReloadState;
import ckathode.weaponmod.WeaponModAttributes;
import ckathode.weaponmod.WeaponModConfig;
import ckathode.weaponmod.entity.projectile.EntityProjectile;
import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RangedComponent extends AbstractWeaponComponent {

    protected static final int MAX_DELAY = 72000;
    public final RangedSpecs rangedSpecs;

    public static boolean isReloaded(ItemStack itemstack) {
        return ReloadHelper.getReloadState(itemstack).isReloaded();
    }

    public static boolean isReadyToFire(ItemStack itemstack) {
        return ReloadHelper.getReloadState(itemstack) == ReloadState.STATE_READY;
    }

    public static void setReloadState(ItemStack itemstack, ReloadState state) {
        ReloadHelper.setReloadState(itemstack, state);
    }

    public RangedComponent(RangedSpecs rangedspecs) {
        rangedSpecs = rangedspecs;
    }

    @Override
    protected void onSetItem() {
    }

    @Override
    public Properties setProperties(Properties properties) {
        return properties.defaultDurability(rangedSpecs.durability);
    }

    @Override
    public float getDamage() {
        return 0.0f;
    }

    @Override
    public float getEntityDamage() {
        return 0.0f;
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, BlockState block) {
        return 0.0f;
    }

    @Override
    public boolean canHarvestBlock(BlockState block) {
        return false;
    }

    @Override
    public boolean mineBlock(ItemStack itemstack, Level world, BlockState block,
                             BlockPos pos, LivingEntity entityliving) {
        return false;
    }

    @Override
    public boolean hurtEnemy(ItemStack itemstack, LivingEntity entityliving,
                             LivingEntity attacker) {
        return false;
    }

    @Override
    public float getAttackDelay(ItemStack itemstack, LivingEntity entityliving,
                                LivingEntity attacker) {
        return 0.0f;
    }

    @Override
    public float getKnockBack(ItemStack itemstack, LivingEntity entityliving,
                              LivingEntity attacker) {
        return 0.0f;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public void addItemAttributeModifiers(Multimap<Attribute, AttributeModifier> multimap) {
        multimap.put(WeaponModAttributes.RELOAD_TIME, new AttributeModifier(IItemWeapon.RELOAD_TIME_MODIFIER,
                "Weapon reloadtime modifier", rangedSpecs.getReloadTime(), AttributeModifier.Operation.ADDITION));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack itemstack, Player player, Entity entity) {
        return false;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        ReloadState state = ReloadHelper.getReloadState(itemstack);
        if (state == ReloadState.STATE_READY) {
            return UseAnim.BOW;
        }
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return MAX_DELAY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player entityplayer,
                                                  InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);
        if (itemstack.isEmpty() || entityplayer.isUsingItem()) {
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
        }
        if (!hasAmmo(itemstack, world, entityplayer)) {
            soundEmpty(itemstack, world, entityplayer);
            setReloadState(itemstack, ReloadState.STATE_NONE);
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
        }
        if (isReadyToFire(itemstack)) {
            soundCharge(itemstack, world, entityplayer);
            entityplayer.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        }
        entityplayer.startUsingItem(hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    @Override
    public void onUsingTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        Player entityplayer = (Player) livingEntity;
        if (ReloadHelper.getReloadState(stack) == ReloadState.STATE_NONE
            && getUseDuration(stack) - remainingUseDuration >= getReloadDuration(stack)) {
            effectReloadDone(stack, entityplayer.level(), entityplayer);
            setReloadState(stack, ReloadState.STATE_RELOADED);
        }
    }

    @Override
    public void releaseUsing(ItemStack itemstack, Level world,
                             LivingEntity entityliving, int i) {
        Player entityplayer = (Player) entityliving;
        if (!isReloaded(itemstack)) {
            return;
        }
        if (isReadyToFire(itemstack)) {
            if (hasAmmoAndConsume(itemstack, world, entityplayer)) {
                fire(itemstack, world, entityplayer, i);
            }
            setReloadState(itemstack, ReloadState.STATE_NONE);
        } else {
            setReloadState(itemstack, ReloadState.STATE_READY);
        }
    }

    @Override
    public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int i,
                              boolean flag) {
    }

    public void soundEmpty(ItemStack itemstack, Level world, Player entityplayer) {
        world.playSound(null, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(),
                SoundEvents.COMPARATOR_CLICK, SoundSource.PLAYERS, 1.0f, 1.25f);
    }

    public void soundCharge(ItemStack itemstack, Level world, Player entityplayer) {
    }

    public void postShootingEffects(ItemStack itemstack, Player entityplayer,
                                    Level world) {
        effectPlayer(itemstack, entityplayer, world);
        effectShoot(world, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(), entityplayer.getYRot(),
                entityplayer.getXRot());
    }

    public abstract void effectReloadDone(ItemStack p0, Level p1, Player p2);

    public abstract void fire(ItemStack p0, Level p1, LivingEntity p2, int p3);

    public abstract void effectPlayer(ItemStack p0, Player p1, Level p2);

    public abstract void effectShoot(Level p0, double p1, double p2, double p3,
                                     float p4, float p5);

    public void applyProjectileEnchantments(EntityProjectile<?> entity, ItemStack itemstack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemstack) > 0) {
            entity.setPickupStatus(EntityProjectile.PickupStatus.DISALLOWED);
        }
        int damage = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemstack);
        if (damage > 0) {
            entity.setExtraDamage((float) damage);
        }
        int knockback = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemstack);
        if (knockback > 0) {
            entity.setKnockback(knockback);
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemstack) > 0) {
            entity.setSecondsOnFire(100);
        }
    }

    public int getReloadDuration(ItemStack itemstack) {
        return rangedSpecs.getReloadTime();
    }

    public List<Item> getAmmoItems() {
        return rangedSpecs.getAmmoItems();
    }

    protected ItemStack findAmmo(Player entityplayer) {
        if (isAmmo(entityplayer.getItemInHand(InteractionHand.OFF_HAND))) {
            return entityplayer.getItemInHand(InteractionHand.OFF_HAND);
        }
        if (isAmmo(entityplayer.getItemInHand(InteractionHand.MAIN_HAND))) {
            return entityplayer.getItemInHand(InteractionHand.MAIN_HAND);
        }
        for (int i = 0; i < entityplayer.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack = entityplayer.getInventory().getItem(i);
            if (isAmmo(itemstack)) {
                return itemstack;
            }
        }
        return ItemStack.EMPTY;
    }

    protected boolean isAmmo(ItemStack stack) {
        return getAmmoItems().contains(stack.getItem());
    }

    protected boolean consumeAmmo(Player entityplayer) {
        ItemStack itemAmmo = findAmmo(entityplayer);
        if (itemAmmo.isEmpty()) {
            return false;
        }
        itemAmmo.shrink(1);
        if (itemAmmo.isEmpty()) {
            entityplayer.getInventory().removeItem(itemAmmo);
        }
        return true;
    }

    public boolean hasAmmoAndConsume(ItemStack itemstack, Level world, Player entityplayer) {
        return entityplayer.isCreative() || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS,
                itemstack) > 0 || consumeAmmo(entityplayer);
    }

    public boolean hasAmmo(ItemStack itemstack, Level world, Player entityplayer) {
        boolean flag = !findAmmo(entityplayer).isEmpty();
        return entityplayer.isCreative() || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS,
                itemstack) > 0 || flag;
    }

    public float getFOVMultiplier(int ticksinuse) {
        float f1 = ticksinuse / getMaxAimTimeTicks();
        if (f1 > 1.0f) {
            f1 = 1.0f;
        } else {
            f1 *= f1;
        }
        return 1.0f - f1 * getMaxZoom();
    }

    protected float getMaxAimTimeTicks() {
        return 20.0f;
    }

    protected float getMaxZoom() {
        return 0.15f;
    }

    public enum RangedSpecs {
        BLOWGUN("blowgun", 250, Arrays.stream(DartType.dartTypes)
                .filter(Objects::nonNull).map(t -> t.typeName).toArray(String[]::new)),
        CROSSBOW("crossbow", 250, "bolt"),
        MUSKET("musket", 80, "bullet"),
        BLUNDERBUSS("blunderbuss", 80, "shot"),
        FLINTLOCK("flintlock", 8, "bullet"),
        MORTAR("mortar", 40, "shell");

        private int reloadTime;
        private List<Item> ammoItems;
        private final String[] ammoItemTags;
        public final String reloadTimeTag;
        public final int durability;

        RangedSpecs(String reloadtimetag, int durability, String... ammoitemtags) {
            ammoItemTags = ammoitemtags;
            reloadTimeTag = reloadtimetag;
            this.durability = durability;
            ammoItems = null;
            reloadTime = -1;
        }

        public int getReloadTime() {
            if (reloadTime < 0) {
                reloadTime = WeaponModConfig.get().getReloadTime(reloadTimeTag);
                BalkonsWeaponMod.LOGGER.debug("Found reload time {} for {} @{}",
                        reloadTime, reloadTimeTag, this);
            }
            return reloadTime;
        }

        public List<Item> getAmmoItems() {
            if (ammoItems == null) {
                ammoItems = Arrays.stream(ammoItemTags)
                        .map(t -> BuiltInRegistries.ITEM.get(new ResourceLocation(BalkonsWeaponMod.MOD_ID, t)))
                        .collect(Collectors.toList());
                BalkonsWeaponMod.LOGGER.debug("Found items {} for {} @{}",
                        ammoItems, Arrays.toString(ammoItemTags), this);
            }
            return ammoItems;
        }
    }

}
