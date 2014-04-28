package de.sanandrew.core.manpack.transformer;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

import cpw.mods.fml.common.FMLLog;

import de.sanandrew.core.manpack.mod.ModCntManPack;

public class TransformHorseArmor implements IClassTransformer, Opcodes
{
    private String RF_dataWatcher;
    private String RF_horseChest;
    private String RF_field110280bR;
    private String RF_field110286bQ;
    private String RF_ironShovel;
    private String RM_updateObject;
    private String RM_getStackInSlot;
    private String RM_addObject;
    private String RM_getItem;
    private String RM_getWatchableObjectItemStack;
    private String RM_isItemEqual;
    private String RM_playSound;
    private String RM_getUnlocalizedName;
    private String NTM_interact;
    private String NTM_func146085a;
    private String NTM_entityInit;
    private String NTM_func110232cE;
    private String NTM_setHorseTexturePaths;
    private String NTM_addObject;
    private String NTM_isHorseSaddled;
    private String NTM_onInventoryChanged;
    private String NTM_playSound;
    private String NTM_func110241cb;
    private String NTM_getTotalArmorValue;
    private String NTC_EntityHorse;
    private String NTC_DataWatcher;
    private String NTC_INIT_Items;
    private String NTC_AnimalChest;
    private String NTC_Item;
    private String NTC_InventoryBasic;
    private String NTF_horseChest;
    private String NTF_dataWatcher;
    private String NTF_iron_horse_armor;
    private String NTF_diamondHorseArmor;
    private String NTF_func110241cb;
    private String NTF_armorValues;
    private String NTC_EntityPlayer;

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if( transformedName.equals("net.minecraft.entity.passive.EntityHorse") ) {
		    this.initiateMappings();
			return this.transformHorse(bytes);
		}

		return bytes;
	}

	private void initiateMappings() {
        this.RF_dataWatcher = ASMHelper.getRemappedMF("dataWatcher", "field_70180_af");
        this.RF_horseChest = ASMHelper.getRemappedMF("horseChest",  "field_110296_bG");
        this.RF_field110280bR = ASMHelper.getRemappedMF("field_110280_bR", "field_110280_bR");
        this.RF_field110286bQ = ASMHelper.getRemappedMF("field_110286_bQ", "field_110286_bQ");
        this.RF_ironShovel = ASMHelper.getRemappedMF("iron_shovel", "field_151037_a");

        this.RM_updateObject = ASMHelper.getRemappedMF("updateObject", "func_75692_b");
        this.RM_getStackInSlot = ASMHelper.getRemappedMF("getStackInSlot", "func_70301_a");
        this.RM_addObject = ASMHelper.getRemappedMF("addObject", "func_75682_a");
        this.RM_getItem = ASMHelper.getRemappedMF("getItem", "func_77973_b");
        this.RM_getWatchableObjectItemStack = ASMHelper.getRemappedMF("getWatchableObjectItemStack", "func_82710_f");
        this.RM_isItemEqual = ASMHelper.getRemappedMF("isItemEqual", "func_77969_a");
        this.RM_playSound = ASMHelper.getRemappedMF("playSound", "func_85030_a");
        this.RM_getUnlocalizedName = ASMHelper.getRemappedMF("getUnlocalizedName", "func_77977_a");

        this.NTM_interact = ASMHelper.getRemappedMF("interact", "func_70085_c");
        this.NTM_func146085a = ASMHelper.getRemappedMF("func_146085_a", "func_146085_a");
        this.NTM_entityInit = ASMHelper.getRemappedMF("entityInit", "func_70088_a");
        this.NTM_func110232cE = ASMHelper.getRemappedMF("func_110232_cE", "func_110232_cE");
        this.NTM_setHorseTexturePaths = ASMHelper.getRemappedMF("setHorseTexturePaths", "func_110247_cG");
        this.NTM_addObject = ASMHelper.getRemappedMF("addObject", "func_75682_a");
        this.NTM_isHorseSaddled = ASMHelper.getRemappedMF("isHorseSaddled", "func_110257_ck");
        this.NTM_onInventoryChanged = ASMHelper.getRemappedMF("onInventoryChanged", "func_76316_a");
        this.NTM_playSound = ASMHelper.getRemappedMF("playSound", "func_85030_a");
        this.NTM_func110241cb = ASMHelper.getRemappedMF("func_110241_cb", "func_110241_cb");
        this.NTM_getTotalArmorValue = ASMHelper.getRemappedMF("getTotalArmorValue", "func_70658_aO");

        this.NTC_EntityHorse = "net/minecraft/entity/passive/EntityHorse";
        this.NTC_DataWatcher = "net/minecraft/entity/DataWatcher";
        this.NTC_INIT_Items = "net/minecraft/init/Items";
        this.NTC_AnimalChest = "net/minecraft/inventory/AnimalChest";
        this.NTC_Item = "net/minecraft/item/Item";
        this.NTC_EntityPlayer = "net/minecraft/entity/player/EntityPlayer";
        this.NTC_InventoryBasic = "net/minecraft/inventory/InventoryBasic";

        this.NTF_horseChest = ASMHelper.getRemappedMF("horseChest", "field_110296_bG");
        this.NTF_dataWatcher = ASMHelper.getRemappedMF("dataWatcher", "field_70180_af");
        this.NTF_iron_horse_armor = ASMHelper.getRemappedMF("iron_horse_armor", "field_151138_bX");
        this.NTF_diamondHorseArmor = ASMHelper.getRemappedMF("diamond_horse_armor", "field_151125_bZ");
        this.NTF_armorValues = ASMHelper.getRemappedMF("armorValues", "field_110272_by");
        this.NTF_func110241cb = ASMHelper.getRemappedMF("func_110241_cb", "func_110241_cb");
	}

    private byte[] transformHorse(byte[] bytes) {
		ClassNode clazz = ASMHelper.createClassNode(bytes);

		MethodNode method = this.injectMethodSCAI();
		clazz.methods.add(method);

		method = this.injectMethodGCAI();
		clazz.methods.add(method);

		this.transformInteract(ASMHelper.findMethod(this.NTM_interact, "(L" + this.NTC_EntityPlayer + ";)Z", clazz));
        this.transformIsValidArmor(ASMHelper.findMethod(this.NTM_func146085a, "(L" + this.NTC_Item + ";)Z", clazz));
        this.transformEntityInit(ASMHelper.findMethod(this.NTM_entityInit, "()V", clazz));
        this.transformUpdateHorseSlots(ASMHelper.findMethod(this.NTM_func110232cE, "()V", clazz));
        this.transformOnInvChanged(ASMHelper.findMethod(this.NTM_onInventoryChanged, "(L" + this.NTC_InventoryBasic + ";)V", clazz));
        this.transformGetTotalArmorValue(ASMHelper.findMethod(this.NTM_getTotalArmorValue, "()I", clazz));

        try {
            this.transformArmorTexture(ASMHelper.findMethod(this.NTM_setHorseTexturePaths, "()V", clazz));
        } catch( ASMHelper.MethodNotFoundException e ) {
            FMLLog.log(ModCntManPack.MOD_LOG, Level.INFO, "Running on dedicated server, no need to transform Method %s!", this.NTM_setHorseTexturePaths);
        }

	    bytes = ASMHelper.createBytes(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

		return bytes;
	}

    private MethodNode injectMethodGCAI() {
        MethodNode method = new MethodNode(ACC_PRIVATE, "_SAP_getCustomArmorItem", "()Lnet/minecraft/item/ItemStack;", null, null);
        method.visitCode();
        Label l0 = new Label();
        method.visitLabel(l0);
        method.visitVarInsn(ALOAD, 0);
        method.visitFieldInsn(GETFIELD, "net/minecraft/entity/passive/EntityHorse", this.RF_dataWatcher, "Lnet/minecraft/entity/DataWatcher;");
        method.visitIntInsn(BIPUSH, 23);
        method.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/DataWatcher", this.RM_getWatchableObjectItemStack, "(I)Lnet/minecraft/item/ItemStack;");
        method.visitInsn(ARETURN);
        Label l1 = new Label();
        method.visitLabel(l1);
        method.visitLocalVariable("this", "Lnet/minecraft/entity/passive/EntityHorse;", null, l0, l1, 0);
        method.visitMaxs(2, 1);
        method.visitEnd();

        return method;
    }

    private MethodNode injectMethodSCAI() {
        MethodNode method = new MethodNode(ACC_PRIVATE, "_SAP_setCustomArmorItem", "(Lnet/minecraft/item/ItemStack;)V", null, null);
        method.visitCode();
        Label l0 = new Label();
        method.visitLabel(l0);
        method.visitVarInsn(ALOAD, 1);
        Label l1 = new Label();
        method.visitJumpInsn(IFNONNULL, l1);
        method.visitTypeInsn(NEW, "net/minecraft/item/ItemStack");
        method.visitInsn(DUP);
        method.visitFieldInsn(GETSTATIC, "net/minecraft/init/Items", this.RF_ironShovel, "Lnet/minecraft/item/Item;");
        method.visitInsn(ICONST_0);
        method.visitMethodInsn(INVOKESPECIAL, "net/minecraft/item/ItemStack", "<init>", "(Lnet/minecraft/item/Item;I)V");
        method.visitVarInsn(ASTORE, 1);
        method.visitLabel(l1);
        method.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        method.visitVarInsn(ALOAD, 0);
        method.visitFieldInsn(GETFIELD, "net/minecraft/entity/passive/EntityHorse", this.RF_dataWatcher, "Lnet/minecraft/entity/DataWatcher;");
        method.visitIntInsn(BIPUSH, 23);
        method.visitVarInsn(ALOAD, 1);
        method.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/DataWatcher", this.RM_updateObject, "(ILjava/lang/Object;)V");
        Label l3 = new Label();
        method.visitLabel(l3);
        method.visitInsn(RETURN);
        Label l4 = new Label();
        method.visitLabel(l4);
        method.visitLocalVariable("this", "Lnet/minecraft/entity/passive/EntityHorse;", null, l0, l3, 0);
        method.visitLocalVariable("stack", "Lnet/minecraft/item/ItemStack;", null, l0, l3, 1);
        method.visitMaxs(5, 2);
        method.visitEnd();

        return method;
    }

    private void transformGetTotalArmorValue(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));
        needle.add(new FieldInsnNode(GETSTATIC, this.NTC_EntityHorse, this.NTF_armorValues, "[I"));
        needle.add(new VarInsnNode(ALOAD, 0));
        needle.add(new MethodInsnNode(INVOKEVIRTUAL, this.NTC_EntityHorse, this.NTM_func110241cb, "()I"));

        AbstractInsnNode pointer = ASMHelper.findFirstNodeFromNeedle(method.instructions, needle);

        InsnList newInstr = new InsnList();

        newInstr.add(new LabelNode());
        newInstr.add(new VarInsnNode(ALOAD, 0));
        newInstr.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/entity/passive/EntityHorse", "_SAP_getCustomArmorItem", "()Lnet/minecraft/item/ItemStack;"));
        newInstr.add(new VarInsnNode(ASTORE, 1));
        newInstr.add(new LabelNode());
        newInstr.add(new VarInsnNode(ALOAD, 1));
        newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", this.RM_getItem, "()Lnet/minecraft/item/Item;"));
        newInstr.add(new TypeInsnNode(INSTANCEOF, "de/sanandrew/core/manpack/helpers/ItemHorseArmor"));
        LabelNode l2 = new LabelNode();
        newInstr.add(new JumpInsnNode(IFEQ, l2));
        newInstr.add(new LabelNode());
        newInstr.add(new VarInsnNode(ALOAD, 1));
        newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", this.RM_getItem, "()Lnet/minecraft/item/Item;"));
        newInstr.add(new TypeInsnNode(CHECKCAST, "de/sanandrew/core/manpack/helpers/ItemHorseArmor"));
        newInstr.add(new VarInsnNode(ALOAD, 0));
        newInstr.add(new VarInsnNode(ALOAD, 1));
        newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "de/sanandrew/core/manpack/helpers/ItemHorseArmor", "getArmorValue", "(Lnet/minecraft/entity/passive/EntityHorse;Lnet/minecraft/item/ItemStack;)I"));
        newInstr.add(new InsnNode(IRETURN));
        newInstr.add(l2);

        method.instructions.insertBefore(pointer, newInstr);
    }

    private void transformOnInvChanged(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add(new VarInsnNode(ALOAD, 0));
        needle.add(new MethodInsnNode(INVOKEVIRTUAL, this.NTC_EntityHorse, this.NTM_isHorseSaddled, "()Z"));
        needle.add(new VarInsnNode(ISTORE, 3));

        AbstractInsnNode pointer = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);

        InsnList newInstr = new InsnList();

        newInstr.add(new LabelNode());
        newInstr.add(new VarInsnNode(ALOAD, 0));
        newInstr.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/entity/passive/EntityHorse", "_SAP_getCustomArmorItem", "()Lnet/minecraft/item/ItemStack;"));
        newInstr.add(new VarInsnNode(ASTORE, 4));

        method.instructions.insert(pointer, newInstr);

        needle = new InsnList();
        needle.add(new LdcInsnNode("mob.horse.armor"));
        needle.add(new LdcInsnNode(new Float("0.5")));
        needle.add(new InsnNode(FCONST_1));
        needle.add(new MethodInsnNode(INVOKEVIRTUAL, this.NTC_EntityHorse, this.NTM_playSound, "(Ljava/lang/String;FF)V"));
        needle.add(new LabelNode());

        pointer = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);

        newInstr = new InsnList();
        newInstr.add(new VarInsnNode(ALOAD, 4));
        newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", this.RM_getItem, "()Lnet/minecraft/item/Item;"));
        newInstr.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Items", this.RF_ironShovel, "Lnet/minecraft/item/Item;"));
        LabelNode l9 = new LabelNode();
        newInstr.add(new JumpInsnNode(IF_ACMPNE, l9));
        newInstr.add(new VarInsnNode(ALOAD, 4));
        newInstr.add(new VarInsnNode(ALOAD, 0));
        newInstr.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/entity/passive/EntityHorse", "_SAP_getCustomArmorItem", "()Lnet/minecraft/item/ItemStack;"));
        newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", this.RM_isItemEqual, "(Lnet/minecraft/item/ItemStack;)Z"));
        newInstr.add(new JumpInsnNode(IFNE, l9));
        newInstr.add(new LabelNode());
        newInstr.add(new VarInsnNode(ALOAD, 0));
        newInstr.add(new LdcInsnNode("mob.horse.armor"));
        newInstr.add(new LdcInsnNode(new Float("0.5")));
        newInstr.add(new InsnNode(FCONST_1));
        newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/passive/EntityHorse", this.RM_playSound, "(Ljava/lang/String;FF)V"));
        newInstr.add(l9);

        method.instructions.insert(pointer, newInstr);
    }

	private void transformArmorTexture(MethodNode method) {
	    InsnList needle = new InsnList();
	    needle.add(new VarInsnNode(ALOAD, 0));
	    needle.add(new MethodInsnNode(INVOKEVIRTUAL, this.NTC_EntityHorse, this.NTF_func110241cb, "()I"));
	    needle.add(new VarInsnNode(ISTORE, 3));

	    AbstractInsnNode node = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);

        InsnList newInstr = new InsnList();
	    LabelNode l17 = new LabelNode();
	    newInstr.add(l17);
	    newInstr.add(new VarInsnNode(ALOAD, 0));
	    newInstr.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/entity/passive/EntityHorse", "_SAP_getCustomArmorItem", "()Lnet/minecraft/item/ItemStack;"));
	    newInstr.add(new VarInsnNode(ASTORE, 4));
	    LabelNode l18 = new LabelNode();
	    newInstr.add(l18);
	    newInstr.add(new VarInsnNode(ALOAD, 4));
	    newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", this.RM_getItem, "()Lnet/minecraft/item/Item;"));
	    newInstr.add(new TypeInsnNode(INSTANCEOF, "de/sanandrew/core/manpack/helpers/ItemHorseArmor"));
	    LabelNode l19 = new LabelNode();
	    newInstr.add(new JumpInsnNode(IFEQ, l19));
	    LabelNode l20 = new LabelNode();
	    newInstr.add(l20);
	    newInstr.add(new VarInsnNode(ALOAD, 0));
	    newInstr.add(new FieldInsnNode(GETFIELD, "net/minecraft/entity/passive/EntityHorse", this.RF_field110280bR, "[Ljava/lang/String;"));
	    newInstr.add(new InsnNode(ICONST_2));
	    newInstr.add(new VarInsnNode(ALOAD, 4));
	    newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", this.RM_getItem, "()Lnet/minecraft/item/Item;"));
	    newInstr.add(new TypeInsnNode(CHECKCAST, "de/sanandrew/core/manpack/helpers/ItemHorseArmor"));
	    newInstr.add(new VarInsnNode(ALOAD, 0));
	    newInstr.add(new VarInsnNode(ALOAD, 4));
	    newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "de/sanandrew/core/manpack/helpers/ItemHorseArmor", "getArmorTexture", "(Lnet/minecraft/entity/passive/EntityHorse;Lnet/minecraft/item/ItemStack;)Ljava/lang/String;"));
	    newInstr.add(new InsnNode(AASTORE));
	    LabelNode l21 = new LabelNode();
	    newInstr.add(l21);
	    newInstr.add(new VarInsnNode(ALOAD, 0));
	    newInstr.add(new TypeInsnNode(NEW, "java/lang/StringBuilder"));
	    newInstr.add(new InsnNode(DUP));
	    newInstr.add(new VarInsnNode(ALOAD, 0));
	    newInstr.add(new FieldInsnNode(GETFIELD, "net/minecraft/entity/passive/EntityHorse", this.RF_field110286bQ, "Ljava/lang/String;"));
	    newInstr.add(new MethodInsnNode(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;"));
	    newInstr.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V"));
	    newInstr.add(new LdcInsnNode("cst-"));
	    newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
	    newInstr.add(new VarInsnNode(ALOAD, 4));
	    newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", this.RM_getUnlocalizedName, "()Ljava/lang/String;"));
	    newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
	    newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;"));
	    newInstr.add(new FieldInsnNode(PUTFIELD, "net/minecraft/entity/passive/EntityHorse", this.RF_field110286bQ, "Ljava/lang/String;"));
	    newInstr.add(new InsnNode(RETURN));
	    newInstr.add(l19);

	    method.instructions.insert(node, newInstr);
	}

	private void transformUpdateHorseSlots(MethodNode method) {
	    InsnList needle = new InsnList();
	    needle.add(new VarInsnNode(ALOAD, 0));
	    needle.add(new VarInsnNode(ALOAD, 0));
	    needle.add(new FieldInsnNode(GETFIELD, this.NTC_EntityHorse, this.NTF_horseChest, "L" + this.NTC_AnimalChest + ";"));
	    needle.add(new InsnNode(ICONST_1));

	    AbstractInsnNode pointer = ASMHelper.findFirstNodeFromNeedle(method.instructions, needle);

	    InsnList newInstr = new InsnList();
	    newInstr.add(new VarInsnNode(ALOAD, 0));
	    newInstr.add(new VarInsnNode(ALOAD, 0));
	    newInstr.add(new FieldInsnNode(GETFIELD, "net/minecraft/entity/passive/EntityHorse", this.RF_horseChest, "Lnet/minecraft/inventory/AnimalChest;"));
	    newInstr.add(new InsnNode(ICONST_1));
	    newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/inventory/AnimalChest", this.RM_getStackInSlot, "(I)Lnet/minecraft/item/ItemStack;"));
	    newInstr.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/entity/passive/EntityHorse", "_SAP_setCustomArmorItem", "(Lnet/minecraft/item/ItemStack;)V"));
	    newInstr.add(new LabelNode());

	    method.instructions.insertBefore(pointer, newInstr);
	}

	private void transformEntityInit(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add(new VarInsnNode(ALOAD, 0));
        needle.add(new FieldInsnNode(GETFIELD, this.NTC_EntityHorse, this.NTF_dataWatcher, "L" + this.NTC_DataWatcher + ";"));
        needle.add(new IntInsnNode(BIPUSH, 22));
        needle.add(new InsnNode(ICONST_0));
        needle.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"));
        needle.add(new MethodInsnNode(INVOKEVIRTUAL, this.NTC_DataWatcher, this.NTM_addObject, "(ILjava/lang/Object;)V"));

        AbstractInsnNode pointer = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);

        InsnList newInstr = new InsnList();
        newInstr.add(new LabelNode());
        newInstr.add(new VarInsnNode(ALOAD, 0));
        newInstr.add(new FieldInsnNode(GETFIELD, "net/minecraft/entity/passive/EntityHorse", this.RF_dataWatcher, "Lnet/minecraft/entity/DataWatcher;"));
        newInstr.add(new IntInsnNode(BIPUSH, 23));
        newInstr.add(new TypeInsnNode(NEW, "net/minecraft/item/ItemStack"));
        newInstr.add(new InsnNode(DUP));
        newInstr.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Items", this.RF_ironShovel, "Lnet/minecraft/item/Item;"));
        newInstr.add(new InsnNode(ICONST_0));
        newInstr.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/item/ItemStack", "<init>", "(Lnet/minecraft/item/Item;I)V"));
        newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/DataWatcher", this.RM_addObject, "(ILjava/lang/Object;)V"));

        method.instructions.insert(pointer, newInstr);
	}

	private void transformIsValidArmor(MethodNode method) {
	    InsnList needle = new InsnList();
	    needle.add(new VarInsnNode(ALOAD, 0));
	    needle.add(new FieldInsnNode(GETSTATIC, this.NTC_INIT_Items, this.NTF_iron_horse_armor, "L" + this.NTC_Item + ";"));
	    needle.add(new JumpInsnNode(IF_ACMPEQ, new LabelNode()));

	    AbstractInsnNode node = ASMHelper.findFirstNodeFromNeedle(method.instructions, needle);

	    InsnList newInstr = new InsnList();
        newInstr.add(new VarInsnNode(ALOAD, 0));
        newInstr.add(new TypeInsnNode(INSTANCEOF, "de/sanandrew/core/manpack/helpers/ItemHorseArmor"));
        LabelNode ln = new LabelNode();
        newInstr.add(new JumpInsnNode(IFEQ, ln));
        newInstr.add(new LabelNode());
        newInstr.add(new InsnNode(ICONST_1));
        newInstr.add(new InsnNode(IRETURN));
        newInstr.add(ln);
        newInstr.add(new FrameNode(F_SAME, 0, null, 0, null));

	    method.instructions.insertBefore(node, newInstr);
	}

	private void transformInteract(MethodNode method) {
	    InsnList needle = new InsnList();
	    needle.add(new FieldInsnNode(GETSTATIC, this.NTC_INIT_Items, this.NTF_diamondHorseArmor, "L" + this.NTC_Item + ";"));
	    needle.add(new JumpInsnNode(IF_ACMPNE, new LabelNode()));
	    needle.add(new LabelNode());
	    needle.add(new LineNumberNode(801, new LabelNode()));
	    needle.add(new InsnNode(ICONST_3));
	    needle.add(new VarInsnNode(ISTORE, 4));
	    needle.add(new LabelNode());
	    needle.add(new LineNumberNode(804, new LabelNode()));
	    needle.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

	    AbstractInsnNode node = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);

	    InsnList newInstr = new InsnList();
	    newInstr.add(new VarInsnNode(ALOAD, 2));
        newInstr.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", this.RM_getItem, "()Lnet/minecraft/item/Item;"));
        newInstr.add(new TypeInsnNode(INSTANCEOF, "de/sanandrew/core/manpack/helpers/ItemHorseArmor"));
        LabelNode l8 = new LabelNode();
        newInstr.add(new JumpInsnNode(IFEQ, l8));
        newInstr.add(new InsnNode(ICONST_4));
        newInstr.add(new VarInsnNode(ISTORE, 4));
        newInstr.add(l8);
        newInstr.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

	    method.instructions.insert(node, newInstr);
	}
}
