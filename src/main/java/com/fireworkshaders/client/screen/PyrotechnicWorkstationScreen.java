package com.fireworkshaders.client.screen;

import com.fireworkshaders.menu.PyrotechnicWorkstationMenu;
import com.fireworkshaders.network.ModNetwork;
import com.fireworkshaders.network.SetRocketNamePacket;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import com.mojang.blaze3d.vertex.PoseStack;

/** Compact, texture-free workstation UI with a live rocket-name field. */
public class PyrotechnicWorkstationScreen extends AbstractContainerScreen<PyrotechnicWorkstationMenu> {
    private EditBox nameField;

    public PyrotechnicWorkstationScreen(PyrotechnicWorkstationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 236;
        this.imageHeight = 200;
        this.inventoryLabelX = 37;
        this.inventoryLabelY = 104;
    }

    @Override
    protected void init() {
        super.init();
        this.nameField = new EditBox(this.font, this.leftPos + 25, this.topPos + 80,
                186, 16, Component.translatable("gui.fireworkshaders.rocket_name"));
        this.nameField.setMaxLength(32);
        this.nameField.setValue(this.menu.getRocketName());
        this.nameField.setSuggestion(Component.translatable("gui.fireworkshaders.rocket_name_hint").getString());
        this.nameField.setResponder(value -> ModNetwork.CHANNEL.sendToServer(
                new SetRocketNamePacket(this.menu.containerId, value)));
        this.addRenderableWidget(this.nameField);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        // Dark enamel panel with inset ingredient and inventory wells.
        fill(poseStack, x, y, x + this.imageWidth, y + this.imageHeight, 0xF0141B24);
        fill(poseStack, x + 1, y + 1, x + this.imageWidth - 1, y + 24, 0xFF263442);
        fill(poseStack, x + 8, y + 31, x + this.imageWidth - 8, y + 73, 0xFF1B2732);
        fill(poseStack, x + 8, y + 77, x + this.imageWidth - 8, y + 101, 0xFF10161D);
        fill(poseStack, x + 8, y + 111, x + this.imageWidth - 8, y + this.imageHeight - 5, 0xFF1B2732);
        fill(poseStack, x + 1, y + 1, x + this.imageWidth - 1, y + 2, 0xFF78DCE8);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        drawCenteredString(poseStack, this.font, this.title, this.imageWidth / 2, 8, 0xFFF3F6FA);
        drawCenteredString(poseStack, this.font, Component.translatable("gui.fireworkshaders.powder"),
                21, 34, 0xFFFFD166);
        drawCenteredString(poseStack, this.font, Component.translatable("gui.fireworkshaders.shape"),
                57, 34, 0xFFFFD166);
        drawCenteredString(poseStack, this.font, Component.translatable("gui.fireworkshaders.trail"),
                93, 34, 0xFFFFD166);
        drawCenteredString(poseStack, this.font, Component.translatable("gui.fireworkshaders.crackle"),
                129, 34, 0xFFFFD166);
        drawCenteredString(poseStack, this.font, Component.translatable("gui.fireworkshaders.smoke"),
                165, 34, 0xFFFFD166);
        drawCenteredString(poseStack, this.font, Component.translatable("gui.fireworkshaders.output"),
                213, 34, 0xFF7DE2D1);
        this.font.draw(poseStack, Component.translatable("gui.fireworkshaders.rocket_name"),
                25, 68, 0xFFD3DAE3);
        this.font.draw(poseStack, Component.translatable("container.inventory"),
                this.inventoryLabelX, this.inventoryLabelY, 0xFFD3DAE3);
        this.font.draw(poseStack, "›", 184, 48, 0xFF7DE2D1);
    }
}
