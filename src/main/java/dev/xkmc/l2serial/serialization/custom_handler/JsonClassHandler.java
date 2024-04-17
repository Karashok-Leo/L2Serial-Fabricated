package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;

public interface JsonClassHandler<T>
{
    T fromJson(JsonElement e);

    JsonElement toJson(Object obj);
}