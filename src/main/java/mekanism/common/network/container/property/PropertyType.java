package mekanism.common.network.container.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.FloatingLong;
import mekanism.common.inventory.container.sync.ISyncableData;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableByte;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableFloat;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableFluidStack;
import mekanism.common.inventory.container.sync.SyncableFrequency;
import mekanism.common.inventory.container.sync.SyncableGasStack;
import mekanism.common.inventory.container.sync.SyncableInfusionStack;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableItemStack;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.container.sync.SyncablePigmentStack;
import mekanism.common.inventory.container.sync.SyncableShort;
import mekanism.common.inventory.container.sync.SyncableSlurryStack;
import mekanism.common.lib.frequency.Frequency;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public enum PropertyType {
    BOOLEAN(Boolean.TYPE, false, (getter, setter) -> SyncableBoolean.create(() -> (boolean) getter.get(), setter::accept)),
    BYTE(Byte.TYPE, (byte) 0, (getter, setter) -> SyncableByte.create(() -> (byte) getter.get(), setter::accept)),
    DOUBLE(Double.TYPE, 0D, (getter, setter) -> SyncableDouble.create(() -> (double) getter.get(), setter::accept)),
    FLOAT(Float.TYPE, 0F, (getter, setter) -> SyncableFloat.create(() -> (float) getter.get(), setter::accept)),
    INT(Integer.TYPE, 0, (getter, setter) -> SyncableInt.create(() -> (int) getter.get(), setter::accept)),
    LONG(Long.TYPE, 0L, (getter, setter) -> SyncableLong.create(() -> (long) getter.get(), setter::accept)),
    SHORT(Short.TYPE, (short) 0, (getter, setter) -> SyncableShort.create(() -> (short) getter.get(), setter::accept)),
    ITEM_STACK(ItemStack.class, ItemStack.EMPTY, (getter, setter) -> SyncableItemStack.create(() -> (ItemStack) getter.get(), setter::accept)),
    FLUID_STACK(FluidStack.class, FluidStack.EMPTY, (getter, setter) -> SyncableFluidStack.create(() -> (FluidStack) getter.get(), setter::accept)),
    GAS_STACK(GasStack.class, GasStack.EMPTY, (getter, setter) -> SyncableGasStack.create(() -> (GasStack) getter.get(), setter::accept)),
    INFUSION_STACK(InfusionStack.class, InfusionStack.EMPTY, (getter, setter) -> SyncableInfusionStack.create(() -> (InfusionStack) getter.get(), setter::accept)),
    PIGMENT_STACK(PigmentStack.class, PigmentStack.EMPTY, (getter, setter) -> SyncablePigmentStack.create(() -> (PigmentStack) getter.get(), setter::accept)),
    SLURRY_STACK(SlurryStack.class, SlurryStack.EMPTY, (getter, setter) -> SyncableSlurryStack.create(() -> (SlurryStack) getter.get(), setter::accept)),
    FREQUENCY(Frequency.class, null, (getter, setter) -> SyncableFrequency.create(() -> (Frequency) getter.get(), setter::accept)),
    LIST(ArrayList.class, Collections.emptyList(), (getter, setter) -> null /* not handled */),
    FLOATING_LONG(FloatingLong.class, FloatingLong.ZERO, (getter, setter) -> SyncableFloatingLong.create(() -> (FloatingLong) getter.get(), setter::accept));

    private final Class<?> type;
    private final Object defaultValue;
    private final BiFunction<Supplier<Object>, Consumer<Object>, ISyncableData> creatorFunction;

    private static final PropertyType[] VALUES = values();

    PropertyType(Class<?> type, Object defaultValue, BiFunction<Supplier<Object>, Consumer<Object>, ISyncableData> creatorFunction) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.creatorFunction = creatorFunction;
    }

    public <T> T getDefault() {
        return (T) defaultValue;
    }

    public static PropertyType getFromType(Class<?> type) {
        for (PropertyType propertyType : VALUES) {
            if (type == propertyType.type) {
                return propertyType;
            }
        }

        return null;
    }

    public ISyncableData create(Supplier<Object> supplier, Consumer<Object> consumer) {
        return creatorFunction.apply(supplier, consumer);
    }
}