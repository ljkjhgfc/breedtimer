package com.breedtimer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.passive.AnimalEntity;

@Mixin(AnimalEntity.class)
public class AnimalEntityMixin {
    @Inject(method = "setLoveTicks", at = @At("HEAD"))
    private void onSetLoveTicks(int loveTicks, CallbackInfo ci) {
    }
}
