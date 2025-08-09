package org.rebix.stars.item

class GemstoneManager {
    private val gemstones: MutableList<SGemstoneSlot> = mutableListOf()

    fun set(index: Int, slot: SGemstoneSlot) {
        gemstones[index] = slot
    }

    fun clear() {
        gemstones.clear()
    }

    fun get(index: Int): SGemstoneSlot? {
        return gemstones.getOrNull(index)
    }

    fun getAll(): List<SGemstoneSlot> {
        return gemstones.toList()
    }

    fun add(slot: SGemstoneSlot) {
        gemstones.add(slot)
    }

    fun add(type: SGemstoneSlotType, unlocked: Boolean, gemstone: SGemstoneType? = null) {
        val newSlot = SGemstoneSlot(type)
        newSlot.unlocked = unlocked
        newSlot.gemstone = gemstone
        gemstones.add(newSlot)
    }

    fun remove(index: Int) {
        if (index in gemstones.indices) {
            gemstones.removeAt(index)
        }
    }

    fun size(): Int {
        return gemstones.size
    }

    override fun toString(): String {
        return gemstones.toString()
    }

    fun hasAny(): Boolean {
        return gemstones.isNotEmpty()
    }

    fun forEach(action: (SGemstoneSlot) -> Unit) {
        gemstones.forEach(action)
    }
}