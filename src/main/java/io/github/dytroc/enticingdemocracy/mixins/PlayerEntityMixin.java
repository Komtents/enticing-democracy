package io.github.dytroc.enticingdemocracy.mixins;

import com.mojang.datafixers.util.Either;
import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    public PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "jump", cancellable = true)
    private void onJump(CallbackInfo ci) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.jumpingIncapability)) {
            ci.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "tickMovement")
    private void tickMovement(CallbackInfo ci) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.burnsInDaylight)) {
            if (isAffectedByDaylight()) {
                ItemStack itemStack = getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                } else setOnFireFor(8);
            }
        }
    }

    protected boolean isAffectedByDaylight() {
        if (this.world.isDay() && !this.world.isClient) {
            BlockPos blockPos = new BlockPos(this.getX(), this.getEyeY(), this.getZ());
            boolean bl = this.isWet() || this.inPowderSnow || this.wasInPowderSnow;
            return !bl && this.world.isSkyVisible(blockPos);
        }

        return false;
    }

    @Inject(at = @At("HEAD"), method = "trySleep", cancellable = true)
    private void onSleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        if (EnticingDemocracy.hasPermanentEvent(Events.insomnia)) cir.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.OTHER_PROBLEM));
    }

    @Inject(at = @At("RETURN"), method = "getAttackCooldownProgress", cancellable = true)
    private void getAttackCooldownProgress(float baseTime, CallbackInfoReturnable<Float> cir) {
        if (EnticingDemocracy.hasPermanentEvent(Events.highAttackSpeed)) cir.setReturnValue(1f);
    }
}