package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

public class TextHelper
{
    public static JsonElement serializeText(Text text)
    {
        return Util.getResult(Codecs.TEXT.encodeStart(JsonOps.INSTANCE, text), IllegalStateException::new);
    }

    public static Text deserializeText(JsonElement elem)
    {
        return Util.getResult(Codecs.TEXT.decode(JsonOps.INSTANCE, elem), IllegalStateException::new).getFirst();
    }

    public static NbtElement textToNbt(Text text)
    {
        String json = Text.Serializer.toJson(text);
        return NbtString.of(json);
    }

    public static Text textFromNbt(NbtElement nbt)
    {
        MutableText text = Text.Serializer.fromJson(nbt.asString());
        return text == null ? Text.empty() : text;
    }
}
