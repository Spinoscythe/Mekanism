import mods.mekanism.api.ingredient.ChemicalStackIngredient.SlurryStackIngredient;

//Removes the Washing Recipe for cleaning Dirty Uranium Slurry.

// <recipetype:mekanism:washing>.removeByName(name as string)

<recipetype:mekanism:washing>.removeByName("mekanism:processing/uranium/slurry/clean");
//Add back the Washing Recipe that was removed above, this time having it require 10 mB of water to clean 1 mB of Dirty Uranium Slurry instead of 5 mB:

// <recipetype:mekanism:washing>.addRecipe(name as string, fluidInput as CTFluidIngredient, slurryInput as SlurryStackIngredient, output as ICrTSlurryStack)

<recipetype:mekanism:washing>.addRecipe("cleaning_uranium_slurry", <tag:fluids:minecraft:water> * 10, SlurryStackIngredient.from(<slurry:mekanism:dirty_uranium>), <slurry:mekanism:clean_uranium>);
//An alternate implementation of the above recipe are shown commented below. This implementation makes use of implicit casting to allow easier calling:
// <recipetype:mekanism:washing>.addRecipe("cleaning_uranium_slurry", <tag:fluids:minecraft:water> * 10, <slurry:mekanism:dirty_uranium>, <slurry:mekanism:clean_uranium>);

