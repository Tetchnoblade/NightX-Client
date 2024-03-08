package net.aspw.client.injection.transformers;

import net.aspw.client.utils.ClassUtils;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class ProtocolTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("net.aspw.client.injection.forge.mixins.network.MixinNetworkManager")) {
            try {
                final ClassNode classNode = ClassUtils.INSTANCE.toClassNode(basicClass);

                classNode.methods.stream().filter(methodNode -> methodNode.name.equals("createNetworkManagerAndConnect")).forEach(methodNode -> {
                    for (int i = 0; i < methodNode.instructions.size(); ++i) {
                        final AbstractInsnNode abstractInsnNode = methodNode.instructions.get(i);
                        if (abstractInsnNode instanceof TypeInsnNode) {
                            TypeInsnNode tin = (TypeInsnNode) abstractInsnNode;
                            if (tin.desc.equals("net/aspw/client/injection/forge/mixins/network/MixinNetworkManager$1")) {
                                ((TypeInsnNode) abstractInsnNode).desc = "net/minecraft/network/NetworkManager$5";
                            }
                        } else if (abstractInsnNode instanceof MethodInsnNode) {
                            MethodInsnNode min = (MethodInsnNode) abstractInsnNode;
                            if (min.owner.equals("net/aspw/client/injection/forge/mixins/network/MixinNetworkManager$1") && min.name.equals("<init>")) {
                                min.owner = "net/minecraft/network/NetworkManager$5";
                            }
                        }
                    }
                });

                return ClassUtils.INSTANCE.toBytes(classNode);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return basicClass;
    }
}