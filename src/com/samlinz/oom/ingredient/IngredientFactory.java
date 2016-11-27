package com.samlinz.oom.ingredient;

/**
 * A factory class for Ingredient objects
 * Created by samlinz on 26.11.2016.
 */
public class IngredientFactory {

    /**
     * Construct a new Ingredient object in a controlled manner and returns it
     * @.pre description != null
     * @.post RESULT = a new Ingredient object with the given parameters
     * @param description ingredient description
     * @return a newly instatiated Ingredient object
     */
    public static Ingredient getIngredient(String description) {
        Ingredient newIngredient = new Ingredient();
        newIngredient.setDescription(description);
        return newIngredient;
    }
}
