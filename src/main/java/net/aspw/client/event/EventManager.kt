package net.aspw.client.event

//import java.util.*

class EventManager {

    private val registry = hashMapOf<Class<out Event>, MutableList<EventHook>>()

    /**
     * Register [listener]
     */
    fun registerListener(listener: Listenable) {
        for (method in listener.javaClass.declaredMethods) {
            if (method.isAnnotationPresent(EventTarget::class.java) && method.parameterTypes.size == 1) {
                if (!method.isAccessible)
                    method.isAccessible = true

                val eventClass = method.parameterTypes[0] as Class<out Event>
                val eventTarget = method.getAnnotation(EventTarget::class.java)

                val invokableEventTargets = registry.getOrElse(eventClass, { arrayListOf<EventHook>() })
                try {
                    invokableEventTargets.add(EventHook(listener, method, eventTarget.priority, eventTarget))
                } catch (e: Exception) {
                    e.printStackTrace()
                    // switch to normal handler
                    invokableEventTargets.add(EventHook(listener, method, eventTarget))
                }
                invokableEventTargets.sortBy { it.priority }
                registry.put(eventClass, invokableEventTargets)
            }
        }
    }

    /**
     * Unregister listener
     *
     * @param listenable for unregister
     */
    fun unregisterListener(listenable: Listenable) {
        for ((key, targets) in registry) {
            targets.removeIf { it.eventClass == listenable }

            registry.put(key, targets)
        }
    }

    /**
     * Call event to listeners
     *
     * @param event to call
     */
    fun callEvent(event: Event) {
        val targets = registry.get(event.javaClass) ?: return

        targets.filter { it.eventClass.handleEvents() || it.isIgnoreCondition }.forEach {
            try {
                it.method.invoke(it.eventClass, event)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
    }
}
