package ckathode.weaponmod.entity.projectile;

import ckathode.weaponmod.WMDamageSources;
import ckathode.weaponmod.WMRegistries;
import ckathode.weaponmod.item.DartType;
import ckathode.weaponmod.item.ItemBlowgunDart;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityBlowgunDart extends EntityProjectile<EntityBlowgunDart> {

    private static final EntityDataAccessor<Byte> DART_EFFECT_TYPE = SynchedEntityData.defineId(EntityBlowgunDart.class,
            EntityDataSerializers.BYTE);
    private static final float[][] DART_COLORS = new float[][]{{0.2f, 0.8f, 0.3f}, {0.9f, 0.7f, 1.0f},
            {0.6f, 1.0f, 0.9f}, {0.8f, 0.5f, 0.2f}};

    public static final String ID = "dart";
    public static final EntityType<EntityBlowgunDart> TYPE = WMRegistries.createEntityType(
            ID, new EntityDimensions(0.5f, 0.5f, false), EntityBlowgunDart::new);

    public EntityBlowgunDart(EntityType<EntityBlowgunDart> entityType, Level world) {
        super(entityType, world);
    }

    public EntityBlowgunDart(Level world, double d, double d1, double d2) {
        this(TYPE, world);
        setPickupStatus(PickupStatus.ALLOWED);
        setPos(d, d1, d2);
    }

    public EntityBlowgunDart(Level world, LivingEntity shooter) {
        this(world, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        setOwner(shooter);
        setPickupStatusFromEntity(shooter);
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
    public void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DART_EFFECT_TYPE, (byte) 0);
    }

    public void setDartEffectType(DartType type) {
        setDartEffectType(type.typeID);
    }

    public void setDartEffectType(byte i) {
        entityData.set(DART_EFFECT_TYPE, i);
    }

    public DartType getDartEffectType() {
        return DartType.dartTypes[getDartEffectId()];
    }

    public byte getDartEffectId() {
        return entityData.get(DART_EFFECT_TYPE);
    }

    public float[] getDartColor() {
        return DART_COLORS[getDartEffectId()];
    }

    @Override
    public void onEntityHit(Entity entity) {
        DamageSource damagesource = damageSources().source(WMDamageSources.WEAPON, this, getDamagingEntity());
        if (entity.hurt(damagesource, 1.0f + extraDamage)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(DartType.dartTypes[getDartEffectId()].potionEffect));
            }
            applyEntityHitEffects(entity);
            playHitSound();
            remove(RemovalReason.DISCARDED);
        } else {
            bounceBack();
        }
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public void playHitSound() {
        playSound(SoundEvents.ARROW_HIT, 1.0f, 1.2f / (random.nextFloat() * 0.2f + 0.2f));
    }

    @Override
    public int getMaxArrowShake() {
        return 4;
    }

    @NotNull
    @Override
    public ItemStack getPickupItem() {
        return new ItemStack(ItemBlowgunDart.ITEMS.get(getDartEffectType()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putByte("darttype", getDartEffectId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        setDartEffectType(nbttagcompound.getByte("darttype"));
    }

}
