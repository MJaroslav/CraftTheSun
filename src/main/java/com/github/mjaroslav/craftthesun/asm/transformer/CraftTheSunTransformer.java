package com.github.mjaroslav.craftthesun.asm.transformer;

import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.reflectors.v2.Reflectors;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Type;

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
                        name = name != null ? params.length <= 1 || (boolean) params[1]
                                ? Reflectors.unmapMethod(name) : name : null;
                        if (methodInsn.name.equals(name))
                            return methodInsn;
                    } else
                        throw new IllegalArgumentException("Find method for instruction with type " + type
                                + " not implemented");
        return null;
    }

    public byte[] entityPlayer(byte[] basicClass) {
        val classNode = Reflectors.readClassFromBytes(basicClass);
        entityPlayer_onLivingUpdate(classNode);
        return Reflectors.writeClassToBytes(classNode, COMPUTE_MAXS + COMPUTE_FRAMES);
    }

    public byte[] foodStats(byte[] basicClass) {
        val classNode = Reflectors.readClassFromBytes(basicClass);
        foodStats_onUpdate(classNode);
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

    private void entityPlayer_onLivingUpdate(@NotNull ClassNode classNode) {
        val methodNode = Reflectors.findMethodNode(classNode, "onLivingUpdate", "()V");
        val point = (MethodInsnNode) findFirstNode(methodNode, AbstractInsnNode.METHOD_INSN, "heal");
        if (point != null)
            addNaturalRegenerationCheckBefore(point, methodNode, 0);
    }

    private void foodStats_onUpdate(@NotNull ClassNode classNode) {
        val methodNode = Reflectors.findMethodNode(classNode, "onUpdate",
                "(Lnet/minecraft/entity/player/EntityPlayer;)V");
        val point = (MethodInsnNode) findFirstNode(methodNode, AbstractInsnNode.METHOD_INSN, "heal");
        if (point != null)
            addNaturalRegenerationCheckBefore(point, methodNode, 1);
    }
}
