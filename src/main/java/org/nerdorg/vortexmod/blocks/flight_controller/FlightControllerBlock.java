package org.nerdorg.vortexmod.blocks.flight_controller;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlockEntity;
import org.nerdorg.vortexmod.index.VMBlockEntities;
import org.nerdorg.vortexmod.shapes.VMShapes;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

import java.util.List;

public class FlightControllerBlock extends HorizontalKineticBlock implements IBE<FlightControllerBlockEntity>, IRotate {

    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public static final VoxelShaper CONTROLLER_SHAPE = VMShapes.shape(0, 0, 0, 16, 10, 16).forHorizontal(Direction.DOWN);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return CONTROLLER_SHAPE.get(state.getValue(HORIZONTAL_FACING));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 4;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(HORIZONTAL_FACING, context.getHorizontalDirection())
                .setValue(ENABLED, false);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel instanceof ServerLevel serverLevel) {
            if (!pPlayer.isPassenger()) {
                FlightControllerBlockEntity entity = (FlightControllerBlockEntity) pLevel.getBlockEntity(pPos);
                entity.startRiding(pPlayer, true, pPos, pState, serverLevel);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hideStressImpact() {
        return false;
    }

    public FlightControllerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, Boolean.FALSE));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getClockWise().getAxis();
    }

    @Override
    public BlockEntityType<? extends FlightControllerBlockEntity> getBlockEntityType() {
        return VMBlockEntities.FLIGHT_CONTROLLER.get();
    }

    @Override
    public Class<FlightControllerBlockEntity> getBlockEntityClass() {
        return FlightControllerBlockEntity.class;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return VMBlockEntities.FLIGHT_CONTROLLER.create(pos, state);
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.MEDIUM;
    }
}
