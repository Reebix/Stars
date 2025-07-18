package org.rebix.stars

enum class SItemType(val categories: Set<SItemCategory>) {
    NONE(setOf()),
    HELMET(setOf(SItemCategory.ARMOR)),
    SWORD(setOf(SItemCategory.WEAPONS));

    override fun toString(): String {
        if (this == NONE) {
            return ""
        }
        return super.toString()
    }
}