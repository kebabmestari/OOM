package com.samlinz.oom.ingredient;

/**
 * Class represents a single ingredient
 * Created by samlinz on 25.11.2016.
 */
public class Ingredient {

    /**
     * Constructor
     * Disallow explicit instatiation from outside the package
     * @.pre true
     * @.post true
     */
    Ingredient() {
    }

    // ingredient as text
    private String description;

    /**
     * @.pre true
     * @.post true
     * @return ingredient string
     */
    public String getDescription() {
        return description;
    }

    /**
     * @.pre description != null
     * @.post getDescription() != null
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
