package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.xkmc.l2serial.serialization.generic_types.*;
import dev.xkmc.l2serial.serialization.nulldefer.NullDefer;
import dev.xkmc.l2serial.serialization.nulldefer.PrimitiveNullDefer;
import dev.xkmc.l2serial.serialization.nulldefer.SimpleNullDefer;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.function.Supplier;

public class Handlers
{
    public static final Map<Class<?>, JsonClassHandler<?>> JSON_MAP = new HashMap<>();
    public static final Map<Class<?>, NBTClassHandler<?, ?>> NBT_MAP = new HashMap<>();
    public static final Map<Class<?>, PacketClassHandler<?>> PACKET_MAP = new HashMap<>();

    public static final List<GenericCodec> LIST = new ArrayList<>();
    public static final Map<Class<?>, NullDefer<?>> MAP = new HashMap<>();

    // register handlers
    static
    {
        // Primitives
        new ClassHandler<>(
                long.class,
                JsonPrimitive::new,
                JsonElement::getAsLong,
                PacketByteBuf::readLong,
                PacketByteBuf::writeLong,
                NbtLong::longValue,
                NbtLong::of,
                Long.class
        );
        new ClassHandler<>(
                int.class,
                JsonPrimitive::new,
                JsonElement::getAsInt,
                PacketByteBuf::readInt,
                PacketByteBuf::writeInt,
                NbtInt::intValue,
                NbtInt::of,
                Integer.class
        );
        new ClassHandler<NbtShort, Short>(
                short.class,
                JsonPrimitive::new,
                JsonElement::getAsShort,
                PacketByteBuf::readShort,
                PacketByteBuf::writeShort,
                NbtShort::shortValue,
                NbtShort::of,
                Short.class
        );
        new ClassHandler<NbtByte, Byte>(
                byte.class,
                JsonPrimitive::new,
                JsonElement::getAsByte,
                PacketByteBuf::readByte,
                PacketByteBuf::writeByte,
                NbtByte::byteValue,
                NbtByte::of,
                Byte.class);
        new ClassHandler<NbtByte, Boolean>(
                boolean.class,
                JsonPrimitive::new,
                JsonElement::getAsBoolean,
                PacketByteBuf::readBoolean,
                PacketByteBuf::writeBoolean,
                tag -> tag.byteValue() != 0,
                NbtByte::of,
                Boolean.class);
        new ClassHandler<NbtByte, Character>(
                char.class,
                JsonPrimitive::new,
                JsonElement::getAsCharacter,
                PacketByteBuf::readChar,
                PacketByteBuf::writeChar,
                t -> (char) t.byteValue(),
                c -> NbtByte.of((byte) (char) c),
                Character.class
        );
        new ClassHandler<>(
                double.class,
                JsonPrimitive::new,
                JsonElement::getAsDouble,
                PacketByteBuf::readDouble,
                PacketByteBuf::writeDouble,
                NbtDouble::doubleValue,
                NbtDouble::of,
                Double.class
        );
        new ClassHandler<>(
                float.class,
                JsonPrimitive::new,
                JsonElement::getAsFloat,
                PacketByteBuf::readFloat,
                PacketByteBuf::writeFloat,
                NbtFloat::floatValue,
                NbtFloat::of,
                Float.class
        );

        new ClassHandler<>(
                String.class,
                JsonPrimitive::new,
                JsonElement::getAsString,
                PacketByteBuf::readString,
                PacketByteBuf::writeString,
                NbtElement::asString,
                NbtString::of
        );

        // Minecraft
        new ClassHandler<>(
                ItemStack.class,
                StackHelper::serializeItemStack,
                StackHelper::deserializeItemStack,
                PacketByteBuf::readItemStack,
                PacketByteBuf::writeItemStack,
                ItemStack::fromNbt,
                is -> is.writeNbt(new NbtCompound())
        );
//        new ClassHandler<>(
//                FluidStack.class,
//                StackHelper::serializeFluidStack,
//                StackHelper::deserializeFluidStack,
//                FluidStack::readFromPacket,
//                PacketByteBuf::writeFluidStack,
//                FluidStack::loadFluidStackFromNBT,
//                f -> f.writeToNBT(new NbtCompound())
//        );

        new StringClassHandler<>(
                Identifier.class,
                Identifier::new,
                Identifier::toString
        );
        new StringClassHandler<>(
                UUID.class,
                UUID::fromString,
                UUID::toString
        );

        // Partials

        // No NBT
        new ClassHandler<>(
                Ingredient.class,
                StackHelper::serializeIngredient,
                e -> e.isJsonArray() && e.getAsJsonArray().isEmpty() ? Ingredient.EMPTY : Ingredient.fromJson(e, false),
                Ingredient::fromPacket,
                (p, o) -> o.write(p),
                null,
                null
        );

        // No JSON
        new ClassHandler<NbtCompound, NbtCompound>(
                NbtCompound.class,
                null,
                null,
                PacketByteBuf::readNbt,
                PacketByteBuf::writeNbt,
                e -> e,
                e -> e
        );
        new ClassHandler<NbtList, NbtList>(
                NbtList.class,
                null,
                null,
                buf -> (NbtList) buf.readNbt().get("warp"),
                (buf, tag) ->
                {
                    NbtCompound comp = new NbtCompound();
                    comp.put("warp", tag);
                    buf.writeNbt(comp);
                },
                e -> e,
                e -> e
        );

        new ClassHandler<>(
                long[].class,
                null,
                null,
                PacketByteBuf::readLongArray,
                PacketByteBuf::writeLongArray,
                NbtLongArray::getLongArray,
                NbtLongArray::new
        );
        new ClassHandler<>(
                int[].class,
                null,
                null,
                PacketByteBuf::readIntArray,
                PacketByteBuf::writeIntArray,
                NbtIntArray::getIntArray,
                NbtIntArray::new
        );
        new ClassHandler<>(
                byte[].class,
                null,
                null,
                PacketByteBuf::readByteArray,
                PacketByteBuf::writeByteArray,
                NbtByteArray::getByteArray,
                NbtByteArray::new
        );

        new AutoPacketNBTHandler<>(
                BlockPos.class,
                tag -> new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")),
                obj ->
                {
                    NbtCompound tag = new NbtCompound();
                    tag.putInt("x", obj.getX());
                    tag.putInt("y", obj.getY());
                    tag.putInt("z", obj.getZ());
                    return tag;
                }
        );
        new AutoPacketNBTHandler<>(
                Vec3d.class,
                tag -> new Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z")),
                obj ->
                {
                    NbtCompound tag = new NbtCompound();
                    tag.putDouble("x", obj.getX());
                    tag.putDouble("y", obj.getY());
                    tag.putDouble("z", obj.getZ());
                    return tag;
                }
        );
        new AutoPacketNBTHandler<>(
                StatusEffectInstance.class,
                StatusEffectInstance::fromNbt,
                e -> e.writeNbt(new NbtCompound())
        );
    }

    // register generic codec
    static
    {
        new RecordCodec();
        new EnumCodec();
        new ArrayCodec();
        new AliasCodec();
        new ListCodec();
        new SetCodec();
        new MapCodec();
    }

    // register null defer
    static
    {
        new SimpleNullDefer<>(ItemStack.class, ItemStack.EMPTY);
        new SimpleNullDefer<>(Ingredient.class, Ingredient.EMPTY);
        new PrimitiveNullDefer<>(Integer.class, 0);
        new PrimitiveNullDefer<>(int.class, 0);
        new PrimitiveNullDefer<>(Long.class, 0L);
        new PrimitiveNullDefer<>(long.class, 0L);
        new PrimitiveNullDefer<>(Short.class, (short) 0);
        new PrimitiveNullDefer<>(short.class, (short) 0);
        new PrimitiveNullDefer<>(Byte.class, (byte) 0);
        new PrimitiveNullDefer<>(byte.class, (byte) 0);
        new PrimitiveNullDefer<>(Character.class, (char) 0);
        new PrimitiveNullDefer<>(char.class, (char) 0);
        new PrimitiveNullDefer<>(Double.class, 0d);
        new PrimitiveNullDefer<>(double.class, 0d);
        new PrimitiveNullDefer<>(Float.class, 0f);
        new PrimitiveNullDefer<>(float.class, 0f);
        new PrimitiveNullDefer<>(Boolean.class, false);
        new PrimitiveNullDefer<>(boolean.class, false);
    }

    private static final Set<Registry<?>> VANILLA_SYNC_REGISTRIES;

    static
    {
        VANILLA_SYNC_REGISTRIES = Set.of(
                Registries.SOUND_EVENT, // Required for SoundEvent packets
                Registries.STATUS_EFFECT, // Required for MobEffect packets
                Registries.BLOCK, // Required for chunk BlockState paletted containers syncing
                Registries.ENCHANTMENT, // Required for EnchantmentMenu syncing
                Registries.ENTITY_TYPE, // Required for Entity spawn packets
                Registries.ITEM, // Required for Item/ItemStack packets
                Registries.PARTICLE_TYPE, // Required for ParticleType packets
                Registries.BLOCK_ENTITY_TYPE, // Required for BlockEntity packets
                Registries.PAINTING_VARIANT, // Required for EntityDataSerializers
                Registries.SCREEN_HANDLER, // Required for ClientboundOpenScreenPacket
                Registries.COMMAND_ARGUMENT_TYPE, // Required for ClientboundCommandsPacket
                Registries.STAT_TYPE, // Required for ClientboundAwardStatsPacket
                Registries.VILLAGER_TYPE, // Required for EntityDataSerializers
                Registries.VILLAGER_PROFESSION, // Required for EntityDataSerializers
                Registries.CAT_VARIANT, // Required for EntityDataSerializers
                Registries.FROG_VARIANT // Required for EntityDataSerializers
        );
    }

    public static <T> void enable(Class<T> cls, Supplier<Registry<T>> reg)
    {
        if (VANILLA_SYNC_REGISTRIES.contains(reg.get()) &&
                reg.get() instanceof SimpleRegistry<T> mapped)
            new RLClassHandler<>(cls, mapped);
        else
            new StringRLClassHandler<>(cls, reg);
    }

    static
    {
        enable(Item.class, () -> Registries.ITEM);
        enable(Block.class, () -> Registries.BLOCK);
        enable(Potion.class, () -> Registries.POTION);
        enable(Enchantment.class, () -> Registries.ENCHANTMENT);
        enable(StatusEffect.class, () -> Registries.STATUS_EFFECT);
        enable(EntityAttribute.class, () -> Registries.ATTRIBUTE);
        enable(Wrappers.cast(EntityType.class), () -> Registries.ENTITY_TYPE);
    }

    public static void register()
    {
    }
}