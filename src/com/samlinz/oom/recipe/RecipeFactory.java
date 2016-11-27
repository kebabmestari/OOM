package com.samlinz.oom.recipe;

/**
 * Factory class for Recipe objects
 * Created by samlinz on 25.11.2016.
 */
public class RecipeFactory {

    private static int ids = 0;

    /**
     * Build and return a new Recipe object
     * which can then be filled with the wished information
     *
     * @param name name of the recipe
     * @return Recipe object
     * @.pre name != null
     * @.post RESULT != null
     */
    public static Recipe getRecipe(String name) {
        Recipe newRecipe = new Recipe();
        newRecipe.setId(ids++);
        newRecipe.setName(name);
        return newRecipe;
    }

}
