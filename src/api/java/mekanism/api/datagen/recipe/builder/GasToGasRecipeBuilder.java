package mekanism.api.datagen.recipe.builder;

import com.google.gson.JsonObject;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@NothingNullByDefault
public class GasToGasRecipeBuilder extends MekanismRecipeBuilder<GasToGasRecipeBuilder> {

    private final GasStackIngredient input;
    private final GasStack output;

    protected GasToGasRecipeBuilder(GasStackIngredient input, GasStack output, ResourceLocation serializerName) {
        super(serializerName);
        this.input = input;
        this.output = output;
    }

    /**
     * Creates an Activating recipe builder.
     *
     * @param input  Input.
     * @param output Output.
     */
    public static GasToGasRecipeBuilder activating(GasStackIngredient input, GasStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This solar neutron activator recipe requires a non empty gas output.");
        }
        return new GasToGasRecipeBuilder(input, output, mekSerializer("activating"));
    }

    /**
     * Creates a Centrifuging recipe builder.
     *
     * @param input  Input.
     * @param output Output.
     */
    public static GasToGasRecipeBuilder centrifuging(GasStackIngredient input, GasStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This Isotopic Centrifuge recipe requires a non empty gas output.");
        }
        return new GasToGasRecipeBuilder(input, output, mekSerializer("centrifuging"));
    }

    @Override
    protected MekanismRecipeBuilder<GasToGasRecipeBuilder>.RecipeResult getResult(ResourceLocation id, Provider registries) {
        return new GasToGasRecipeResult(id, registries);
    }

    public class GasToGasRecipeResult extends RecipeResult {

        protected GasToGasRecipeResult(ResourceLocation id, Provider registries) {
            super(id, registries);
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            json.add(JsonConstants.INPUT, input.serialize());
            json.add(JsonConstants.OUTPUT, SerializerHelper.serializeGasStack(output));
        }
    }
}