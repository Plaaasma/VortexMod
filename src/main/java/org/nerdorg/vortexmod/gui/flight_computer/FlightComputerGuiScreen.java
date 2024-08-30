package org.nerdorg.vortexmod.gui.flight_computer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jline.utils.Colors;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.packets.c2s.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FlightComputerGuiScreen extends AbstractContainerScreen<FlightComputerGuiMenu> {
    private Button assemble_button;
    private Button disassemble_button;
    private Button stabilizer_button;
    private Button antigrav_button;
    private Button setTargetButton;
    private Button exitButton;

    private EditBox targetX;
    private EditBox targetY;
    private EditBox targetZ;
    private EditBox rotX;
    private EditBox rotY;
    private EditBox rotZ;

    public FlightComputerGuiScreen(FlightComputerGuiMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        this.width = this.minecraft.getWindow().getGuiScaledWidth();
        this.height = this.minecraft.getWindow().getGuiScaledHeight();

        int i = this.width / 2;
        int j = this.height / 2;

        this.targetX = this.addRenderableWidget(new EditBox(this.font, i - 50, j - 50, 100, 20, Component.literal("")));
        this.targetY = this.addRenderableWidget(new EditBox(this.font, i - 50, j - 30, 100, 20, Component.literal("")));
        this.targetZ = this.addRenderableWidget(new EditBox(this.font, i - 50, j - 10, 100, 20, Component.literal("")));
        this.rotX = this.addRenderableWidget(new EditBox(this.font, i - 50, j + 11, 30, 20, Component.literal("")));
        this.rotY = this.addRenderableWidget(new EditBox(this.font, i - 15, j + 11, 30, 20, Component.literal("")));
        this.rotZ = this.addRenderableWidget(new EditBox(this.font, i + 20, j + 11, 30, 20, Component.literal("")));

        this.setTargetButton = this.addRenderableWidget(Button.builder(Component.literal("Set"), (p_97691_) -> {
            this.onSetTarget();
        }).bounds(i - 50, j + 40, 40, 20).build());

        this.exitButton = this.addRenderableWidget(Button.builder(Component.literal("Exit"), (p_97691_) -> {
            this.onExit();
        }).bounds(i + 10, j + 40, 40, 20).build());

        this.assemble_button = this.addRenderableWidget(Button.builder(Component.literal("Assemble"), (p_97691_) -> {
            this.onAssemble();
        }).bounds(i - 116, j, 65, 20).build());

        this.disassemble_button = this.addRenderableWidget(Button.builder(Component.literal("Disassemble"), (p_97691_) -> {
            this.onDisassemble();
        }).bounds(i + 51, j, 65, 20).build());

        this.antigrav_button = this.addRenderableWidget(Button.builder(Component.literal("Anti-Grav"), (p_97691_) -> {
            this.onAntiGrav();
        }).bounds(i - 116, j - 30, 65, 20).build());

        this.stabilizer_button = this.addRenderableWidget(Button.builder(Component.literal("Stabilizers"), (p_97691_) -> {
            this.onStabilizer();
        }).bounds(i + 51, j - 30, 65, 20).build());
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft.level != null) {
            pGuiGraphics.fillGradient(0, 0, this.width, this.height, -804253680, -937550306);
            MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(this, pGuiGraphics));
        } else {
            this.renderDirtBackground(pGuiGraphics);
        }

        this.width = this.minecraft.getWindow().getGuiScaledWidth();
        this.height = this.minecraft.getWindow().getGuiScaledHeight();

        int i = this.width / 2;
        int j = this.height / 2;

        NumberFormat nf = new DecimalFormat("#.#");


        pGuiGraphics.drawCenteredString(this.font, "Speed: " + String.format("%.2f", this.getMenu().blockEntity.speed) + " m/s" + " | " + String.format("%.2f", this.getMenu().blockEntity.speed * 2.23694) + " mph",
                i, j - 90, ChatFormatting.GRAY.getColor());

        pGuiGraphics.drawCenteredString(this.font, "Stress: " + String.format("%.2f", (this.getMenu().blockEntity.stress_amount / this.getMenu().blockEntity.max_stress) * 100) + "% (" + this.getMenu().blockEntity.stress_amount + "/" + this.getMenu().blockEntity.max_stress + ")",
                i, j - 80, ChatFormatting.GRAY.getColor());

        pGuiGraphics.drawCenteredString(this.font, "Target Pos: " + this.getMenu().blockEntity.targetPos.toShortString() + " | Rotation: " + this.getMenu().blockEntity.targetRotation.toString(nf),
                i, j - 70, ChatFormatting.GRAY.getColor());

        pGuiGraphics.drawCenteredString(this.font, "Current Pos: " + this.getMenu().blockEntity.currentPos.toShortString() + " | Rotation: " + this.getMenu().blockEntity.currentRotation.toString(nf),
                i, j - 60, ChatFormatting.GRAY.getColor());

        this.targetX.setTooltip(Tooltip.create(Component.literal("Target X Coordinate")));
        this.targetY.setTooltip(Tooltip.create(Component.literal("Target Y Coordinate")));
        this.targetZ.setTooltip(Tooltip.create(Component.literal("Target Z Coordinate")));

        this.rotX.setTooltip(Tooltip.create(Component.literal("Target X Rotation")));
        this.rotY.setTooltip(Tooltip.create(Component.literal("Target Y Rotation")));
        this.rotZ.setTooltip(Tooltip.create(Component.literal("Target Z Rotation")));

        this.targetX.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.targetY.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.targetZ.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.rotX.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.rotY.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.rotZ.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.setTargetButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.exitButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        this.assemble_button.setTooltip(Tooltip.create(Component.literal("Assembles All Attached Blocks")));
        this.disassemble_button.setTooltip(Tooltip.create(Component.literal("Disassembles All Attached Blocks")));

        this.assemble_button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.disassemble_button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        this.antigrav_button.setTooltip(Tooltip.create(Component.literal("Toggles The TARDIS Anti-Grav. Currently: " + (this.getMenu().blockEntity.antigrav ? "On" : "Off"))));
        this.stabilizer_button.setTooltip(Tooltip.create(Component.literal("Toggles Stabilizing The TARDIS. Currently: " + (this.getMenu().blockEntity.stabilizers ? "On" : "Off"))));

        this.antigrav_button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.stabilizer_button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        this.setTargetButton.setFocused(false);
        this.exitButton.setFocused(false);
        this.assemble_button.setFocused(false);
        this.disassemble_button.setFocused(false);
        this.antigrav_button.setFocused(false);
        this.stabilizer_button.setFocused(false);

        if (this.getMenu().blockEntity.assembled) {
            this.assemble_button.active = false;
            this.disassemble_button.active = true;
        }
        else {
            this.assemble_button.active = true;
            this.disassemble_button.active = false;
        }
    }

    public void onAssemble() {
        VortexMod.Network.sendToServer(new AssemblePacket(this.getMenu().blockEntity.getBlockPos()));
    }

    public void onDisassemble() {
        VortexMod.Network.sendToServer(new DisassemblePacket(this.getMenu().blockEntity.getBlockPos()));
    }

    public void onStabilizer() {
        VortexMod.Network.sendToServer(new ToggleStabilizerPacket(this.getMenu().blockEntity.getBlockPos()));
    }

    public void onAntiGrav() {
        VortexMod.Network.sendToServer(new ToggleAntiGravPacket(this.getMenu().blockEntity.getBlockPos()));
    }

    public void onSetTarget() {
        double x = 0;
        double y = 0;
        double z = 0;
        double x_rot = 0;
        double y_rot = 0;
        double z_rot = 0;
        String x_value = this.targetX.getValue();
        if (x_value.matches("-?\\d+")) {
            x = Double.parseDouble(x_value);
        }
        else {
            x = this.getMenu().blockEntity.targetPos.getX();
        }
        String y_value = this.targetY.getValue();
        if (y_value.matches("-?\\d+")) {
            y = Double.parseDouble(y_value);
        }
        else {
            y = this.getMenu().blockEntity.targetPos.getY();
        }
        String z_value = this.targetZ.getValue();
        if (z_value.matches("-?\\d+")) {
            z = Double.parseDouble(z_value);
        }
        else {
            z = this.getMenu().blockEntity.targetPos.getZ();
        }
        String x_rot_value = this.rotX.getValue();
        if (x_rot_value.matches("-?\\d+")) {
            x_rot = Double.parseDouble(x_rot_value);
        }
        else {
            x_rot = this.getMenu().blockEntity.targetRotation.x();
        }
        String y_rot_value = this.rotY.getValue();
        if (y_rot_value.matches("-?\\d+")) {
            y_rot = Double.parseDouble(y_rot_value);
        }
        else {
            y_rot = this.getMenu().blockEntity.targetRotation.y();
        }
        String z_rot_value = this.rotZ.getValue();
        if (z_rot_value.matches("-?\\d+")) {
            z_rot = Double.parseDouble(z_rot_value);
        }
        else {
            z_rot = this.getMenu().blockEntity.targetRotation.z();
        }

        VortexMod.Network.sendToServer(new SetTargetPacket(this.getMenu().blockEntity.getBlockPos(), new BlockPos((int) x, (int) y, (int) z), x_rot, y_rot, z_rot));
    }

    public void onExit() {
        this.onClose();
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);

        this.width = this.minecraft.getWindow().getGuiScaledWidth();
        this.height = this.minecraft.getWindow().getGuiScaledHeight();

        int i = this.width / 2;
        int j = this.height / 2;

        this.targetX.setX(i - 50);
        this.targetX.setY(j - 50);

        this.targetY.setX(i - 50);
        this.targetY.setY(j - 30);

        this.targetZ.setX(i - 50);
        this.targetZ.setY(j - 10);

        this.rotX.setX(i - 50);
        this.rotX.setY(j + 11);

        this.rotY.setX(i - 15);
        this.rotY.setY(j + 11);

        this.rotZ.setX(i + 20);
        this.rotZ.setY(j + 11);

        this.setTargetButton.setX(i - 50);
        this.setTargetButton.setY(j + 40);

        this.exitButton.setX(i + 10);
        this.exitButton.setY(j + 40);

        this.assemble_button.setX(i - 116);
        this.assemble_button.setY(j);

        this.disassemble_button.setX(i + 51);
        this.disassemble_button.setY(j);

        this.antigrav_button.setX(i - 116);
        this.antigrav_button.setY(j - 30);

        this.stabilizer_button.setX(i + 51);
        this.stabilizer_button.setY(j - 30);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        // Background rendering can be done here if needed
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

