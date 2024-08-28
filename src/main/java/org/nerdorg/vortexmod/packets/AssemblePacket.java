package org.nerdorg.vortexmod.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlockEntity;

import java.util.function.Supplier;

public class AssemblePacket {
    private BlockPos pos;

    public AssemblePacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(AssemblePacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.pos);
    }

    public static AssemblePacket decode(FriendlyByteBuf buf) {
        AssemblePacket scp = new AssemblePacket(buf.readBlockPos());
        return scp;
    }

    public static void handle(AssemblePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                ServerLevel serverLevel = ctx.get().getSender().serverLevel();
                FlightComputerBlockEntity flightComputerBlockEntity = (FlightComputerBlockEntity) serverLevel.getBlockEntity(pkt.pos);
                if (flightComputerBlockEntity != null) {
                    flightComputerBlockEntity.assemble();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
