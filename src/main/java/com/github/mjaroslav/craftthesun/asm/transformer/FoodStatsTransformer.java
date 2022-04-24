package com.github.mjaroslav.craftthesun.asm.transformer;

import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.reflectors.v2.Reflectors;
import lombok.val;
import lombok.var;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Type;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class FoodStatsTransformer {
    public static byte[] transform(byte[] basicClass) {
        val classNode = Reflectors.readClassFromBytes(basicClass);
        val methodNode = Reflectors.findMethodNode(classNode, "onUpdate",
                "(Lnet/minecraft/entity/player/EntityPlayer;)V");
        MethodInsnNode point = null;
        if (methodNode != null)
            for (var node : methodNode.instructions.toArray())
                if (node.getType() == AbstractInsnNode.METHOD_INSN) {
                    val i = (MethodInsnNode) node;
                    if (i.name.equals(Reflectors.unmapMethod("heal"))) {
                        point = i;
                        break;
                    }
                }
        if (point != null) {
            val list = new InsnList();
            list.add(new VarInsnNode(ALOAD, 1));
            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CommonUtils.class),
                    "isNaturalRegenerationEnabled", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
            val label = new LabelNode();
            list.add(new JumpInsnNode(IFEQ, label));
            methodNode.instructions.insertBefore(point.getPrevious().getPrevious(), list);
            methodNode.instructions.insert(point, label);
            return Reflectors.writeClassToBytes(classNode, COMPUTE_MAXS + COMPUTE_FRAMES);
        }
        return basicClass;
    }
}
