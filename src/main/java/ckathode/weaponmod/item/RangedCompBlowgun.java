package ckathode.weaponmod.item;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.ReloadHelper;
import ckathode.weaponmod.entity.projectile.EntityBlowgunDart;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RangedCompBlowgun extends RangedComponent {
    public RangedCompBlowgun() {
        super(RangedSpecs.BLOWGUN);
    }

    @Override
    public void effectReloadDone(final ItemStack itemstack, final World world, final EntityPlayer entityplayer) {
        entityplayer.swingArm(EnumHand.MAIN_HAND);
        world.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ,
                SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.PLAYERS, 0.8f,
                1.0f / (this.weapon.getItemRand().nextFloat() * 0.4f + 0.4f));
    }

    @Override
    public void fire(final ItemStack itemstack, final World world, final EntityLivingBase entityliving, final int i) {
        final EntityPlayer entityplayer = (EntityPlayer) entityliving;
        final int j = this.getUseDuration(itemstack) - i;
        float f = j / 20.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        if (f < 0.1f) {
            return;
        }
        if (f > 1.0f) {
            f = 1.0f;
        }
        ItemStack dartstack = this.findAmmo(entityplayer);
        if (dartstack.isEmpty() && entityplayer.abilities.isCreativeMode) {
            dartstack = new ItemStack(BalkonsWeaponMod.darts.get(DartType.damage), 1);
        }
        if (!entityplayer.abilities.isCreativeMode
            && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) == 0) {
            dartstack.shrink(1);
            if (dartstack.isEmpty()) {
                entityplayer.inventory.deleteStack(dartstack);
            }
        }
        if (!world.isRemote) {
            final EntityBlowgunDart entityblowgundart = new EntityBlowgunDart(world, entityplayer);
            entityblowgundart.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0f,
                    f * 1.5f, 1.0f);
            Item item = dartstack.getItem();
            if (item instanceof ItemBlowgunDart)
                entityblowgundart.setDartEffectType(((ItemBlowgunDart) item).getDartType());
            this.applyProjectileEnchantments(entityblowgundart, itemstack);
            world.spawnEntity(entityblowgundart);
        }
        final int damage = 1;
        if (itemstack.getDamage() + damage <= itemstack.getMaxDamage()) {
            RangedComponent.setReloadState(itemstack, ReloadHelper.STATE_NONE);
        }
        itemstack.damageItem(damage, entityplayer);
        this.postShootingEffects(itemstack, entityplayer, world);
        RangedComponent.setReloadState(itemstack, ReloadHelper.STATE_NONE);
    }

    @Override
    public boolean hasAmmoAndConsume(final ItemStack itemstack, final World world, final EntityPlayer entityplayer) {
        return this.hasAmmo(itemstack, world, entityplayer);
    }

    @Override
    public void soundEmpty(final ItemStack itemstack, final World world, final EntityPlayer entityplayer) {
        world.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT
                , SoundCategory.PLAYERS, 1.0f, 1.0f / (this.weapon.getItemRand().nextFloat() * 0.2f + 0.5f));
    }

    @Override
    public void soundCharge(final ItemStack itemstack, final World world, final EntityPlayer entityplayer) {
        world.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ,
                SoundEvents.ENTITY_PLAYER_BREATH, SoundCategory.PLAYERS, 1.0f,
                1.0f / (this.weapon.getItemRand().nextFloat() * 0.4f + 0.8f));
    }

    @Override
    public void effectShoot(final World world, final double x, final double y, final double z, final float yaw,
                            final float pitch) {
        world.playSound(null, x, y, z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f,
                1.0f / (this.weapon.getItemRand().nextFloat() * 0.2f + 0.5f));
        final float particleX = -MathHelper.sin((yaw + 23.0f) * 0.017453292f) * MathHelper.cos(pitch * 0.017453292f);
        final float particleY = -MathHelper.sin(pitch * 0.017453292f) + 1.6f;
        final float particleZ = MathHelper.cos((yaw + 23.0f) * 0.017453292f) * MathHelper.cos(pitch * 0.017453292f);
        world.addParticle(Particles.POOF, x + particleX, y + particleY, z + particleZ, 0.0, 0.0, 0.0);
    }

    @Override
    public void effectPlayer(final ItemStack itemstack, final EntityPlayer entityplayer, final World world) {
    }

    @Override
    public float getMaxZoom() {
        return 0.1f;
    }
}
