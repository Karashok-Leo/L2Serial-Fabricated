package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Util;

public class StackHelper
{
    private static final Gson GSON = new Gson();

    /**
     * for mod recipes that use automatic serialization
     */
    public static JsonElement serializeItemStack(ItemStack stack)
    {
        return serializeFabricItemStack(stack);
    }

    /**
     * for vanilla recipes
     */
    public static JsonElement serializeFabricItemStack(ItemStack stack)
    {
        return Util.getResult(ItemStack.CODEC.encodeStart(JsonOps.INSTANCE,stack), IllegalStateException::new);
//        JsonObject ans = new JsonObject();
//        ans.addProperty("id", Registries.ITEM.getId(stack.getItem()).toString());
//        ans.addProperty("Count", stack.getCount());
//        if (stack.getNbt() != null)
//            ans.addProperty("tag", stack.getNbt().toString());
//        return ans;
    }

    public static ItemStack deserializeItemStack(JsonElement elem)
    {
        return Util.getResult(ItemStack.CODEC.decode(JsonOps.INSTANCE, elem), IllegalStateException::new).getFirst();
    }

//    public static JsonElement serializeFluidStack(FluidStack stack)
//    {
//        JsonObject json = new JsonObject();
//        json.addProperty("fluid", Registries.FLUID.getId(stack.getFluid()).toString());
//        json.addProperty("amount", stack.getAmount());
//        if (stack.hasTag())
//            json.addProperty("nbt", stack.getTag().toString());
//        return json;
//    }

//    public static FluidStack deserializeFluidStack(JsonElement e)
//    {
//        JsonObject json = e.getAsJsonObject();
//        Identifier id = new Identifier(JsonHelper.asString(json, "fluid"));
//        if (!Registries.FLUID.containsId(id))
//            throw new JsonSyntaxException("Unknown fluid '" + id + "'");
//        Fluid fluid = Registries.FLUID.get(id);
//        int amount = JsonHelper.asInt(json, "amount");
//        FluidStack stack = new FluidStack(fluid, amount);
//        if (!json.has("nbt")) return stack;
//        try
//        {
//            JsonElement element = json.get("nbt");
//            stack.setTag(StringNbtReader.parse(element.isJsonObject() ? GSON.toJson(element) : JsonHelper.asString(element, "nbt")));
//        } catch (CommandSyntaxException err)
//        {
//            err.printStackTrace();
//        }
//        return stack;
//    }

    public static JsonElement serializeIngredient(Ingredient ing)
    {
//        return Util.getResult(Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ing), IllegalStateException::new);
        return ing.toJson();
    }

    public static Ingredient deserializeIngredient(JsonElement elem)
    {
        return Ingredient.fromJson(elem);
    }
}