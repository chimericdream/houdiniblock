package com.chimericdream.houdiniblock.mixin;

import com.chimericdream.houdiniblock.blocks.HoudiniBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
abstract public class HoudiniWorldMixin {
    @Inject(
        method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;scheduleBlockRerenderIfNeeded(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void houdini$preventBlockUpdates(
        BlockPos pos,
        BlockState state,
        int flags,
        int maxUpdateDepth,
        CallbackInfoReturnable<Boolean> cir,
        @Local(ordinal = 0) Block newBlock,
        @Local(ordinal = 1) BlockState previousState
    ) {
        if ((
            previousState.getBlock() instanceof HoudiniBlock
                && !previousState.get(HoudiniBlock.PREVENT_ON_PLACE) // All states except prevent_on_place should prevent updates
                && newBlock instanceof AirBlock)
            || (
            previousState.getBlock() instanceof HoudiniBlock
                && previousState.get(HoudiniBlock.REPLACE_BLOCK))
        ) {
            cir.setReturnValue(false);
            return;
        }

        if ((
            previousState.getBlock() instanceof AirBlock
                && newBlock instanceof HoudiniBlock
                // All states except prevent_on_break should prevent updates
                && !state.get(HoudiniBlock.PREVENT_ON_BREAK))
            || (
            newBlock instanceof HoudiniBlock
                && state.get(HoudiniBlock.REPLACE_BLOCK))
        ) {
            cir.setReturnValue(false);
        }
    }
}
