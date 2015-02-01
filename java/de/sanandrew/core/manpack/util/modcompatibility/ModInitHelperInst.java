/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.core.manpack.util.modcompatibility;

import cpw.mods.fml.common.Loader;
import de.sanandrew.core.manpack.mod.ModCntManPack;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;

public class ModInitHelperInst
{
    private final IModInitHelper helperInst;

    private ModInitHelperInst(IModInitHelper instance) {
        this.helperInst = instance;
    }

    @SuppressWarnings("unchecked")
    public static ModInitHelperInst loadWhenModAvailable(String modId, String helperClass) {
        if( modId == null || modId.isEmpty() ) {
            ModCntManPack.MOD_LOG.log(Level.FATAL, "Cannot check for null/empty mod ID!");
            throw new RuntimeException();
        }

        if( Loader.isModLoaded(modId) ) {
            try {
                Class helperClassInst = Class.forName(helperClass);
                if( IModInitHelper.class.isAssignableFrom(helperClassInst) ) {
                    IModInitHelper inst = (IModInitHelper) helperClassInst.getConstructor().newInstance();
                    ModCntManPack.MOD_LOG.log(Level.INFO,"Mod %s is available. Initialized compatibillity class %s.", modId, helperClass);
                    return new ModInitHelperInst(inst);
                } else {
                    ModCntManPack.MOD_LOG.log(Level.ERROR, "Class %s is not a subclass of IModInitHelper! This is a serious modder error!", helperClass);
                    throw new RuntimeException();
                }
            } catch( ClassNotFoundException | InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e ) {
                ModCntManPack.MOD_LOG.log(Level.ERROR, "Unexpected exception while trying to build instance of compatibility class!");
                return new ModInitHelperInst(new EmptyModInitHelper());
//                throw new RuntimeException(e);
            }
        } else {
            ModCntManPack.MOD_LOG.log(Level.INFO, "Mod %s is unavailable. Skipping initialization of compatibility class %s!", modId, helperClass);
            return new ModInitHelperInst(new EmptyModInitHelper());
        }
    }

    public void preInitialize() {
        this.helperInst.preInitialize();
    }

    public void initialize() {
        this.helperInst.initialize();
    }

    public void postInitialize() {
        this.helperInst.postInitialize();
    }

    public static final class EmptyModInitHelper implements IModInitHelper {
        @Override public void preInitialize() { }
        @Override public void initialize() { }
        @Override public void postInitialize() { }
    }
}
