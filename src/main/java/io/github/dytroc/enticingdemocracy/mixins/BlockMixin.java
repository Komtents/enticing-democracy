package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(at = @At("RETURN"), method = "getSlipperiness", cancellable = true)
    private void getSlipperiness(CallbackInfoReturnable<Float> cir) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.slipperyBlocks)) {
            cir.setReturnValue(1.1f);
        }
    }
}
