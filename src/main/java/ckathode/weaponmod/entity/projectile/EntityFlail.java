package ckathode.weaponmod.entity.projectile;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.PlayerWeaponData;
import ckathode.weaponmod.WeaponDamageSource;
import ckathode.weaponmod.item.ItemFlail;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class EntityFlail extends EntityMaterialProjectile<EntityFlail> {
    public static final String NAME = "flail";

    public boolean isSwinging;
    private float flailDamage;
    private Vector3d distance;

    public EntityFlail(EntityType<EntityFlail> entityType, World world) {
        super(entityType, world);
        noCulling = true;
        flailDamage = 1.0f;
        distance = Vector3d.ZERO;
    }

    public EntityFlail(World world, double d, double d1, double d2) {
        this(BalkonsWeaponMod.entityFlail, world);
        setPos(d, d1, d2);
    }

    public EntityFlail(World worldIn, LivingEntity shooter, ItemStack itemstack) {
        this(worldIn, shooter.getX(), shooter.getEyeY() - 0.3, shooter.getZ());
        setOwner(shooter);
        setPickupStatusFromEntity(shooter);
        setThrownItemStack(itemstack);
    }

    @Override
    public void shootFromRotation(Entity entity, float f, float f1, float f2, float f3, float f4) {
        Vector3d entityMotion = entity.getDeltaMovement();
        setDeltaMovement(getDeltaMovement().add(entityMotion.x, entity.isOnGround() ? 0 : entityMotion.y,
                entityMotion.z));
        swing(f, f1, f3, f4);
    }

    @Override
    public void tick() {
        super.tick();
        Entity shooter = getOwner();
        if (shooter != null) {
            distance = shooter.position().subtract(position());
            if (distance.lengthSqr() > 9.0) {
                returnToOwner(true);
            }
            if (shooter instanceof PlayerEntity) {
                ItemStack itemstack = ((PlayerEntity) shooter).getMainHandItem();
                if (itemstack.isEmpty() || (thrownItem != null && itemstack.getItem() != thrownItem.getItem()) || !shooter.isAlive()) {
                    pickUpByOwner();
                }
            }
        } else if (!level.isClientSide) {
            remove();
        }
        if (inGround) {
            inGround = false;
            return;
        }
        returnToOwner(false);
    }

    public void returnToOwner(boolean looseFromGround) {
        if (looseFromGround) {
            inGround = false;
        }
        Entity shooter = getOwner();
        if (shooter == null) {
            return;
        }
        double targetPosX = shooter.getX();
        double targetPosY = shooter.getBoundingBox().minY + 0.4000000059604645;
        double targetPosZ = shooter.getZ();
        float f = 27.0f;
        float f2 = 2.0f;
        targetPosX += -Math.sin((shooter.yRot + f) * 0.017453292f) * Math.cos(shooter.xRot * 0.017453292f) * f2;
        targetPosZ += Math.cos((shooter.yRot + f) * 0.017453292f) * Math.cos(shooter.xRot * 0.017453292f) * f2;
        distance = new Vector3d(targetPosX, targetPosY, targetPosZ).subtract(position());
        double distanceTotalSqr = distance.lengthSqr();
        if (distanceTotalSqr > 9.0) {
            setPos(targetPosX, targetPosY, targetPosZ);
        } else if (distanceTotalSqr > 6.25) {
            isSwinging = false;
            setDeltaMovement(getDeltaMovement().scale(-0.5));
        }
        if (!isSwinging) {
            float f3 = 0.2f;
            setDeltaMovement(distance.scale(f3 * MathHelper.sqrt(distanceTotalSqr)));
        }
    }

    public void pickUpByOwner() {
        remove();
        Entity shooter = getOwner();
        if (shooter instanceof PlayerEntity && thrownItem != null) {
            PlayerWeaponData.setFlailThrown((PlayerEntity) shooter, false);
        }
    }

    public void swing(float f, float f1, float f2, float f3) {
        if (isSwinging) {
            return;
        }
        playSound(SoundEvents.ARROW_SHOOT, 0.5f, 0.4f / (random.nextFloat() * 0.4f + 0.8f));
        float x = -MathHelper.sin(f1 * 0.017453292f) * MathHelper.cos(f * 0.017453292f);
        float y = -MathHelper.sin(f * 0.017453292f);
        float z = MathHelper.cos(f1 * 0.017453292f) * MathHelper.cos(f * 0.017453292f);
        shoot(x, y, z, f2, f3);
        isSwinging = true;
        inGround = false;
    }

    @Override
    public void onEntityHit(Entity entity) {
        if (entity.equals(getOwner())) {
            return;
        }
        Entity shooter = getDamagingEntity();
        DamageSource damagesource;
        if (shooter instanceof LivingEntity) {
            damagesource = DamageSource.mobAttack((LivingEntity) shooter);
        } else {
            damagesource = WeaponDamageSource.causeProjectileWeaponDamage(this, shooter);
        }
        if (entity.hurt(damagesource, flailDamage + extraDamage)) {
            playHitSound();
            returnToOwner(true);
        } else {
            bounceBack();
        }
    }

    @Override
    public void bounceBack() {
        setDeltaMovement(getDeltaMovement().scale(-0.8));
        yRot += 180.0f;
        yRotO += 180.0f;
        ticksInAir = 0;
    }

    @Override
    public void playHitSound() {
        if (inGround) {
            return;
        }
        playSound(SoundEvents.PLAYER_HURT, 1.0f, random.nextFloat() * 0.4f + 0.8f);
    }

    @Override
    public void setThrownItemStack(@Nonnull ItemStack itemstack) {
        if (!(itemstack.getItem() instanceof ItemFlail)) {
            return;
        }
        super.setThrownItemStack(itemstack);
        flailDamage = ((ItemFlail) itemstack.getItem()).getFlailDamage();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putFloat("fDmg", flailDamage);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        flailDamage = nbttagcompound.getFloat("fDmg");
    }

    @Override
    public void playerTouch(@Nonnull PlayerEntity entityplayer) {
    }

    @Override
    public int getMaxArrowShake() {
        return 0;
    }
}
