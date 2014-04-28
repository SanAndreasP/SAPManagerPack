package de.sanandrew.core.manpack;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

import de.sanandrew.core.manpack.mod.ModCntManPack;
import de.sanandrew.core.manpack.transformer.ASMHelper;
import de.sanandrew.core.manpack.transformer.TransformBadPotionsATN;
import de.sanandrew.core.manpack.transformer.TransformELBAttackingPlayer;
import de.sanandrew.core.manpack.transformer.TransformEntityThrowable;
import de.sanandrew.core.manpack.transformer.TransformHorseArmor;
import de.sanandrew.core.manpack.transformer.TransformPlayerDismountCtrl;

@TransformerExclusions({"de.sanandrew.core.manpack.transformer", "de.sanandrew.core.manpack.ManPackLoadingPlugin"})
@SortingIndex(1001)
public class ManPackLoadingPlugin implements IFMLLoadingPlugin
{
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
			TransformBadPotionsATN.class.getName(),
			TransformEntityThrowable.class.getName(),
			TransformELBAttackingPlayer.class.getName(),
			TransformPlayerDismountCtrl.class.getName(),
			TransformHorseArmor.class.getName()
		};
	}

	@Override
	public String getModContainerClass() {
		return ModCntManPack.class.getName();
	}

	@Override
	public String getSetupClass() {
		return ManPackSetupClass.class.getName();
	}

	@Override
	public void injectData(Map<String, Object> data) {
		ASMHelper.isMCP = !((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
		ModCntManPack.modLocation = (File)data.get("coremodLocation");
	}

	@SuppressWarnings("rawtypes")
	public static boolean isServer() {
		try {
			Class clazz = Class.forName("net.minecraft.client.Minecraft");
			if( clazz != null ) {
				clazz = null;
				return false;
			}
		} catch (ClassNotFoundException e) {
			return true;
		}
		return true;
	}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
