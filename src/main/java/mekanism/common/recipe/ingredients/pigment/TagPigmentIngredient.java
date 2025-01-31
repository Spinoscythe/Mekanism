package mekanism.common.recipe.ingredients.pigment;

import com.mojang.serialization.MapCodec;
import mekanism.api.MekanismAPI;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.recipes.ingredients.chemical.IPigmentIngredient;
import mekanism.api.recipes.ingredients.chemical.TagChemicalIngredient;
import mekanism.common.registries.MekanismPigmentIngredientTypes;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

@NothingNullByDefault
public final class TagPigmentIngredient extends TagChemicalIngredient<Pigment, IPigmentIngredient> implements IPigmentIngredient {

    public static final MapCodec<TagPigmentIngredient> CODEC = codec(MekanismAPI.PIGMENT_REGISTRY_NAME, TagPigmentIngredient::new);

    TagPigmentIngredient(TagKey<Pigment> tag) {
        super(tag);
    }

    @Override
    public MapCodec<TagPigmentIngredient> codec() {
        return MekanismPigmentIngredientTypes.TAG.value();
    }

    @Override
    protected Registry<Pigment> registry() {
        return MekanismAPI.PIGMENT_REGISTRY;
    }
}
