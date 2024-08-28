package org.nerdorg.vortexmod.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlockEntity;

import java.util.function.Supplier;

public class DisassemblePacket {
    private BlockPos pos;

    public DisassemblePacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(DisassemblePacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.pos);
    }

    public static DisassemblePacket decode(FriendlyByteBuf buf) {
        DisassemblePacket scp = new DisassemblePacket(buf.readBlockPos());
        return scp;
    }

    public static void handle(DisassemblePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                ServerLevel serverLevel = ctx.get().getSender().serverLevel();
                FlightComputerBlockEntity flightComputerBlockEntity = (FlightComputerBlockEntity) serverLevel.getBlockEntity(pkt.pos);
                if (flightComputerBlockEntity != null) {
                    flightComputerBlockEntity.disassemble();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
