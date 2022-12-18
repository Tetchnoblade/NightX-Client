package net.aspw.nightx.features.special.script.remapper

/*
 * @author CCBlueX
 */
object Remapper {
    private val fields: HashMap<String, HashMap<String, String>> = hashMapOf()
    private val methods: HashMap<String, HashMap<String, String>> = hashMapOf()

    /**
     * Remap field
     */
    fun remapField(clazz: Class<*>, name: String): String {
        if (!fields.containsKey(clazz.name))
            return name

        return fields[clazz.name]!!.getOrDefault(name, name)
    }

    /**
     * Remap method
     */
    fun remapMethod(clazz: Class<*>, name: String, desc: String): String {
        if (!methods.containsKey(clazz.name))
            return name

        return methods[clazz.name]!!.getOrDefault(name + desc, name)
    }
}