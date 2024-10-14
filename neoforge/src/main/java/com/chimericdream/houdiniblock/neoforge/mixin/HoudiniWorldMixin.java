package com.chimericdream.houdiniblock.neoforge.mixin;

import com.chimericdream.houdiniblock.blocks.HoudiniBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
abstract public class HoudiniWorldMixin {
    @Shadow
    abstract public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState oldState, BlockState newState);

    @Inject(
        method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;markAndNotifyBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;II)V"
        ),
        cancellable = true
    )
    private void houdini$preventBlockUpdates(
        BlockPos pos,
        BlockState newState,
        int flags,
        int maxUpdateDepth,
        CallbackInfoReturnable<Boolean> cir,
        @Local(ordinal = 0) Block newBlock,
        @Local(ordinal = 1) BlockState previousState
    ) {
        if ((
            previousState.getBlock() instanceof HoudiniBlock
                && !previousState.get(HoudiniBlock.PREVENT_ON_PLACE) // All states except prevent_on_place should prevent updates
                && (newBlock instanceof AirBlock || newBlock instanceof FluidBlock))
            || (
            previousState.getBlock() instanceof HoudiniBlock
                && previousState.get(HoudiniBlock.REPLACE_BLOCK))
        ) {
            this.scheduleBlockRerenderIfNeeded(pos, newState, previousState);
            cir.setReturnValue(false);
            return;
        }

        if ((
            (previousState.getBlock() instanceof AirBlock || previousState.getBlock() instanceof FluidBlock)
                && newBlock instanceof HoudiniBlock
                // All states except prevent_on_break should prevent updates
                && !newState.get(HoudiniBlock.PREVENT_ON_BREAK))
            || (
            newBlock instanceof HoudiniBlock
                && newState.get(HoudiniBlock.REPLACE_BLOCK))
        ) {
            this.scheduleBlockRerenderIfNeeded(pos, newState, previousState);
            cir.setReturnValue(false);
        }
    }
}
