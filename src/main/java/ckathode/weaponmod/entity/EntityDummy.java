package ckathode.weaponmod.entity;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.WeaponDamageSource;
import ckathode.weaponmod.item.IItemWeapon;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityDummy extends Entity {
    public static final String NAME = "dummy";

    private static final DataParameter<Integer> TIME_SINCE_HIT;
    private static final DataParameter<Byte> ROCK_DIRECTION;
    private static final DataParameter<Integer> CURRENT_DAMAGE;
    private int durability;

    public EntityDummy(final World world) {
        super(BalkonsWeaponMod.entityDummy, world);
        this.preventEntitySpawning = true;
        this.rotationPitch = -20.0f;
        this.setRotation(this.rotationYaw, this.rotationPitch);
        this.setSize(0.5f, 1.9f);
        this.durability = 50;
    }

    public EntityDummy(final World world, final double d, final double d1, final double d2) {
        this(world);
        this.setPosition(d, d1, d2);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = d;
        this.prevPosY = d1;
        this.prevPosZ = d2;
    }

    @Override
    protected void registerData() {
        this.dataManager.register(EntityDummy.TIME_SINCE_HIT, 0);
        this.dataManager.register(EntityDummy.ROCK_DIRECTION, (byte) 1);
        this.dataManager.register(EntityDummy.CURRENT_DAMAGE, 0);
    }

    @Override
    public AxisAlignedBB getCollisionBox(final Entity entity) {
        return entity.getBoundingBox();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getBoundingBox();
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull final DamageSource damagesource, final float damage) {
        if (this.world.isRemote || !this.isAlive() || damage <= 0.0f) {
            return false;
        }
        this.setRockDirection(-this.getRockDirection());
        this.setTimeSinceHit(10);
        int i = this.getCurrentDamage();
        i += (int) (damage * 5.0f);
        if (i > 50) {
            i = 50;
        }
        this.setCurrentDamage(i);
        this.markVelocityChanged();
        if (!(damagesource instanceof EntityDamageSource)) {
            this.durability -= (int) damage;
        } else if (damagesource instanceof WeaponDamageSource) {
            final Entity entity = ((WeaponDamageSource) damagesource).getProjectile();
            if (MathHelper.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ) > 0.5) {
                entity.motionX *= 0.10000000149011612;
                entity.motionY *= 0.10000000149011612;
                entity.motionZ *= 0.10000000149011612;
                this.playRandomHitSound();
            } else {
                entity.motionX = this.rand.nextFloat() - 0.5f;
                entity.motionY = this.rand.nextFloat() - 0.5f;
                entity.motionZ = this.rand.nextFloat() - 0.5f;
            }
        } else {
            this.playRandomHitSound();
        }
        if (this.durability <= 0 && this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.dropAsItem(true, true);
        }
        this.markVelocityChanged();
        return false;
    }

    public void playRandomHitSound() {
        final int i = this.rand.nextInt(2);
        if (i == 0) {
            this.playSound(SoundEvents.BLOCK_WOOL_STEP, 0.7f, 1.0f / (this.rand.nextFloat() * 0.2f + 0.4f));
        } else {
            this.playSound(SoundEvents.BLOCK_WOOD_STEP, 0.7f, 1.0f / (this.rand.nextFloat() * 0.2f + 0.2f));
        }
    }

    @Override
    public void performHurtAnimation() {
        this.setRockDirection(-this.getRockDirection());
        this.setTimeSinceHit(10);
        this.setCurrentDamage(this.getCurrentDamage() + 10);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public void tick() {
        super.tick();
        int i = this.getTimeSinceHit();
        if (i > 0) {
            this.setTimeSinceHit(i - 1);
        }
        i = this.getCurrentDamage();
        if (i > 0) {
            this.setCurrentDamage(i - this.rand.nextInt(2));
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.onGround) {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
        } else {
            this.motionX *= 0.99;
            this.motionZ *= 0.99;
            this.motionY -= 0.05;
            this.fallDistance += (float) (-this.motionY);
        }
        this.setRotation(this.rotationYaw, this.rotationPitch);
        this.move(MoverType.SELF, 0.0, this.motionY, 0.0);
        final List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(0.2,
                0.0, 0.2), EntitySelectors.pushableBy(this));
        if (!list.isEmpty()) {
            for (final Entity entity : list) {
                if (!entity.isPassenger(this)) {
                    this.applyEntityCollision(entity);
                }
            }
        }
    }

    @Override
    public void fall(final float f, final float f1) {
        super.fall(f, f1);
        if (!this.onGround) {
            return;
        }
        final int i = MathHelper.floor(f);
        this.attackEntityFrom(DamageSource.FALL, (float) i);
    }

    public void dropAsItem(final boolean destroyed, final boolean noCreative) {
        if (this.world.isRemote) {
            return;
        }
        if (destroyed) {
            for (int i = 0; i < this.rand.nextInt(8); ++i) {
                this.entityDropItem(Items.LEATHER, 1);
            }
        } else if (noCreative) {
            this.entityDropItem(BalkonsWeaponMod.dummy, 1);
        }
        this.remove();
    }

    @Override
    public boolean processInitialInteract(final EntityPlayer entityplayer, @Nonnull final EnumHand hand) {
        final ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        if (!itemstack.isEmpty()) {
            if (itemstack.getItem() instanceof IItemWeapon || itemstack.getItem() instanceof ItemSword || itemstack.getItem() instanceof ItemBow || itemstack.getItem() instanceof ItemShield) {
                return false;
            }
        }
        if (entityplayer.abilities.isCreativeMode) {
            this.dropAsItem(false, false);
            return true;
        }
        this.dropAsItem(false, true);
        return true;
    }

    @Override
    protected void writeAdditional(@Nonnull final NBTTagCompound nbttagcompound) {
    }

    @Override
    protected void readAdditional(@Nonnull final NBTTagCompound nbttagcompound) {
        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(this.rotationYaw, this.rotationPitch);
    }

    public void setTimeSinceHit(final int i) {
        this.dataManager.set(EntityDummy.TIME_SINCE_HIT, i);
    }

    public void setRockDirection(final int i) {
        this.dataManager.set(EntityDummy.ROCK_DIRECTION, (byte) i);
    }

    public void setCurrentDamage(final int i) {
        this.dataManager.set(EntityDummy.CURRENT_DAMAGE, i);
    }

    public int getTimeSinceHit() {
        return this.dataManager.get(EntityDummy.TIME_SINCE_HIT);
    }

    public int getRockDirection() {
        return this.dataManager.get(EntityDummy.ROCK_DIRECTION);
    }

    public int getCurrentDamage() {
        return this.dataManager.get(EntityDummy.CURRENT_DAMAGE);
    }

    static {
        TIME_SINCE_HIT = EntityDataManager.createKey(EntityDummy.class, DataSerializers.VARINT);
        ROCK_DIRECTION = EntityDataManager.createKey(EntityDummy.class, DataSerializers.BYTE);
        CURRENT_DAMAGE = EntityDataManager.createKey(EntityDummy.class, DataSerializers.VARINT);
    }
}
