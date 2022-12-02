package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity {

    @Shadow @Nullable private Entity owner;

    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "onCollision")
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        if (EnticingDemocracy.hasPermanentEvent(Events.explosiveArrows)) {
            if (getType().equals(EntityType.ARROW)) {
                if (hitResult.getType() == HitResult.Type.MISS) return;
                Vec3d pos = hitResult.getPos();
                world.createExplosion(null, owner instanceof LivingEntity ? DamageSource.explosion((LivingEntity) owner) : null, null, pos.x, pos.y, pos.z, 1.2f, true, Explosion.DestructionType.DESTROY);
                remove(RemovalReason.DISCARDED);
            }
        }
    }
}
