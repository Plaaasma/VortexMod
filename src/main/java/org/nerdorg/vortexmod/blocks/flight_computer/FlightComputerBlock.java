package org.nerdorg.vortexmod.blocks.flight_computer;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.nerdorg.vortexmod.index.VMBlockEntities;
import org.nerdorg.vortexmod.shapes.VMShapes;

import java.util.List;

public class FlightComputerBlock extends DirectionalKineticBlock implements IBE<FlightComputerBlockEntity>, IRotate {

    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public static final VoxelShaper COMPUTER_SHAPE = VMShapes.shape(0, 1, 0, 16, 16, 16).forDirectional();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return COMPUTER_SHAPE.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown()) || preferred == null)
            return super.getStateForPlacement(context);
        return defaultBlockState().setValue(FACING, preferred).setValue(ENABLED, false);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        FlightComputerBlockEntity entity = (FlightComputerBlockEntity) pLevel.getBlockEntity(pPos);

        if (!pLevel.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) pPlayer, entity, pPos);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return (MenuProvider) pLevel.getBlockEntity(pPos);
    }

    @Override
    public boolean hideStressImpact() {
        return false;
    }

    public FlightComputerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        boolean touching_back = face == state.getValue(FACING);

        Direction direction = state.getValue(FlightComputerBlock.FACING);
        Direction.Axis first_axis = direction == Direction.UP || direction == Direction.DOWN ?
                Direction.Axis.Z : (direction == Direction.EAST || direction == Direction.WEST ? Direction.Axis.Z : Direction.Axis.X);
        Direction.Axis second_axis = direction == Direction.UP || direction == Direction.DOWN ?
                Direction.Axis.X : Direction.Axis.Y;

        return touching_back || face == state.getValue(FACING).getClockWise(first_axis) || face == state.getValue(FACING).getCounterClockWise(first_axis) || face == state.getValue(FACING).getClockWise(second_axis) || face == state.getValue(FACING).getCounterClockWise(second_axis);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING)
                .getAxis();
    }

    @Override
    public BlockEntityType<? extends FlightComputerBlockEntity> getBlockEntityType() {
        return VMBlockEntities.FLIGHT_COMPUTER.get();
    }

    @Override
    public Class<FlightComputerBlockEntity> getBlockEntityClass() {
        return FlightComputerBlockEntity.class;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return VMBlockEntities.FLIGHT_COMPUTER.create(pos, state);
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.SLOW;
    }
}
