package org.rebix.stars

import net.minecraft.util.math.Vec3d

enum class SEntityType(
    val parts: Set<Pair<Vec3d, String>> = setOf(),
    val defaultHitbox: Pair<Float, Float> = Pair(1f, 1f)
) {
    DUMMY(
        setOf(
            Pair(Vec3d(0.0, 0.4, 0.0), "animated_java:blueprint/blueprint/base"),
            Pair(Vec3d(0.0, 1.4, 0.0), "animated_java:blueprint/blueprint/top")
        ),
        defaultHitbox = Pair(0.8f, 1.9f),
    ),
}