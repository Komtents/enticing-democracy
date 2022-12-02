package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
abstract class ItemMixin {
    @Shadow @Nullable public abstract FoodComponent getFoodComponent();

    @Inject(at = @At("RETURN"), method = "isFood", cancellable = true)
    private void isFood(CallbackInfoReturnable<Boolean> cir) {
        if (getFoodComponent() != null) {
            if (EnticingDemocracy.hasTemporaryEvent(Events.fast)) {
                cir.setReturnValue(false);
            } else if (EnticingDemocracy.hasPermanentEvent(Events.noMeat)) {
                if (getFoodComponent().isMeat() || isFish(getFoodComponent())) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    private boolean isFish(FoodComponent food) {
        return food.equals(FoodComponents.SALMON) || food.equals(FoodComponents.COOKED_SALMON)
                || food.equals(FoodComponents.COD) || food.equals(FoodComponents.COOKED_COD)
                || food.equals(FoodComponents.TROPICAL_FISH) || food.equals(FoodComponents.PUFFERFISH);
    }
}