package dragon826307.dt.project.analog_circuit;

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
    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        //TODO : 检查玩家游戏模式
        if (!playerEntity.isPlayer() || !playerEntity.getMainHandStack().isEmpty()) return ActionResult.PASS;
        if (ConfigProjectManager.getConfig(ConfigProjects.Main.ALLOW_MODIFY_CONTAINER_SIGNAL).asBoolean() && !world.isClient()){
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
        world.setBlockState(blockPos, state , Block.NOTIFY_NEIGHBORS);
    }
}
