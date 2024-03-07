package net.aspw.client.utils

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList

object NodeUtils {

    fun toNodes(vararg nodes: AbstractInsnNode): InsnList {
        val insnList = InsnList()
        for (node in nodes)
            insnList.add(node)
        return insnList
    }
}