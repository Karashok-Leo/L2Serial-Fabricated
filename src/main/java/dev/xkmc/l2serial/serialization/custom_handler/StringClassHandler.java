package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NbtString;

import java.util.function.Function;

public class StringClassHandler<T> extends ClassHandler<NbtString, T>
{
    public StringClassHandler(Class<T> cls, Function<String, T> fj, Function<T, String> tp)
    {
        super(
                cls,
                e -> tp == null ? JsonNull.INSTANCE : new JsonPrimitive(tp.apply(e)),
                e ->
                {
                    if (e.isJsonNull())
                        return null;
                    String str = e.getAsString();
                    if (str.isEmpty())
                        return null;
                    return fj.apply(str);
                },
                p ->
                {
                    String str = p.readString();
                    if (str.isEmpty())
                        return null;
                    return fj.apply(str);
                },
                (p, t) -> p.writeString(t == null ? "" : tp.apply(t)),
                t -> fj.apply(t.asString()),
                e -> NbtString.of(tp.apply(e))
        );
    }
}