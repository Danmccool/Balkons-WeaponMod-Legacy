package ckathode.weaponmod.entity.projectile;

import ckathode.weaponmod.WMDamageSources;
import ckathode.weaponmod.WMRegistries;
import ckathode.weaponmod.item.RangedComponent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityBlunderShot extends EntityProjectile<EntityBlunderShot> {

    public static final String ID = "shot";
    public static final EntityType<EntityBlunderShot> TYPE = WMRegistries.createEntityType(
            ID, new EntityDimensions(0.5f, 0.5f, false), EntityBlunderShot::new);

    public EntityBlunderShot(EntityType<EntityBlunderShot> entityType, Level world) {
        super(entityType, world);
        setPickupStatus(PickupStatus.DISALLOWED);
    }

    public EntityBlunderShot(Level world, double x, double y, double z) {
        this(TYPE, world);
        setPos(x, y, z);
    }

    public EntityBlunderShot(Level world, LivingEntity shooter) {
        this(world, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        setOwner(shooter);
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkManager.createAddEntityPacket(this);
    }

    @Override
    public void shootFromRotation(Entity entity, float f, float f1, float f2, float f3,
                                  float f4) {
        float x = -Mth.sin(f1 * 0.017453292f) * Mth.cos(f * 0.017453292f);
        float y = -Mth.sin(f * 0.017453292f);
        float z = Mth.cos(f1 * 0.017453292f) * Mth.cos(f * 0.017453292f);
        shoot(x, y, z, f3, f4);
        Vec3 entityMotion = entity.getDeltaMovement();
        setDeltaMovement(getDeltaMovement().add(entityMotion.x, entity.onGround() ? 0 : entityMotion.y,
                entityMotion.z));
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksInAir > 4) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void onEntityHit(Entity entity) {
        float damage = 4.0f + extraDamage;
        DamageSource damagesource = damageSources().source(WMDamageSources.WEAPON, this, getDamagingEntity());
        int prevhurtrestime = entity.invulnerableTime;
        if (entity.hurt(damagesource, damage)) {
            entity.invulnerableTime = prevhurtrestime;
            applyEntityHitEffects(entity);
            playHitSound();
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean aimRotation() {
        return false;
    }

    @Override
    public int getMaxLifetime() {
        return 200;
    }

    @Override
    public int getMaxArrowShake() {
        return 0;
    }

    @Override
    public float getGravity() {
        return (getTotalVelocity() < 2.0) ? 0.04f : 0.0f;
    }

    @NotNull
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(WMRegistries.ITEM_BLUNDER_SHOT.get());
    }

    public static void fireSpreadShot(Level world, LivingEntity entityliving,
                                      RangedComponent item, ItemStack itemstack) {
        Player entityplayer = (Player) entityliving;
        for (int i = 0; i < 10; ++i) {
            EntityBlunderShot entity = new EntityBlunderShot(world, entityliving);
            entity.shootFromRotation(entityplayer, entityplayer.getXRot(), entityplayer.getYRot(),
                    0.0f, 5.0f, 15.0f);
            if (item != null && !itemstack.isEmpty()) {
                item.applyProjectileEnchantments(entity, itemstack);
            }
            world.addFreshEntity(entity);
        }
    }

    public static void fireSpreadShot(Level world, double x, double y, double z) {
        for (int i = 0; i < 10; ++i) {
            world.addFreshEntity(new EntityBlunderShot(world, x, y, z));
        }
    }

    public static void fireFromDispenser(Level world, double d, double d1, double d2,
                                         int i, int j, int k) {
        for (int i2 = 0; i2 < 10; ++i2) {
            EntityBlunderShot entityblundershot = new EntityBlunderShot(world, d, d1, d2);
            entityblundershot.shoot(i, j, k, 5.0f, 15.0f);
            world.addFreshEntity(entityblundershot);
        }
    }

}
