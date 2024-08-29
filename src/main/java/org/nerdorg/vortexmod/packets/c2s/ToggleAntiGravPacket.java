package org.nerdorg.vortexmod.packets.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlockEntity;

import java.util.function.Supplier;

public class ToggleAntiGravPacket {
    private BlockPos pos;

    public ToggleAntiGravPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(ToggleAntiGravPacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.pos);
    }

    public static ToggleAntiGravPacket decode(FriendlyByteBuf buf) {
        ToggleAntiGravPacket scp = new ToggleAntiGravPacket(buf.readBlockPos());
        return scp;
    }

    public static void handle(ToggleAntiGravPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                ServerLevel serverLevel = ctx.get().getSender().serverLevel();
                FlightComputerBlockEntity flightComputerBlockEntity = (FlightComputerBlockEntity) serverLevel.getBlockEntity(pkt.pos);
                if (flightComputerBlockEntity != null) {
                    if (flightComputerBlockEntity.serverShip != null) {
                        if (flightComputerBlockEntity.control != null) {
                            flightComputerBlockEntity.control.antigrav = !flightComputerBlockEntity.control.antigrav;
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
