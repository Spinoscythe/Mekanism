package mekanism.common.integration.computer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraftforge.common.util.Lazy;

import java.util.HashSet;
import java.util.Set;

public abstract class BoundMethodHolder {
    protected final Multimap<String, MethodData> methods = HashMultimap.create();
    /**
     * Method + arg count pairs to make sure methods are unique
     */
    private final Set<ObjectIntPair<String>> methodsKnown = new HashSet<>();

    protected Lazy<String[]> methodNames = Lazy.of(()->this.methods.keys().toArray(new String[0]));

    public <T> void register(String name, boolean threadSafe, String[] argumentNames, Class<?>[] argClasses, T subject, ComputerMethodFactory.ComputerFunctionCaller<T> handler) {
        if (!methodsKnown.add(new ObjectIntImmutablePair<>(name, argumentNames.length))) {
            throw new RuntimeException("Duplicate method name "+name+"_"+argumentNames.length);
        }
        //noinspection unchecked
        this.methods.put(name, new MethodData(name, threadSafe, argumentNames, argClasses, subject, (ComputerMethodFactory.ComputerFunctionCaller<Object>) handler));
    }

    public record MethodData(String name, boolean threadSafe, String[] argumentNames, Class<?>[] argClasses, Object subject, ComputerMethodFactory.ComputerFunctionCaller<Object> handler){}
}