package org.nerdorg.vortexmod.blocks.flight_controller;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
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
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlockEntity;
import org.nerdorg.vortexmod.index.VMBlockEntities;
import org.nerdorg.vortexmod.shapes.VMShapes;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

public class FlightControllerBlock extends HorizontalAxisKineticBlock implements IBE<FlightControllerBlockEntity>, IRotate {

    public static final VoxelShaper CONTROLLER_SHAPE = VMShapes.shape(4, 0, 4, 12, 16, 12).forHorizontalAxis();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return CONTROLLER_SHAPE.get(state.getValue(HORIZONTAL_AXIS));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Axis preferredAxis = getPreferredHorizontalAxis(context);
        if (preferredAxis != null)
            return this.defaultBlockState().setValue(HORIZONTAL_AXIS, preferredAxis);
        return this.defaultBlockState().setValue(HORIZONTAL_AXIS, context.getHorizontalDirection().getClockWise().getAxis());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel instanceof ServerLevel serverLevel) {
            FlightControllerBlockEntity entity = (FlightControllerBlockEntity) pLevel.getBlockEntity(pPos);
            entity.startRiding(pPlayer, true, pPos, pState, serverLevel);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hideStressImpact() {
        return false;
    }

    public FlightControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_AXIS);
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
