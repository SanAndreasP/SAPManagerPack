package de.sanandrew.core.manpack.mod.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.managers.SAPUpdateManager;
import de.sanandrew.core.manpack.mod.CommonProxy;
import de.sanandrew.core.manpack.mod.client.event.EventWorldRenderLast;
import de.sanandrew.core.manpack.mod.client.particle.SAPEffectRenderer;
import de.sanandrew.core.manpack.util.client.RenderBlockGlowOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private static final UpdateOverlayManager UPDATE_OVERLY_MGR = new UpdateOverlayManager();
    public static final KeyBinding KEY_UPDATE_GUI = new KeyBinding("key.sapmp.updateKey", Keyboard.KEY_U, "key.categories.misc");

	@Override
    public void registerRenderStuff() {
        RenderBlockGlowOverlay.renderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(RenderBlockGlowOverlay.renderID, new RenderBlockGlowOverlay());

        this.applyCapesToCertainPlayers();

        MinecraftForge.EVENT_BUS.register(UPDATE_OVERLY_MGR);
        FMLCommonHandler.instance().bus().register(UPDATE_OVERLY_MGR);

        ClientRegistry.registerKeyBinding(KEY_UPDATE_GUI);
        KeyHandler kHandler = new KeyHandler();
        FMLCommonHandler.instance().bus().register(kHandler);
        MinecraftForge.EVENT_BUS.register(kHandler);

        SAPEffectRenderer.initialize(Minecraft.getMinecraft().getTextureManager());
        EventWorldRenderLast wrlEvent = new EventWorldRenderLast();
        MinecraftForge.EVENT_BUS.register(wrlEvent);
        FMLCommonHandler.instance().bus().register(wrlEvent);
	}

	@Override
	public void updOverlayAddUpdMgr(SAPUpdateManager mgr, String version) {
	    UPDATE_OVERLY_MGR.addUpdate(mgr, version);
	}

	@Override
	public void registerPackets() {
		super.registerPackets();
	}

	private void applyCapesToCertainPlayers() {
        //TODO: removed hook. There will be something better for our modders ;)
//	    String capeURL = "http://i.imgur.com/4SpmGSv.png";
//        String[] owners = {"sanandreasMC", "SilverChiren"};
//
//        ThreadDownloadImageData image = new ThreadDownloadImageData(capeURL, null, null);
//
//        for( String username : owners ) {
//            Minecraft.getMinecraft().renderEngine.loadTexture(new ResourceLocation("cloaks/" + username), image);
//        }
	}
}
