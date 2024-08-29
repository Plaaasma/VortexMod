package org.nerdorg.vortexmod.blocks.types;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.nerdorg.vortexmod.ship_management.ShipController;
import org.nerdorg.vortexmod.ship_management.TardisInfo;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

public class TardisComponentBlockEntity extends KineticBlockEntity {

    public ServerShip serverShip;
    public ShipController control;
    public TardisInfo tardisInfo;

    public TardisComponentBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
    }

    private boolean firstTickState = true;

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide()) return;
        if(firstTickState) firstTick();
        firstTickState = false;

        if (this.serverShip == null) {
            updateShipReference((ServerLevel) level, getBlockPos());
        }
        else {
            if (this.control != null) {
                this.control.serverShip = this.serverShip;
            }
        }
    }

    private void updateShipReference(ServerLevel serverLevel, BlockPos pos) {
        this.serverShip = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos.getX(), pos.getY(), pos.getZ());
        if (this.serverShip != null) {
            this.control = this.serverShip.getAttachment(ShipController.class);

            if (this.control == null) {
                this.control = new ShipController();
                this.control.serverShip = this.serverShip;
                this.serverShip.saveAttachment(ShipController.class, this.control);
            }

            this.control.serverShip = this.serverShip;

            this.tardisInfo = this.serverShip.getAttachment(TardisInfo.class);

            if (this.tardisInfo == null) {
                Vector3dc shipPos = this.serverShip.getTransform().getPositionInWorld();
                this.serverShip.saveAttachment(TardisInfo.class, new TardisInfo(new BlockPos((int) shipPos.x(), (int) shipPos.y(), (int) shipPos.z()), new Vector3d(0, 0, 0), this.getLevel(), this.getLevel()));
            }
            else {
                this.tardisInfo.current_level = this.getLevel();
                this.tardisInfo.target_level = this.getLevel();
            }
        }
    }

    public void firstTick() {

    };
}
