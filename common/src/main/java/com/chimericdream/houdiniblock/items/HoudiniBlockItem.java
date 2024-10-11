package com.chimericdream.houdiniblock.items;

import net.minecraft.block.Block;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

public class HoudiniBlockItem extends BlockItem {
    private static final NbtComponent DEFAULT_NBT;

    static {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("houdini_placement_mode", PlacementMode.PREVENT_ON_BREAK.toString());

        DEFAULT_NBT = NbtComponent.of(nbt);
    }

    public HoudiniBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

//    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        if (!player.isSneaking()) {
//            return TypedActionResult.pass(player.getStackInHand(hand));
//        }
//
//        try {
//            ItemStack itemStack = player.getStackInHand(hand);
//            NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, DEFAULT_NBT).copyNbt();
//
//            PlacementMode currentMode = PlacementMode.valueOf(nbt.getString("houdini_placement_mode"));
//            PlacementMode newMode = switch (currentMode) {
//                case PREVENT_ON_BREAK -> PlacementMode.PREVENT_ON_PLACE;
//                case PREVENT_ON_PLACE -> PlacementMode.REPLACE_BLOCK;
//                case REPLACE_BLOCK -> PlacementMode.PREVENT_ON_BREAK;
//            };
//
//            nbt.putString("houdini_placement_mode", newMode.toString());
//            itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
//
//            return TypedActionResult.pass(player.getStackInHand(hand));
//        } catch (IllegalArgumentException e) {
//            return TypedActionResult.fail(player.getStackInHand(hand));
//        }
//    }
//
//    public ActionResult useOnBlock(ItemUsageContext context) {
//        PlayerEntity player = context.getPlayer();
//        ItemStack stack = context.getStack();
//
//        if (player != null && player.isSneaking()) {
//            BlockState target = context.getWorld().getBlockState(context.getBlockPos());
//
//            return ActionResult.PASS;
//        }
//
//        return super.useOnBlock(context);
//    }

    private enum PlacementMode {
        PREVENT_ON_BREAK,
        PREVENT_ON_PLACE,
        REPLACE_BLOCK
    }
}
