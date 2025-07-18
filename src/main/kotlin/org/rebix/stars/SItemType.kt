package org.rebix.stars

enum class SItemType {
    NONE,
    HELMET;

    override fun toString(): String {
        if (this == NONE) {
            return ""
        }
        return super.toString()
    }
}