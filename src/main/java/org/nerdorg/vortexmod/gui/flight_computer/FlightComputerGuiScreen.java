package org.nerdorg.vortexmod.gui.flight_computer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.index.VMBlocks;
import org.nerdorg.vortexmod.packets.AssemblePacket;
import org.nerdorg.vortexmod.packets.DisassemblePacket;

public class FlightComputerGuiScreen extends AbstractContainerScreen<FlightComputerGuiMenu> {
    private Button assemble_button;
    private Button disassemble_button;

    private EditBox targetX;
    private EditBox targetY;
    private EditBox targetZ;
    private EditBox rotX;
    private EditBox rotY;
    private EditBox rotZ;
    private Button setTargetButton;
    private Button exitButton;

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

        //if (this.isTargetTabActive) {
        // Target Tab Components
        this.targetX = this.addRenderableWidget(new EditBox(this.font, i - 50, j - 20, 100, 20, Component.literal("")));
        this.targetY = this.addRenderableWidget(new EditBox(this.font, i - 50, j, 100, 20, Component.literal("")));
        this.targetZ = this.addRenderableWidget(new EditBox(this.font, i - 50, j + 20, 100, 20, Component.literal("")));
        this.rotX = this.addRenderableWidget(new EditBox(this.font, i - 50, j + 41, 30, 20, Component.literal("")));
        this.rotY = this.addRenderableWidget(new EditBox(this.font, i - 15, j + 41, 30, 20, Component.literal("")));
        this.rotZ = this.addRenderableWidget(new EditBox(this.font, i + 20, j + 41, 30, 20, Component.literal("")));

        this.setTargetButton = this.addRenderableWidget(Button.builder(Component.literal("Set"), (p_97691_) -> {
            this.onSetTarget();
        }).bounds(i - 50, j + 70, 40, 20).build());

        this.exitButton = this.addRenderableWidget(Button.builder(Component.literal("Exit"), (p_97691_) -> {
            this.onExit();
        }).bounds(i + 10, j + 70, 40, 20).build());
        //} else {
        // Info Tab Components
        this.assemble_button = this.addRenderableWidget(Button.builder(Component.literal("Assemble"), (p_97691_) -> {
            this.onAssemble();
        }).bounds(i - 116, j, 65, 20).build());

        this.disassemble_button = this.addRenderableWidget(Button.builder(Component.literal("Disassemble"), (p_97691_) -> {
            this.onDisassemble();
        }).bounds(i + 51, j, 65, 20).build());
        //}
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

        this.setTargetButton.setFocused(false);
        this.exitButton.setFocused(false);
        this.assemble_button.setFocused(false);
        this.disassemble_button.setFocused(false);
    }

    public void onAssemble() {
        VortexMod.Network.sendToServer(new AssemblePacket(this.getMenu().blockEntity.getBlockPos()));
    }

    public void onDisassemble() {
        VortexMod.Network.sendToServer(new DisassemblePacket(this.getMenu().blockEntity.getBlockPos()));
    }

    public void onSetTarget() {
        this.onClose();
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
        this.targetX.setY(j - 20);

        this.targetY.setX(i - 50);
        this.targetY.setY(j);

        this.targetZ.setX(i - 50);
        this.targetZ.setY(j + 20);

        this.rotX.setX(i - 50);
        this.rotX.setY(j + 41);

        this.rotY.setX(i - 15);
        this.rotY.setY(j + 41);

        this.rotZ.setX(i + 20);
        this.rotZ.setY(j + 41);

        this.setTargetButton.setX(i - 50);
        this.setTargetButton.setY(j + 70);

        this.exitButton.setX(i + 10);
        this.exitButton.setY(j + 70);

        this.assemble_button.setX(i - 116);
        this.assemble_button.setY(j);

        this.disassemble_button.setX(i + 51);
        this.disassemble_button.setY(j);
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

