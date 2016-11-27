package com.samlinz.oom.ingredient;

/**
 * Created by samlinz on 26.11.2016.
 */
public class IngredientFactory {
    public static Ingredient getIngredient(String description) {
        Ingredient newIngredient = new Ingredient();
        newIngredient.setDescription(description);
        return newIngredient;
    }
}
