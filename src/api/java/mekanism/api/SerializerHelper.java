package mekanism.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.inputs.chemical.ChemicalIngredientDeserializer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SerializerHelper {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static FloatingLong getFloatingLong(@Nonnull JsonObject json, @Nonnull String key) {
        if (!json.has(key)) {
            throw new JsonSyntaxException("Missing '" + key + "', expected to find an object");
        }
        JsonElement jsonElement = json.get(key);
        if (!jsonElement.isJsonPrimitive()) {
            throw new JsonSyntaxException("Expected '" + key + "' to be a json primitive representing a FloatingLong");
        }
        try {
            return FloatingLong.parseFloatingLong(jsonElement.getAsNumber().toString(), true);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException("Expected '" + key + "' to be a valid FloatingLong (positive decimal number)");
        }
    }

    private static void validateKey(@Nonnull JsonObject json, @Nonnull String key) {
        if (!json.has(key)) {
            throw new JsonSyntaxException("Missing '" + key + "', expected to find an object");
        }
        if (!json.get(key).isJsonObject()) {
            throw new JsonSyntaxException("Expected '" + key + "' to be an object");
        }
    }

    public static ItemStack getItemStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, key));
    }

    public static GasStack getGasStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializeGas(JSONUtils.getJsonObject(json, key));
    }

    public static FluidStack getFluidStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializeFluid(JSONUtils.getJsonObject(json, key));
    }

    public static InfusionStack getInfusionStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializeInfuseType(JSONUtils.getJsonObject(json, key));
    }

    public static PigmentStack getPigmentStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializePigment(JSONUtils.getJsonObject(json, key));
    }

    public static SlurryStack getSlurryStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializeSlurry(JSONUtils.getJsonObject(json, key));
    }

    public static GasStack deserializeGas(@Nonnull JsonObject json) {
        return ChemicalIngredientDeserializer.GAS.deserializeStack(json);
    }

    public static FluidStack deserializeFluid(@Nonnull JsonObject json) {
        if (!json.has(JsonConstants.AMOUNT)) {
            throw new JsonSyntaxException("Expected to receive a amount that is greater than zero");
        }
        JsonElement count = json.get(JsonConstants.AMOUNT);
        if (!JSONUtils.isNumber(count)) {
            throw new JsonSyntaxException("Expected amount to be a number greater than zero.");
        }
        int amount = count.getAsJsonPrimitive().getAsInt();
        if (amount < 1) {
            throw new JsonSyntaxException("Expected amount to be greater than zero.");
        }
        ResourceLocation resourceLocation = new ResourceLocation(JSONUtils.getString(json, JsonConstants.FLUID));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(resourceLocation);
        if (fluid == null || fluid == Fluids.EMPTY) {
            throw new JsonSyntaxException("Invalid fluid type '" + resourceLocation + "'");
        }
        CompoundNBT nbt = null;
        if (json.has(JsonConstants.NBT)) {
            JsonElement jsonNBT = json.get(JsonConstants.NBT);
            try {
                if (jsonNBT.isJsonObject()) {
                    nbt = JsonToNBT.getTagFromJson(GSON.toJson(jsonNBT));
                } else {
                    nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(jsonNBT, JsonConstants.NBT));
                }
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid NBT entry for fluid '" + resourceLocation + "'");
            }
        }
        return new FluidStack(fluid, amount, nbt);
    }

    public static InfusionStack deserializeInfuseType(@Nonnull JsonObject json) {
        return ChemicalIngredientDeserializer.INFUSION.deserializeStack(json);
    }

    public static PigmentStack deserializePigment(@Nonnull JsonObject json) {
        return ChemicalIngredientDeserializer.PIGMENT.deserializeStack(json);
    }

    public static SlurryStack deserializeSlurry(@Nonnull JsonObject json) {
        return ChemicalIngredientDeserializer.SLURRY.deserializeStack(json);
    }

    public static JsonElement serializeItemStack(@Nonnull ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty(JsonConstants.ITEM, stack.getItem().getRegistryName().toString());
        if (stack.getCount() > 1) {
            json.addProperty(JsonConstants.COUNT, stack.getCount());
        }
        if (stack.hasTag()) {
            json.addProperty(JsonConstants.NBT, stack.getTag().toString());
        }
        return json;
    }

    public static JsonElement serializeGasStack(@Nonnull GasStack stack) {
        return ChemicalIngredientDeserializer.GAS.serializeStack(stack);
    }

    public static JsonElement serializeFluidStack(@Nonnull FluidStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty(JsonConstants.FLUID, stack.getFluid().getRegistryName().toString());
        json.addProperty(JsonConstants.AMOUNT, stack.getAmount());
        if (stack.hasTag()) {
            json.addProperty(JsonConstants.NBT, stack.getTag().toString());
        }
        return json;
    }

    public static JsonElement serializeInfusionStack(@Nonnull InfusionStack stack) {
        return ChemicalIngredientDeserializer.INFUSION.serializeStack(stack);
    }

    public static JsonElement serializePigmentStack(@Nonnull PigmentStack stack) {
        return ChemicalIngredientDeserializer.PIGMENT.serializeStack(stack);
    }

    public static JsonElement serializeSlurryStack(@Nonnull SlurryStack stack) {
        return ChemicalIngredientDeserializer.SLURRY.serializeStack(stack);
    }
}