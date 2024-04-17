package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class RLClassHandler<T> extends ClassHandler<NbtString, T>
{
    @Deprecated
    public RLClassHandler(Class<T> cls, SimpleRegistry<T> r)
    {
        super(
                cls,
                e -> e == null ? JsonNull.INSTANCE : new JsonPrimitive(Objects.requireNonNull(r.getId(e)).toString()),
                e -> e.isJsonNull() ? null : r.get(new Identifier(e.getAsString())),
                p ->
                {
                    int index = p.readInt();
                    if (index == -1) return null;
                    return r.get(index);
                },
                (p, t) -> p.writeInt(t == null ? -1 : r.getRawId(t)),
                s -> s.asString().isEmpty() ? null : r.get(new Identifier(s.asString())),
                t -> t == null ? NbtString.of("") : NbtString.of(Objects.requireNonNull(r.getId(t)).toString())
        );
    }

}