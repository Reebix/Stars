package org.rebix.stars

class SStatManager {
    private val stats: MutableMap<SStatType, SStat> = mutableMapOf()

    fun getStat(type: SStatType): SStat {
        return stats.getOrPut(type) { SStat(type, 0.0) }
    }

    constructor(manager: SStatManager) {
        for ((type, stat) in manager.stats) {
            stats[type] = SStat(type, stat.value)
        }
    }

    constructor()

    fun set(type: SStatType, value: Double) {
        getStat(type).value = value
    }

    fun set(type: SStatType, value: Int) {
        getStat(type).value = value.toDouble()
    }

    fun getStats(): MutableList<SStat> {
        return stats.values.toMutableList()
    }

    fun add(type: SStatType, value: Double) {
        val currentStat = getStat(type)
        currentStat.value += value
    }

    fun add(type: SStatType, value: Int) {
        val currentStat = getStat(type)
        currentStat.value += value.toDouble()
    }

    override fun toString(): String {
        return stats.values.toString()
    }

    fun forEach(action: (SStat) -> Unit) {
        stats.values.forEach(action)
    }

    fun add(stat: SStat) {
        val currentStat = getStat(stat.type)
        currentStat.value += stat.value
    }

    fun sort() {
        val sortedStats = stats.values.sortedBy { it.type.ordinal }
        stats.clear()
        for (stat in sortedStats) {
            stats[stat.type] = stat
        }
    }
}