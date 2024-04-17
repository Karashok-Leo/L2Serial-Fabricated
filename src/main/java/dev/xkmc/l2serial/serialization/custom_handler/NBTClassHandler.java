package dev.xkmc.l2serial.serialization.custom_handler;

import net.minecraft.nbt.NbtElement;

public interface NBTClassHandler<R extends NbtElement, T>
{
    T fromTag(NbtElement valueOf);

    R toTag(Object obj);
}