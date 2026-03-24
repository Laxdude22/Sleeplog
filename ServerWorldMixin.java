package com.example.sleeplog.mixin;

import com.example.sleeplog.SleepLogMod;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Unique private String sleeplog$lastSleepingNames = "";
    @Unique private int sleeplog$lastSleepingCount = 0;
    @Unique private boolean sleeplog$pendingNightSkip = false;
    @Unique private long sleeplog$previousTimeOfDay = -1L;

    @Inject(method = "updateSleepingPlayers", at = @At("TAIL"))
    private void sleeplog$afterUpdateSleepingPlayers(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;

        if (!world.getRegistryKey().equals(World.OVERWORLD)) {
            return;
        }

        if (!world.isNight()) {
            return;
        }

        List<ServerPlayerEntity> sleepingPlayers = world.getPlayers().stream()
                .filter(ServerPlayerEntity::isSleeping)
                .collect(Collectors.toList());

        int sleepingCount = sleepingPlayers.size();
        int totalPlayers = world.getPlayers().size();

        String sleepingNames = sleepingPlayers.stream()
                .map(player -> player.getName().getString())
                .collect(Collectors.joining(", "));

        if (sleepingCount > 0) {
            boolean changed = sleepingCount != this.sleeplog$lastSleepingCount
                    || !sleepingNames.equals(this.sleeplog$lastSleepingNames);

            if (changed) {
                SleepLogMod.LOGGER.info(
                        "[SleepLog] Sleeping now: {}/{} - {}",
                        sleepingCount,
                        totalPlayers,
                        sleepingNames
                );
            }

            this.sleeplog$lastSleepingCount = sleepingCount;
            this.sleeplog$lastSleepingNames = sleepingNames;
            this.sleeplog$pendingNightSkip = true;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void sleeplog$afterTick(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;

        if (!world.getRegistryKey().equals(World.OVERWORLD)) {
            return;
        }

        long currentTime = world.getTimeOfDay() % 24000L;

        if (this.sleeplog$previousTimeOfDay != -1L && this.sleeplog$pendingNightSkip) {
            long previousTime = this.sleeplog$previousTimeOfDay % 24000L;

            boolean wrappedToMorning = previousTime > currentTime;
            boolean nowDay = !world.isNight();

            if (wrappedToMorning && nowDay && this.sleeplog$lastSleepingCount > 0) {
                int totalPlayers = world.getPlayers().size();

                SleepLogMod.LOGGER.info(
                        "[SleepLog] Night skipped ({}/{} sleeping): {}",
                        this.sleeplog$lastSleepingCount,
                        totalPlayers,
                        this.sleeplog$lastSleepingNames
                );

                this.sleeplog$pendingNightSkip = false;
                this.sleeplog$lastSleepingCount = 0;
                this.sleeplog$lastSleepingNames = "";
            }
        }

        this.sleeplog$previousTimeOfDay = world.getTimeOfDay();
    }
}
