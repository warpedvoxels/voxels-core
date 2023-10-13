@file:JvmName("ResourcePackSerializersModule")

package net.warpedvoxels.core.rp.serialization

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.warpedvoxels.core.rp.serialization.model.BlockStateModelMultipartCase
import net.warpedvoxels.core.rp.serialization.model.ItemModelPredicate

public val ResourcePackSerializersModule: SerializersModule = SerializersModule {
    polymorphic(BlockStateModelMultipartCase::class) {
        subclass(BlockStateModelMultipartCase.Default::class)
        subclass(BlockStateModelMultipartCase.And::class)
        subclass(BlockStateModelMultipartCase.Or::class)
    }
    polymorphic(ItemModelPredicate::class) {
        subclass(ItemModelPredicate.Angle::class)
        subclass(ItemModelPredicate.Blocking::class)
        subclass(ItemModelPredicate.Broken::class)
        subclass(ItemModelPredicate.Cast::class)
        subclass(ItemModelPredicate.Cooldown::class)
        subclass(ItemModelPredicate.Damage::class)
        subclass(ItemModelPredicate.Damaged::class)
        subclass(ItemModelPredicate.LeftHanded::class)
        subclass(ItemModelPredicate.Pull::class)
        subclass(ItemModelPredicate.Pulling::class)
        subclass(ItemModelPredicate.Charged::class)
        subclass(ItemModelPredicate.Firework::class)
        subclass(ItemModelPredicate.Throwing::class)
        subclass(ItemModelPredicate.Time::class)
        subclass(ItemModelPredicate.CustomModelData::class)
        subclass(ItemModelPredicate.Level::class)
        subclass(ItemModelPredicate.Filled::class)
        subclass(ItemModelPredicate.Tooting::class)
        subclass(ItemModelPredicate.TrimType::class)
        subclass(ItemModelPredicate.Brushing::class)
    }
}
