package org.rebix.stars.stats

class SStat {
    var type: SStatType = SStatType.DEFENSE
    var value: Double = 0.0

    constructor(type: SStatType, value: Double) {
        this.type = type
        this.value = value
    }

    constructor(type: SStatType, value: Int) : this(
        type,
        value.toDouble()
    )

    val formattedValue: String
        get() = if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            val formatted = String.format("%.1f", value).replace(",", ".")
            if (formatted.endsWith(".0")) {
                formatted.dropLast(2)
            } else {
                formatted
            }
        }

    override fun toString(): String {
        return "${type}:$value"
    }

    companion object
}