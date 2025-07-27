package org.rebix.stars

import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import org.rebix.stars.Stars.Companion.MOD_ID

class ModDimensions {
    companion object {
        val HUB_DIMENSION_KEY = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, "hub"))
        val HUB_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of(MOD_ID, "hub"))

        val NETHER_DIMENSION_KEY = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, "nether"))
        val NETHER_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of(MOD_ID, "nether"))

        fun register() {
            // Register the dimension and dimension type here if needed
            // This is usually done in a mod initializer or similar setup class
        }
    }
}