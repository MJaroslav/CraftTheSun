package com.github.mjaroslav.craftthesun.asm.transformer;

import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.reflectors.v4.Reflectors;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

@UtilityClass
public class CraftTheSunTransformer {
    @SuppressWarnings("SameParameterValue") // Temporary
    @Nullable
    @Contract("null,_,_->null")
    private AbstractInsnNode findFirstNode(@Nullable MethodNode methodNode, int type, Object... params) {
        if (methodNode != null)
            for (var node : methodNode.instructions.toArray())
                if (node.getType() == type)
                    if (type == AbstractInsnNode.METHOD_INSN) {
                        val methodInsn = (MethodInsnNode) node;
                        var name = params.length > 0 ? (String) params[0] : null;
                        if (name != null && (params.length <= 1 || (boolean) params[1]))
                            for (var unmapped : Reflectors.unmapMethodAll(name)) {
                                if (methodInsn.name.equals(unmapped))
                                    return methodInsn;
                            }
                        if (methodInsn.name.equals(name))
                            return methodInsn;
                    } else if (type == AbstractInsnNode.FIELD_INSN) {
                        val fieldInsn = (FieldInsnNode) node;
                        var name = params.length > 0 ? (String) params[0] : null;
                        if (name != null && (params.length <= 1 || (boolean) params[1]))
                            for (var unmapped : Reflectors.unmapMethodAll(name)) {
                                if (fieldInsn.name.equals(unmapped))
                                    return fieldInsn;
                            }
                        if (fieldInsn.name.equals(name))
                            return fieldInsn;
                    } else
                        throw new IllegalArgumentException("Find method for instruction with type " + type
                                + " not implemented");
        return null;
    }

    private void print(@NotNull ClassNode classNode, @NotNull String filename) {
        try {
            val path = Paths.get(".").resolve("asm").resolve("CraftTheSun");
            Files.createDirectories(path);
            val printer = new TraceClassVisitor(new PrintWriter(path.resolve(filename + ".txt").toFile()));
            classNode.accept(printer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] entityPlayer(byte[] basicClass) {
        val classNode = Reflectors.readClassFromBytes(basicClass);
        Reflectors.log("Manual pathing of \"net.minecraft.entity.player.EntityPlayer...\"");
        Reflectors.log("Trying to patch \"onLivingUpdate()V\" method...");
        entityPlayer_onLivingUpdate(classNode);
        Reflectors.log("Trying to create \"getCreatureAttribute()Lnet/minecraft/entity/EnumCreatureAttribute;\" method...");
        entityPlayer_getCreatureAttribute(classNode);
        Reflectors.log("Trying to patch \"onDeath()V\" method...");
        entityPlayer_onDeath(classNode);
        Reflectors.log("Trying to patch \"playerClone(Lnet/minecraft/entity/player/EntityPlayer;Z)V\" method...");
        entityPlayer_clonePlayer(classNode);
        Reflectors.log("Patching done");
        print(classNode, "EntityPlayer");
        return Reflectors.writeClassToBytes(classNode, COMPUTE_MAXS + COMPUTE_FRAMES);
    }

    public byte[] entityPlayerMP(byte[] basicClass) {
        val classNode = Reflectors.readClassFromBytes(basicClass);
        Reflectors.log("Manual pathing of \"net.minecraft.entity.player.EntityPlayerMP...\"");
        Reflectors.log("Trying to patch \"onDeath()V\" method...");
        entityPlayerMP_onDeath(classNode);
        Reflectors.log("Patching done");
        print(classNode, "EntityPlayerMP");
        return Reflectors.writeClassToBytes(classNode, COMPUTE_MAXS + COMPUTE_FRAMES);
    }

    public byte[] foodStats(byte[] basicClass) {
        val classNode = Reflectors.readClassFromBytes(basicClass);
        Reflectors.log("Manual pathing of \"net.minecraft.util.FoodStats...\"");
        Reflectors.log("Trying to patch \"onUpdate(Lnet/minecraft/entity/player/EntityPlayer;)V\" method...");
        foodStats_onUpdate(classNode);
        Reflectors.log("Patching done");
        print(classNode, "FoodStats");
        return Reflectors.writeClassToBytes(classNode, COMPUTE_MAXS + COMPUTE_FRAMES);
    }

    private void addNaturalRegenerationCheckBefore(@NotNull AbstractInsnNode point, @NotNull MethodNode methodNode, int aloadIndex) {
        val list = new InsnList();
        list.add(new VarInsnNode(ALOAD, aloadIndex));
        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CommonUtils.class),
                "isNaturalRegenerationEnabled", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
        val label = new LabelNode();
        list.add(new JumpInsnNode(IFEQ, label));
        methodNode.instructions.insertBefore(point.getPrevious().getPrevious(), list);
        methodNode.instructions.insert(point, label);
    }

    private void entityPlayerMP_onDeath(@NotNull ClassNode classNode) {
        val methodNode = Reflectors.findMethodNode(classNode, "onDeath", "(Lnet/minecraft/util/DamageSource;)V");
        if (methodNode == null) {
            Reflectors.log("Error, target method not found");
            return;
        }
        val point = findFirstNode(methodNode, AbstractInsnNode.METHOD_INSN, "sendChatMsg");
        var jump = findFirstNode(methodNode, AbstractInsnNode.METHOD_INSN, "getGameRuleBooleanValue");
        jump = jump != null ? jump.getNext() : null;
        if (point != null && jump instanceof JumpInsnNode) {
            val targetLabel = ((JumpInsnNode) jump).label;
            val label = new LabelNode();
            val list = new InsnList();
            list.add(new VarInsnNode(ALOAD, 0));
            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CommonUtils.class),
                    "isPlayerShouldDropInventory", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
            list.add(new JumpInsnNode(IFEQ, label));
            methodNode.instructions.insert(point.getNext(), list);
            methodNode.instructions.insert(targetLabel, label);
            Reflectors.log("Success");
        } else Reflectors.log("Error, inject point not found");
    }

    private AbstractInsnNode getPrev(@NotNull AbstractInsnNode node, int count) {
        for (var i = 0; i < count; i++)
            node = node.getPrevious();
        return node;
    }

    private AbstractInsnNode getNext(@NotNull AbstractInsnNode node, int count) {
        for (var i = 0; i < count; i++)
            node = node.getNext();
        return node;
    }

    private void entityPlayer_clonePlayer(@NotNull ClassNode classNode) {
        val methodNode = Reflectors.findMethodNode(classNode, "clonePlayer", "(Lnet/minecraft/entity/player/EntityPlayer;Z)V");
        if (methodNode == null) {
            Reflectors.log("Error, target method not found");
            return;
        }
        var point = findFirstNode(methodNode, AbstractInsnNode.METHOD_INSN, "getGameRuleBooleanValue");
        var jump = point != null ? point.getNext() : null;
        jump = point != null ? point.getNext() : null;
        jump = jump != null ? jump.getNext() : null;
        if (point != null && jump instanceof LabelNode) {
            val targetLabel = ((LabelNode) jump);
            var list = new InsnList();
            list.add(new VarInsnNode(ALOAD, 1));
            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CommonUtils.class),
                    "isPlayerShouldDropInventory", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
            list.add(new JumpInsnNode(IFEQ, targetLabel));
            methodNode.instructions.insertBefore(getPrev(point, 4), list);
            //methodNode.instructions.insert(targetLabel, label);
            Reflectors.log("Success");
        } else Reflectors.log("Error, inject point not found");
    }

    private void entityPlayer_onDeath(@NotNull ClassNode classNode) {
        val methodNode = Reflectors.findMethodNode(classNode, "onDeath", "(Lnet/minecraft/util/DamageSource;)V");
        if (methodNode == null) {
            Reflectors.log("Error, target method not found");
            return;
        }
        val point = findFirstNode(methodNode, AbstractInsnNode.METHOD_INSN, "getGameRuleBooleanValue");
        val jump = point != null ? point.getNext() : null;
        if (point != null && jump instanceof JumpInsnNode) {
            val targetLabel = ((JumpInsnNode) jump).label;
            val label = new LabelNode();
            val list = new InsnList();
            list.add(new VarInsnNode(ALOAD, 0));
            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CommonUtils.class),
                    "isPlayerShouldDropInventory", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
            list.add(new JumpInsnNode(IFEQ, label));
            methodNode.instructions.insertBefore(getPrev(point, 4), list);
            methodNode.instructions.insert(targetLabel, label);
            Reflectors.log("Success");
        } else Reflectors.log("Error, inject point not found");
    }

    private void entityPlayer_onLivingUpdate(@NotNull ClassNode classNode) {
        val methodNode = Reflectors.findMethodNode(classNode, "onLivingUpdate", "()V");
        if (methodNode == null) {
            Reflectors.log("Error, target method not found");
            return;
        }
        val point = (MethodInsnNode) findFirstNode(methodNode, AbstractInsnNode.METHOD_INSN, "heal");
        if (point != null) {
            addNaturalRegenerationCheckBefore(point, methodNode, 0);
            Reflectors.log("Success");
        } else Reflectors.log("Error, inject point not found");
    }

    public void entityPlayer_getCreatureAttribute(@NotNull ClassNode classNode) {
        val visitor = classNode.visitMethod(ACC_PUBLIC, Reflectors.unmapMethod("getCreatureAttribute"),
                "()Lnet/minecraft/entity/EnumCreatureAttribute;", null, null);
        visitor.visitCode();
        visitor.visitVarInsn(ALOAD, 0);
        visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(CommonUtils.class), "getPlayerCreatureAttribute",
                "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/entity/EnumCreatureAttribute;", false);
        visitor.visitInsn(ARETURN);
        Reflectors.log("Success");
    }

    private void foodStats_onUpdate(@NotNull ClassNode classNode) {
        val methodNode = Reflectors.findMethodNode(classNode, "onUpdate",
                "(Lnet/minecraft/entity/player/EntityPlayer;)V");
        if (methodNode == null) {
            Reflectors.log("Error, target method not found");
            return;
        }
        val point = (MethodInsnNode) findFirstNode(methodNode, AbstractInsnNode.METHOD_INSN, "heal");
        if (point != null) {
            addNaturalRegenerationCheckBefore(point, methodNode, 1);
            Reflectors.log("Success");
        } else Reflectors.log("Error, inject point not found");
    }
}
