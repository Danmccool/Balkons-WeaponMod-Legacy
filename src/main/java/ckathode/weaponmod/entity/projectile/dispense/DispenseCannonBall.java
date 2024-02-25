package ckathode.weaponmod.entity.projectile.dispense;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.entity.projectile.EntityCannonBall;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;

public class DispenseCannonBall extends BehaviorDefaultDispenseItem {
    private final Random rand;
    private boolean normalDispense;

    public DispenseCannonBall() {
        this.rand = new Random();
        this.normalDispense = false;
    }

    @Nonnull
    @Override
    public ItemStack dispenseStack(final IBlockSource blocksource, @Nonnull final ItemStack itemstack) {
        boolean canfire = false;
        this.normalDispense = false;
        final TileEntity tileentity = blocksource.getWorld().getTileEntity(blocksource.getBlockPos());
        if (tileentity instanceof TileEntityDispenser) {
            final TileEntityDispenser dispenser = (TileEntityDispenser) tileentity;
            Item itemtocheck = null;
            if (itemstack.getItem() == Items.GUNPOWDER) {
                itemtocheck = BalkonsWeaponMod.cannonBall;
            } else if (itemstack.getItem() == BalkonsWeaponMod.cannonBall) {
                itemtocheck = Items.GUNPOWDER;
            }
            for (int i = 0; i < dispenser.getSizeInventory(); ++i) {
                final ItemStack itemstack2 = dispenser.getStackInSlot(i);
                if (!itemstack2.isEmpty() && itemstack2.getItem() == itemtocheck) {
                    dispenser.decrStackSize(i, 1);
                    canfire = true;
                    break;
                }
            }
        }
        if (!canfire) {
            this.normalDispense = true;
            return super.dispenseStack(blocksource, itemstack);
        }
        final EnumFacing face = blocksource.getBlockState().get(BlockDispenser.FACING);
        final double xvel = face.getXOffset() * 1.5;
        final double yvel = face.getYOffset() * 1.5;
        final double zvel = face.getZOffset() * 1.5;
        final IPosition pos = BlockDispenser.getDispensePosition(blocksource);
        final EntityCannonBall entitycannonball = new EntityCannonBall(blocksource.getWorld(), pos.getX() + xvel,
                pos.getY() + yvel, pos.getZ() + zvel);
        entitycannonball.shoot(xvel, yvel + 0.15, zvel, 2.0f, 2.0f);
        blocksource.getWorld().spawnEntity(entitycannonball);
        itemstack.split(1);
        return itemstack;
    }

    @Override
    protected void playDispenseSound(@Nonnull final IBlockSource blocksource) {
        if (this.normalDispense) {
            super.playDispenseSound(blocksource);
            return;
        }
        blocksource.getWorld().playSound(null, blocksource.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.NEUTRAL, 8.0f, 1.0f / (this.rand.nextFloat() * 0.8f + 0.9f));
        blocksource.getWorld().playSound(null, blocksource.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.NEUTRAL, 8.0f, 1.0f / (this.rand.nextFloat() * 0.4f + 0.6f));
    }

    @Override
    protected void spawnDispenseParticles(@Nonnull final IBlockSource blocksource, @Nonnull final EnumFacing face) {
        super.spawnDispenseParticles(blocksource, face);
        if (!this.normalDispense) {
            final IPosition pos = BlockDispenser.getDispensePosition(blocksource);
            blocksource.getWorld().addParticle(Particles.FLAME, pos.getX() + face.getXOffset(),
                    pos.getY() + face.getYOffset(), pos.getZ() + face.getZOffset(), 0.0, 0.0, 0.0);
        }
    }
}
