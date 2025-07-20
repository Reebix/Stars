package org.rebix.stars

enum class SItemType(val categories: Set<SItemCategory>) {
    NONE(setOf()),
    ITEM(setOf()),
    HELMET(setOf(SItemCategory.ARMOR)),
    SWORD(setOf(SItemCategory.WEAPONS)),
    BOW(setOf(SItemCategory.WEAPONS)), ;

    override fun toString(): String {
        if (this == NONE) {
            return ""
        }
        return super.toString()
    }
}