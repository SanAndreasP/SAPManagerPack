/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.core.manpack.mod.client.gui;

import de.sanandrew.core.manpack.managers.SAPUpdateManager;
import de.sanandrew.core.manpack.managers.UpdateDownloader.EnumDlState;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

public class GuiModUpdate
        extends GuiScreen
        implements GuiYesNoCallback
{
    private static final List<SAPUpdateManager> MANAGERS = new ArrayList<>();
    private static final List<Pair<GuiButtonUpdate, GuiButtonDetails>> SLOT_BUTTONS = new ArrayList<>();

    public static void addManager(SAPUpdateManager mgr) {
        MANAGERS.add(mgr);

        int slotId = MANAGERS.indexOf(mgr);
        SLOT_BUTTONS.add(Pair.with(new GuiButtonUpdate(MANAGERS.size()*2, slotId, "Update"), new GuiButtonDetails(MANAGERS.size()*2 + 1, slotId, "Details")));
    }

    private GuiButton restartMC;
    private GuiButton back2Menu;
    private final GuiScreen mainMenu;
    private int selectedItem;

    private GuiModUpdate.GuiModSlots modList;

    public GuiModUpdate(GuiScreen menu) {
        this.mainMenu = menu;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        this.selectedItem = -1;

        for( Pair<GuiButtonUpdate, GuiButtonDetails> slotBtns : SLOT_BUTTONS ) {
            this.buttonList.add(slotBtns.getValue0());
            this.buttonList.add(slotBtns.getValue1());
        }

        this.buttonList.add(this.restartMC = new GuiButton(this.buttonList.size(), (this.width - 200) / 2, this.height - 52, "Restart Minecraft"));
        this.buttonList.add(this.back2Menu = new GuiButton(this.buttonList.size(), (this.width - 200) / 2, this.height - 32, "Back to main menu"));

        this.modList = new GuiModSlots();
        this.modList.registerScrollButtons(this.buttonList.size(), this.buttonList.size() + 1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        this.modList.drawScreen(mouseX, mouseY, partTicks);

        this.restartMC.drawButton(this.mc, mouseX, mouseY);
        this.back2Menu.drawButton(this.mc, mouseX, mouseY);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if( button == this.restartMC ) {
            GuiYesNo confirmGui = new GuiYesNo(this, "you really wanna restart MC?", "Yes?", 0);
            this.mc.displayGuiScreen(confirmGui);
        } else if( button == this.back2Menu ) {
            this.mc.displayGuiScreen(this.mainMenu);
        } else if( button instanceof GuiButtonUpdate ) {
            MANAGERS.get(((GuiButtonUpdate)button).slot).runUpdate();
//            System.out.println(((GuiButtonUpdate)button).slot);
        } else if( button instanceof GuiButtonDetails ) {
            System.out.println(((GuiButtonDetails)button).slot);
        } else {
            this.modList.actionPerformed(button);
        }
    }

    @Override
    public void confirmClicked(boolean isConfirmed, int guiId) {
        if( isConfirmed && guiId == 0 ) {
            try {
                SAPUtils.restartApp();
            } catch( RejectedExecutionException e ) {
                e.printStackTrace();
                this.mc.displayGuiScreen(this.mainMenu);
            }
            this.selectedItem = -1;
        }

        super.confirmClicked(isConfirmed, guiId);
    }

    static void drawGlossEffect(int x, int y1, int y2, float shift, int size) {
        int yShiftMax = y2 - y1;
        for( int layer = 0, xShift; layer < yShiftMax; layer++ ) {
            xShift = (int) (x + shift * yShiftMax - shift * layer);
            drawRect(xShift, y1 + layer, xShift + size, y1 + layer + 1, 0x40FFFFFF);
        }
    }

    class GuiModSlots extends GuiSlot
    {
        public GuiModSlots() {
            super(GuiModUpdate.this.mc, GuiModUpdate.this.width, GuiModUpdate.this.height, 32, GuiModUpdate.this.height - 64, 36);
        }

        @Override
        protected int getSize() {
            return GuiModUpdate.MANAGERS.size();
        }

        @Override
        protected void elementClicked(int elemIndex, boolean doubleClicked, int mouseX, int mouseY) {
            GuiModUpdate.this.selectedItem = elemIndex;
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return GuiModUpdate.this.selectedItem == slotIndex;
        }

        @Override
        protected void drawBackground() {
            GuiModUpdate.this.drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int slotIndex, int xPos, int yPos, int yMin, Tessellator tessellator, int mouseX, int mouseY) {
            SAPUpdateManager mgr = GuiModUpdate.MANAGERS.get(slotIndex);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            if( this.isSelected(slotIndex) ) {
                GuiModUpdate.drawGlossEffect(xPos + 100, yPos - 1, yPos + this.getSlotHeight() - 3, 1.0F, 15);
                GuiModUpdate.drawGlossEffect(xPos + 117, yPos - 1, yPos + this.getSlotHeight() - 3, 1.0F, 1);
                GuiModUpdate.drawGlossEffect(xPos + 120, yPos - 1, yPos + this.getSlotHeight() - 3, 1.0F, 5);
            }

            GuiModUpdate.this.fontRendererObj.drawStringWithShadow(mgr.getModName(), xPos, yPos, 0xFFFFFFFF);
            if( mgr.downloader != null ) {
                String result = "Begin downloading...";
                int progBarClr = 0xA0A0A0;
                int progBarLength = 0;

                if( mgr.downloader.getStatus() == EnumDlState.DOWNLOADING && mgr.downloader.getProgress() >= 0.0F) {
                    result = String.format("Download Update: %s%s%%", EnumChatFormatting.WHITE, new DecimalFormat("0.00").format(mgr.downloader.getProgress()));
                    progBarClr = 0xFF3030F0;
                    progBarLength = (int)(140.0F * mgr.downloader.getProgress() / 100.0F);
                } else if( mgr.downloader.getStatus() == EnumDlState.ERROR ) {
                    result = String.format("Download Update: %s%s", EnumChatFormatting.RED, "Failed!");
                    progBarClr = 0xFFE00000;
                    progBarLength = 140;
                } else if( mgr.downloader.getStatus() == EnumDlState.COMPLETE ) {
                    result = String.format("Download Update: %s%s", EnumChatFormatting.GREEN, "Complete!");
                    progBarClr = 0xFF00E000;
                    progBarLength = 140;
                }

                Gui.drawRect(xPos + 4, yPos + 23, xPos + 4 + progBarLength, yPos + 30, progBarClr);

                GuiModUpdate.this.fontRendererObj.drawStringWithShadow(result, xPos, yPos + 10, 0xFF808080);
            } else {
                GuiModUpdate.this.fontRendererObj.drawStringWithShadow("Installed Version: " + mgr.getVersion(), xPos, yPos + 10, 0xFF808080);
                GuiModUpdate.this.fontRendererObj.drawStringWithShadow("Latest Version: " + mgr.getVersionDiffSeverity().format + mgr.getUpdateInfo().version, xPos, yPos + 20, 0xFF808080);
            }

            GuiButtonUpdate btnUpdate =  SLOT_BUTTONS.get(slotIndex).getValue0();
            GuiButtonDetails btnDetails =  SLOT_BUTTONS.get(slotIndex).getValue1();

            btnUpdate.xPosition = xPos + 151;
            btnUpdate.yPosition = yPos;
            if( btnUpdate.yPosition + 15 > this.top && btnUpdate.yPosition < this.bottom ) {
                btnUpdate.enabled = mgr.getUpdateInfo().getDownload() != null && (mgr.downloader == null || mgr.downloader.getStatus() == EnumDlState.ERROR);
                btnUpdate.drawButton(GuiModUpdate.this.mc, mouseX, mouseY);
            } else {
                btnUpdate.enabled = false;
            }

            btnDetails.xPosition = xPos + 151;
            btnDetails.yPosition = yPos + 17;
            if( btnDetails.yPosition + 15 > this.top && btnDetails.yPosition < this.bottom ) {
                btnDetails.enabled = true;
                btnDetails.drawButton(GuiModUpdate.this.mc, mouseX, mouseY);
            } else {
                btnDetails.enabled = false;
            }

            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    static class GuiButtonSlot extends GuiButton {
        public final int slot;

        public GuiButtonSlot(int id, int slotId, String name) {
            super(id, 0, 0, 65, 15, name);

            this.slot = slotId;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if( this.visible ) {
                FontRenderer fontrenderer = mc.fontRenderer;

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                int hoverState = this.getHoverState(this.field_146123_n);

                // frame
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width - 1, this.yPosition + 1,
                             hoverState == 2 ? 0xFFFFFF80 : hoverState == 1 ? 0xFFFFFFFF : 0x80808080);
                Gui.drawRect(this.xPosition + width - 1, this.yPosition, this.xPosition + this.width, this.yPosition + this.height - 1,
                             hoverState == 2 ? 0xFF606030 : hoverState == 1 ? 0xFF606060 : 0x80303030);
                Gui.drawRect(this.xPosition + 1, this.yPosition + height - 1, this.xPosition + this.width, this.yPosition + this.height,
                             hoverState == 2 ? 0xFF606030 : hoverState == 1 ? 0xFF606060 : 0x80303030);
                Gui.drawRect(this.xPosition, this.yPosition + 1, this.xPosition + 1, this.yPosition + this.height,
                             hoverState == 2 ? 0xFFFFFF80 : hoverState == 1 ? 0xFFFFFFFF : 0x80808080);

                // background
                GuiModUpdate.drawGlossEffect(this.xPosition + 45, this.yPosition + 1, this.yPosition + this.height - 1, 1.0F, 2);
                GuiModUpdate.drawGlossEffect(this.xPosition + 49, this.yPosition + 1, this.yPosition + this.height - 1, 1.0F, 1);
                Gui.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1,
                             hoverState == 2 ? 0x80404020 : hoverState == 1 ? 0x80404040 : 0x80000000);

                this.mouseDragged(mc, mouseX, mouseY);
                int l = 0xE0E0E0;

                if( !this.enabled ) {
                    l = 0x808080;
                } else if( this.field_146123_n ) {
                    l = 0xFFFFA0;
                }

                this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
            }
        }
    }

    static class GuiButtonUpdate extends GuiButtonSlot {
        public GuiButtonUpdate(int id, int slotId, String name) {
            super(id, slotId, name);
        }
    }

    static class GuiButtonDetails extends GuiButtonSlot {
        public GuiButtonDetails(int id, int slotId, String name) {
            super(id, slotId, name);
        }
    }
}
