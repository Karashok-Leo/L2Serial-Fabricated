package dev.xkmc.l2serial.serialization.unified_processor;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class PacketContext extends SingletonContext<PacketByteBuf>
{
    private final Predicate<SerialClass.SerialField> pred;

    public PacketContext(PacketByteBuf instance, Predicate<SerialClass.SerialField> pred)
    {
        super(instance);
        this.pred = pred;
    }

    public PacketContext(PacketByteBuf instance)
    {
        this(instance, e -> true);
    }

    @Override
    public boolean hasSpecialHandling(Class<?> cls)
    {
        return Handlers.PACKET_MAP.containsKey(cls);
    }

    @Override
    public Object deserializeSpecial(Class<?> cls, PacketByteBuf self)
    {
        return Handlers.PACKET_MAP.get(cls).fromPacket(instance);
    }

    @Override
    public PacketByteBuf serializeSpecial(Class<?> cls, Object obj)
    {
        Handlers.PACKET_MAP.get(cls).toPacket(instance, obj);
        return instance;
    }

    @Override
    public Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(@Nullable PacketByteBuf obj, TypeInfo cls) throws Exception
    {
        byte header = instance.readByte();
        if (header == 0)
        {
            return Optional.of(Either.left(Optional.empty()));
        } else if (header == 2)
        {
            return Optional.of(Either.right(TypeInfo.of(Class.forName(instance.readString()))));
        } else return Optional.empty();
    }

    @Override
    public Optional<Pair<PacketByteBuf, Optional<ClassCache>>> writeRealClass(TypeInfo cls, @Nullable Object obj) throws Exception
    {
        if (obj == null)
        {
            instance.writeByte(0);
            return Optional.of(Pair.of(instance, Optional.empty()));
        }
        Optional<Wrappers.ExcSup<PacketByteBuf>> special = UnifiedCodec.serializeSpecial(this, cls, obj);
        if (special.isPresent())
        {
            instance.writeByte(1);
            return Optional.of(Pair.of(special.get().get(), Optional.empty()));
        }
        if (obj.getClass() != cls.getAsClass())
        {
            ClassCache cache = ClassCache.get(obj.getClass());
            if (cache.getSerialAnnotation() != null)
            {
                instance.writeByte(2);
                instance.writeString(obj.getClass().getName());
                return Optional.of(Pair.of(instance, Optional.of(cache)));
            }
        }
        instance.writeByte(1);
        return Optional.empty();
    }

    @Override
    public boolean shouldRead(PacketByteBuf obj, FieldCache field) throws Exception
    {
        return pred.test(field.getSerialAnnotation());
    }

    @Override
    public boolean shouldWrite(SerialClass.SerialField sf)
    {
        return pred.test(sf);
    }

    @Override
    public int getSize(PacketByteBuf self)
    {
        return instance.readInt();
    }

    @Override
    public String getAsString(PacketByteBuf self)
    {
        return instance.readString();
    }

    @Override
    public PacketByteBuf createList(int size)
    {
        instance.writeInt(size);
        return instance;
    }

    @Override
    public PacketByteBuf fromString(String str)
    {
        instance.writeString(str);
        return instance;
    }
}