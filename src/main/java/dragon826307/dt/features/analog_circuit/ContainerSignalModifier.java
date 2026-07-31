package dragon826307.dt.features.analog_circuit;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.InitializePhase;
import dragon826307.dt.config.ConfigProjectManager;
import dragon826307.dt.config.ConfigProjects;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContainerSignalModifier implements UseBlockCallback{
    private static final Set<Block> AVAIL_BLOCKS = new HashSet<>(Arrays.asList(
            Blocks.COMPOSTER,
            Blocks.LAVA_CAULDRON,Blocks.WATER_CAULDRON,Blocks.POWDER_SNOW_CAULDRON,Blocks.CAULDRON
    ));
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_MAIN)
    private static void init() {
        UseBlockCallback.EVENT.register(new ContainerSignalModifier());
    }
    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        if (!ConfigProjectManager.getConfig(ConfigProjects.Main.ALLOW_MODIFY_CONTAINER_SIGNAL).asBoolean() || hand != Hand.MAIN_HAND || !playerEntity.isPlayer() || playerEntity.getGameMode() == null || !playerEntity.getMainHandStack().isEmpty() || !playerEntity.getGameMode().isCreative()) return ActionResult.PASS;
        if (!world.isClient()){
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (AVAIL_BLOCKS.contains(block)) {
                if (block == Blocks.COMPOSTER) execute_composter(world,blockPos,blockState);
            }
        }
        return ActionResult.CONSUME;
    }
    private static void execute_composter(World world, BlockPos blockPos,BlockState blockState) {
        int level = blockState.get(ComposterBlock.LEVEL);
        level++;
        level %= 9;
        BlockState state = blockState.with(ComposterBlock.LEVEL, level);
        world.setBlockState(blockPos, state , Block.NOTIFY_LISTENERS);
    }
}
