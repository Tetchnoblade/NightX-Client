package net.aspw.nightx.injection.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class NetworkManagerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("net.aspw.nightx.injection.forge.mixins.network.MixinNetworkManagerProxy")) {
            try {
                final ClassReader reader = new ClassReader(basicClass);
                final ClassNode classNode = new ClassNode();
                reader.accept(classNode, 0);

                classNode.methods.stream().filter(methodNode -> methodNode.name.equals("createNetworkManagerAndConnect")).forEach(methodNode -> {
                    for (int i = 0; i < methodNode.instructions.size(); ++i) {
                        final AbstractInsnNode abstractInsnNode = methodNode.instructions.get(i);
                        if (abstractInsnNode instanceof TypeInsnNode) {
                            TypeInsnNode tin = (TypeInsnNode) abstractInsnNode;
                            if (tin.desc.equals("net/aspw/nightx/injection/forge/mixins/network/MixinNetworkManagerProxy$1")) {
                                ((TypeInsnNode) abstractInsnNode).desc = "net/minecraft/network/NetworkManager$5";
                            }
                        } else if (abstractInsnNode instanceof MethodInsnNode) {
                            MethodInsnNode min = (MethodInsnNode) abstractInsnNode;
                            if (min.owner.equals("net/aspw/nightx/injection/forge/mixins/network/MixinNetworkManagerProxy$1") && min.name.equals("<init>")) {
                                min.owner = "net/minecraft/network/NetworkManager$5";
                            }
                        }
                    }
                });

                final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                classNode.accept(writer);
                return writer.toByteArray();
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return basicClass;
    }
}