package org.nerdorg.vortexmod.packets.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3d;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlockEntity;
import org.nerdorg.vortexmod.ship_management.TardisInfo;

import java.util.function.Supplier;

public class SetTargetPacket {
    private BlockPos entity_pos;
    private BlockPos pos;
    private double rot_x;
    private double rot_y;
    private double rot_z;

    public SetTargetPacket(BlockPos entity_pos, BlockPos pos, double rot_x, double rot_y, double rot_z) {
        this.entity_pos = entity_pos;
        this.pos = pos;
        this.rot_x = rot_x;
        this.rot_y = rot_y;
        this.rot_z = rot_z;
    }

    public static void encode(SetTargetPacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.entity_pos);
        tag.writeBlockPos(packet.pos);
        tag.writeDouble(packet.rot_x);
        tag.writeDouble(packet.rot_y);
        tag.writeDouble(packet.rot_z);
    }

    public static SetTargetPacket decode(FriendlyByteBuf buf) {
        SetTargetPacket scp = new SetTargetPacket(buf.readBlockPos(), buf.readBlockPos(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        return scp;
    }

    public static void handle(SetTargetPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                ServerLevel serverLevel = ctx.get().getSender().serverLevel();
                FlightComputerBlockEntity flightComputerBlockEntity = (FlightComputerBlockEntity) serverLevel.getBlockEntity(pkt.entity_pos);
                if (flightComputerBlockEntity != null) {
                    if (flightComputerBlockEntity.serverShip != null) {
                        if (flightComputerBlockEntity.control != null) {
                            TardisInfo tardisInfo = flightComputerBlockEntity.serverShip.getAttachment(TardisInfo.class);
                            if (tardisInfo != null) {
                                tardisInfo.target_location = pkt.pos;
                                tardisInfo.target_rotation = new Vector3d(pkt.rot_x, pkt.rot_y, pkt.rot_z);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
