package dev.xkmc.l2serial.serialization.custom_handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class CodecHandler<T> extends ClassHandler<NbtElement, T>
{
    public CodecHandler(Class<T> cls, Codec<T> codec, Function<PacketByteBuf, T> fp, BiConsumer<PacketByteBuf, T> tp)
    {
        super(cls, e -> codec.encodeStart(JsonOps.INSTANCE, e).result().get(),
                e -> codec.decode(JsonOps.INSTANCE, e).result().get().getFirst(), fp, tp,
                e -> codec.decode(NbtOps.INSTANCE, e).result().get().getFirst(),
                e -> codec.encodeStart(NbtOps.INSTANCE, e).result().get());
    }
}