package dev.xkmc.l2serial.serialization.custom_handler;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

public class StringRLClassHandler<T> extends StringClassHandler<T>
{
    @Deprecated
    public StringRLClassHandler(Class<T> cls, Supplier<Registry<T>> reg)
    {
        super(
                cls,
                s -> reg.get().get(new Identifier(s)),
                t -> Objects.requireNonNull(reg.get().getId(t)).toString()
        );
    }
}