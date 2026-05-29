package com.breedtimer.mixin;

import com.breedtimer.client.BreedTimerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public class AnimalEntityMixin {

    /**
     * Injects into setLoveTicks — called on both server and client when breeding is triggered.
     * We only care about the client side (local player interaction).
     */
    @Inject(method = "setLoveTicks", at = @At("HEAD"))
    private void onSetLoveTicks(int loveTicks, CallbackInfo ci) {
        // loveTicks > 0 means the animal just entered love mode (was just bred / given food)
        if (loveTicks <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;

        AnimalEntity self = (AnimalEntity)(Object)this;

        // Only trigger on client world
        if (self.getWorld().isClient()) {
            // Get a friendly display name for the mob type
            String mobName = self.getType().getName().getString();
            // Capitalize first letter
            if (!mobName.isEmpty()) {
                mobName = Character.toUpperCase(mobName.charAt(0)) + mobName.substring(1);
            }
            BreedTimerData.addTimer(mobName);
        }
    }

    /**
     * Tick the timer data each entity tick (we piggyback on animal tick).
     * We use a static flag so we only tick once per game tick regardless of how many animals exist.
     */
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        AnimalEntity self = (AnimalEntity)(Object)this;
        if (self.getWorld().isClient() && self.getWorld().getTime() != lastTickedWorldTime) {
            lastTickedWorldTime = self.getWorld().getTime();
            BreedTimerData.tick();
        }
    }

    private static long lastTickedWorldTime = -1;
}
