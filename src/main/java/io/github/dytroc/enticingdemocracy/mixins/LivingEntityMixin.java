package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.noFallDamage)) {
            if (source.isFromFalling()) cir.cancel();
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"), method = "applyDamage", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfo ci, float f, float g, float h) {
        if (source == null) return;


        if (EnticingDemocracy.hasTemporaryEvent(Events.returnedDamage) || EnticingDemocracy.hasTemporaryEvent(Events.vampire)) {
            Entity attacker = source.getAttacker();
            if (attacker instanceof PlayerEntity attackerPlayer) {
                float damage = (h - Math.max(0, h - amount));
                if (EnticingDemocracy.hasTemporaryEvent(Events.returnedDamage)) attackerPlayer.damage(DamageSource.thorns(this), damage);
                if (EnticingDemocracy.hasTemporaryEvent(Events.vampire)) attackerPlayer.heal(damage);
            }
        }
    }
}
