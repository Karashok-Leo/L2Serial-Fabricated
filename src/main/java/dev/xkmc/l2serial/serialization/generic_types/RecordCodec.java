package dev.xkmc.l2serial.serialization.generic_types;

import dev.xkmc.l2serial.serialization.type_cache.RecordCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedCodec;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedContext;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class RecordCodec extends GenericCodec
{
    @Override
    public boolean predicate(TypeInfo cls, @Nullable Object obj)
    {
        return cls.getAsClass().isRecord() || cls.getAsClass() == Record.class;
    }

    @Override
    public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
    Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception
    {
        O o = ctx.castAsMap(e);
        E str = ctx.retrieve(o, "_class");
        Class<?> ccls = cls.getAsClass();
        try
        {
            String str0 = ctx.getAsString(str);
            if (!str0.isEmpty()) ccls = Class.forName(str0);
        } catch (Exception ignored)
        {
        }
        RecordCache cache = RecordCache.get(ccls);
        Field[] fields = cache.getFields();
        Object[] objs = new Object[fields.length];
        for (int i = 0; i < fields.length; i++)
        {
            Field f = fields[i];
            objs[i] = UnifiedCodec.deserializeValue(ctx, ctx.retrieve(o, f.getName()), TypeInfo.of(f), null);
        }
        return cache.create(objs);
    }

    @Override
    public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
    E serializeValue(C ctx, TypeInfo cls, Object obj) throws Exception
    {
        O o = ctx.createMap();
        ctx.addOptionalClass(o, obj.getClass(), cls.getAsClass());
        RecordCache cache = RecordCache.get(obj.getClass());
        for (Field f : cache.getFields())
            ctx.addField(o, f.getName(), UnifiedCodec.serializeValue(ctx, TypeInfo.of(f), f.get(obj)));
        return o;
    }
}