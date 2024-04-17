package dev.xkmc.l2serial.serialization.custom_handler;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AutoPacketNBTHandler<T> extends ClassHandler<NbtCompound, T>
{
    public AutoPacketNBTHandler(
            @NotNull Class<T> cls,
            Function<NbtCompound, T> ft,
            Function<T, NbtCompound> tt,
            @NotNull Class<?>... others
    )
    {
        super(
                cls,
                null,
                null,
                p -> ft.apply(p.readNbt()),
                (p, o) -> p.writeNbt(tt.apply(o)),
                ft,
                tt,
                others
        );
    }
}