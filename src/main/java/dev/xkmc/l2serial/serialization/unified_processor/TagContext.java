package dev.xkmc.l2serial.serialization.unified_processor;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class TagContext extends TreeContext<NbtElement, NbtCompound, NbtList>
{
    private static final NbtCompound NULL = new NbtCompound();

    static
    {
        NULL.putBoolean("_null", true);
    }

    private final Predicate<SerialClass.SerialField> pred;

    public TagContext(Predicate<SerialClass.SerialField> pred)
    {
        super(Optional.of(Pair.of(NULL, Optional.empty())));
        this.pred = pred;
    }

    @Override
    public Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(@Nullable NbtElement e, TypeInfo def) throws Exception
    {
        if (e == null || e instanceof NbtCompound tag && tag.contains("_null"))
            return Optional.of(Either.left(Optional.empty()));
        if (e instanceof NbtCompound obj)
            if (obj.contains("_class"))
            {
                NbtElement tcls = obj.get("_class");
                if (tcls != null)
                {
                    String scls = tcls.asString();
                    if (!scls.isEmpty())
                        return Optional.of(Either.right(TypeInfo.of(Class.forName(scls))));
                }
            }
        return Optional.empty();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object deserializeEfficientMap(NbtElement tag, TypeInfo key, TypeInfo val, Object def) throws Exception
    {
        NbtCompound ctag = (NbtCompound) tag;
        Map map = (Map) def;
        map.clear();
        for (String str : ctag.getKeys())
        {
            Object mkey = key.getAsClass() == String.class ? str :
                    key.getAsClass().isEnum() ? Enum.valueOf((Class) key.getAsClass(), str) :
                            Handlers.NBT_MAP.get(key.getAsClass()).fromTag(NbtString.of(str));
            NbtElement t = ctag.get(str);
            map.put(mkey, UnifiedCodec.deserializeValue(this, t == null ? NULL : t, val, null));
        }
        return map;
    }

    @Override
    public boolean hasSpecialHandling(Class<?> cls)
    {
        return Handlers.NBT_MAP.containsKey(cls);
    }

    @Override
    public Object deserializeSpecial(Class<?> cls, NbtElement e)
    {
        return Handlers.NBT_MAP.get(cls).fromTag(e);
    }

    @Override
    public NbtElement serializeSpecial(Class<?> cls, Object obj)
    {
        return Handlers.NBT_MAP.get(cls).toTag(obj);
    }

    @Override
    public boolean shouldRead(NbtCompound obj, FieldCache field) throws Exception
    {
        return pred.test(field.getSerialAnnotation()) && (obj.contains(field.getName()));
    }

    @Override
    public NbtElement retrieve(NbtCompound obj, String field)
    {
        NbtElement t = obj.get(field);
        return t == null ? NULL : t;
    }

    @Override
    public NbtList castAsList(NbtElement e)
    {
        return (NbtList) e;
    }

    @Override
    public int getSize(NbtList arr)
    {
        return arr.size();
    }

    @Override
    public NbtElement getElement(NbtList arr, int i)
    {
        return arr.get(i);
    }

    @Override
    public boolean isListFormat(NbtElement e)
    {
        return e instanceof NbtList;
    }

    @Override
    public NbtCompound castAsMap(NbtElement e)
    {
        return (NbtCompound) e;
    }

    @Override
    public String getAsString(NbtElement e)
    {
        if (e == NULL)
        {
            return "";
        }
        return e.asString();
    }

    @Override
    public void addField(NbtCompound obj, String str, @Nullable NbtElement e)
    {
        if (e != null)
            obj.put(str, e);
    }

    @Override
    public NbtList createList(int size)
    {
        return new NbtList();
    }

    @Override
    public NbtCompound createMap()
    {
        return new NbtCompound();
    }

    @Override
    public void addListItem(NbtList arr, NbtElement e)
    {
        arr.add(e);
    }

    @Override
    public boolean canBeString(NbtElement e)
    {
        return e instanceof NbtString;
    }

    @Override
    public NbtElement fromString(String str)
    {
        return NbtString.of(str);
    }

    @Override
    public boolean shouldWrite(SerialClass.SerialField sf)
    {
        return pred.test(sf);
    }
}