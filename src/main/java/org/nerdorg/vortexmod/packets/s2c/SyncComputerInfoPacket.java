package org.nerdorg.vortexmod.packets.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3d;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlockEntity;

import java.util.function.Supplier;
import java.util.logging.Level;

public class SyncComputerInfoPacket {
    private BlockPos pos;
    private BlockPos currentPos;
    private double cRotX;
    private double cRotY;
    private double cRotZ;
    private BlockPos targetPos;
    private double tRotX;
    private double tRotY;
    private double tRotZ;
    private double speed;
    private float max_stress;
    private float stress_amount;
    private boolean assembled;
    private boolean stabilizers;
    private boolean antigrav;

    public SyncComputerInfoPacket(BlockPos pos, BlockPos currentPos, double cRotX, double cRotY, double cRotZ, BlockPos targetPos, double tRotX, double tRotY, double tRotZ, double speed, float max_stress, float stress_amount, boolean assembled, boolean stabilizers, boolean antigrav) {
        this.pos = pos;
        this.currentPos = currentPos;
        this.cRotX = cRotX;
        this.cRotY = cRotY;
        this.cRotZ = cRotZ;
        this.targetPos = targetPos;
        this.tRotX = tRotX;
        this.tRotY = tRotY;
        this.tRotZ = tRotZ;
        this.speed = speed;
        this.max_stress = max_stress;
        this.stress_amount = stress_amount;
        this.assembled = assembled;
        this.stabilizers = stabilizers;
        this.antigrav = antigrav;
    }

    public static void encode(SyncComputerInfoPacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.pos);
        tag.writeBlockPos(packet.currentPos);
        tag.writeDouble(packet.cRotX);
        tag.writeDouble(packet.cRotY);
        tag.writeDouble(packet.cRotZ);
        tag.writeBlockPos(packet.targetPos);
        tag.writeDouble(packet.tRotX);
        tag.writeDouble(packet.tRotY);
        tag.writeDouble(packet.tRotZ);
        tag.writeDouble(packet.speed);
        tag.writeFloat(packet.max_stress);
        tag.writeFloat(packet.stress_amount);
        tag.writeBoolean(packet.assembled);
        tag.writeBoolean(packet.stabilizers);
        tag.writeBoolean(packet.antigrav);
    }

    public static SyncComputerInfoPacket decode(FriendlyByteBuf buf) {
        SyncComputerInfoPacket scp = new SyncComputerInfoPacket(buf.readBlockPos(), buf.readBlockPos(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readBlockPos(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
        return scp;
    }

    public static void handle(SyncComputerInfoPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Minecraft client = Minecraft.getInstance();
                ClientLevel clientLevel = client.level;
                FlightComputerBlockEntity flightComputerBlockEntity = (FlightComputerBlockEntity) clientLevel.getBlockEntity(pkt.pos);
                if (flightComputerBlockEntity != null) {
                    flightComputerBlockEntity.currentPos = pkt.currentPos;
                    flightComputerBlockEntity.currentRotation = new Vector3d(pkt.cRotX, pkt.cRotY, pkt.cRotZ);
                    flightComputerBlockEntity.targetPos = pkt.targetPos;
                    flightComputerBlockEntity.targetRotation = new Vector3d(pkt.tRotX, pkt.tRotY, pkt.tRotZ);
                    flightComputerBlockEntity.speed = pkt.speed;
                    flightComputerBlockEntity.max_stress = pkt.max_stress;
                    flightComputerBlockEntity.stress_amount = pkt.stress_amount;
                    flightComputerBlockEntity.assembled = pkt.assembled;
                    flightComputerBlockEntity.stabilizers = pkt.stabilizers;
                    flightComputerBlockEntity.antigrav = pkt.antigrav;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
