package com.chimericdream.houdiniblock.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HoudiniBlockItem extends BlockItem {
    public static final NbtComponent DEFAULT_NBT;

    static {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("houdini_placement_mode", PlacementMode.PREVENT_ON_BREAK.toString());

        DEFAULT_NBT = NbtComponent.of(nbt);
    }

    public HoudiniBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!player.isSneaking()) {
            return TypedActionResult.pass(player.getStackInHand(hand));
        }

        try {
            ItemStack itemStack = player.getStackInHand(hand);
            NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, DEFAULT_NBT).copyNbt();

            PlacementMode currentMode = PlacementMode.valueOf(nbt.getString("houdini_placement_mode"));
            PlacementMode newMode = switch (currentMode) {
                case PREVENT_ON_BREAK -> PlacementMode.PREVENT_ON_PLACE;
                case PREVENT_ON_PLACE -> PlacementMode.REPLACE_BLOCK;
                case REPLACE_BLOCK -> PlacementMode.PREVENT_ON_BREAK;
            };

            nbt.putString("houdini_placement_mode", newMode.toString());
            itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

            if (!world.isClient()) {
                player.sendMessage(Text.of(newMode.getMessage()), true);
            }

            return TypedActionResult.pass(player.getStackInHand(hand));
        } catch (IllegalArgumentException e) {
            return TypedActionResult.fail(player.getStackInHand(hand));
        }
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, DEFAULT_NBT).copyNbt();
        PlacementMode mode = PlacementMode.valueOf(nbt.getString("houdini_placement_mode"));

        if (mode == PlacementMode.REPLACE_BLOCK) {
            BlockState target = context.getWorld().getBlockState(context.getBlockPos());

            return ActionResult.PASS;
        }

        return super.useOnBlock(context);
    }

    public enum PlacementMode {
        PREVENT_ON_BREAK,
        PREVENT_ON_PLACE,
        REPLACE_BLOCK;

        public String getMessage() {
            return switch (this) {
                case PREVENT_ON_BREAK -> "Mode: Prevent on break";
                case PREVENT_ON_PLACE -> "Mode: Prevent on place";
                case REPLACE_BLOCK -> "Mode: Replace block";
            };
        }
    }
}
