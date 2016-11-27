package com.samlinz.oom.recipe;

import com.samlinz.oom.ingredient.Ingredient;
import com.samlinz.oom.stage.Stage;

import java.util.List;

/**
 * Created by samlinz on 25.11.2016.
 */
public class RecipeFactory {

    private static int ids = 0;

    public static Recipe getRecipe(String name) {
        Recipe newRecipe = new Recipe();
        newRecipe.setId(ids++);
        newRecipe.setName(name);
        return newRecipe;
    }

    public static Recipe getRecipe(String name, List<Ingredient> ingredientList, List<Stage> stages) {
        Recipe newRecipe = getRecipe(name);
        newRecipe.setIngredients(ingredientList);
        newRecipe.setStages(stages);
        return newRecipe;
    }

/*    public Recipe getShuffledRecipe(Recipe original) {
        Recipe newRecipe = new Recipe();
    }*/

}
