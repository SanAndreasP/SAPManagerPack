package de.sanandrew.core.manpack.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/** This can easily be done now with Reflection **/
@Deprecated
public final class TransformAccessors
{
	public static EntityPlayer getELAttackingPlayer(EntityLivingBase entity) {
		return null;
	}

	public static void setELAttackingPlayer(EntityPlayer player, EntityLivingBase entity) {

	}

	public static int getELRecentlyHit(EntityLivingBase entity) {
		return 0;
	}

	public static void setELRecentlyHit(int hit, EntityLivingBase entity) {

	}
}
