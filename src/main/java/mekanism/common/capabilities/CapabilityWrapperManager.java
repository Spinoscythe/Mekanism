package mekanism.common.capabilities;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.util.EnumFacing;

public class CapabilityWrapperManager<IMPL, WRAPPER> {

    private Map<EnumFacing, WRAPPER> wrappers = new EnumMap<>(EnumFacing.class);
    private Class<IMPL> typeClass;
    private Class<WRAPPER> wrapperClass;

    public CapabilityWrapperManager(Class<IMPL> type, Class<WRAPPER> wrapper) {
        typeClass = type;
        wrapperClass = wrapper;
    }

    public WRAPPER getWrapper(IMPL impl, EnumFacing facing) {
        try {
            if (wrappers.get(facing) == null) {
                WRAPPER wrapper = wrapperClass.getConstructor(typeClass, EnumFacing.class).newInstance(impl, facing);
                wrappers.put(facing, wrapper);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        return wrappers.get(facing);
    }
}