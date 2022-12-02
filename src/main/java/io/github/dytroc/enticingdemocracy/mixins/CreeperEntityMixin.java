package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin extends HostileEntity {
    public CreeperEntityMixin(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("RETURN"), method = "explode")
    private void onExplode(CallbackInfo ci) {
        if (EnticingDemocracy.hasPermanentEvent(Events.creeperDuplication)) {
            if (Math.random() < 0.25) {
                for (int i = 0; i < 2; i++) {
                    CreeperEntity entity = new CreeperEntity(EntityType.CREEPER, world);
                    entity.setPosition(this.getPos());
                    entity.bodyYaw = bodyYaw;
                    entity.headYaw = headYaw;
                    double distance = i == 0 ? -0.1 : 0.1;
                    entity.addVelocity(distance, 0.05, distance);
                    world.spawnEntity(entity);
                }
            }
        }
    }

}
