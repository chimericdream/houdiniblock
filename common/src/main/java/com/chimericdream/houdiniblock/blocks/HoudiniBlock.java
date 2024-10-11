package com.chimericdream.houdiniblock.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoudiniBlock extends Block implements Waterloggable {
    public static final BooleanProperty PREVENT_ON_PLACE;
    public static final BooleanProperty PREVENT_ON_BREAK;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    static {
        PREVENT_ON_PLACE = BooleanProperty.of("prevent_on_place");
        PREVENT_ON_BREAK = BooleanProperty.of("prevent_on_break");
    }

    public HoudiniBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(PREVENT_ON_PLACE, false)
                .with(PREVENT_ON_BREAK, true)
                .with(WATERLOGGED, false)
        );
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
            PREVENT_ON_PLACE,
            PREVENT_ON_BREAK,
            WATERLOGGED
        );
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
            .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!state.get(PREVENT_ON_BREAK)) {
            return super.onBreak(world, pos, state, player);
        }

        this.spawnBreakParticles(world, player, pos, state);

        if (!player.isCreative()) {
            ItemEntity itemEntity = new ItemEntity(
                world,
                (double) pos.getX() + 0.5D,
                (double) pos.getY() + 0.5D,
                (double) pos.getZ() + 0.5D,
                new ItemStack(this)
            );

            itemEntity.setToDefaultPickupDelay();

            world.spawnEntity(itemEntity);
        }

        return state;
    }
}
