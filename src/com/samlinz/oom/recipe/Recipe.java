package com.samlinz.oom.recipe;

import com.samlinz.oom.ingredient.Ingredient;
import com.samlinz.oom.ingredient.IngredientFactory;
import com.samlinz.oom.stage.Stage;
import com.samlinz.oom.stage.StageFactory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class represents a whole Recipe which consist of a series of
 * manufacturing stages and the list of ingredients
 *
 * Class is instatiated through RecipeFactory and which returns the object,
 * and then the ingredients and stages can be added by adding either objects made with
 * their own factories or then through 'streaming' shortcut by piping addIngredient()
 * and addStage() methods next to each other. These methods return reference to this
 * Recipe so they can be 'piped'
 *
 * To avoid plagiarism, call RecipeUtils class' static methods shuffleIngredients() and
 * shuffleStages(). These mix the recipe information so that the result of the ingredient is
 * not affected but they look very different
 *
 * @author Samuel Lindqvist
 */
public class Recipe {

    // recipe ID
    private int id;
    // recipe name
    private String name;

    // list of manufacturing stages
    List<Stage> stages;
    // list of ingredients
    List<Ingredient> ingredients;

    /**
     * Recipe constructor
     * Initializes the object
     * Package-private to force the customer to address RecipeFactory
     * @.pre true
     * @.post (getStages() != null && getStaged().size() == 0) &
     *          (getIngredients() != null && getIngredients().size() == 0)
     */
    Recipe() {
        stages = new ArrayList<>();
        ingredients = new ArrayList<>();
        LOG.fine("New recipe object created");
    }

    /**
     * @.pre id is set via setId
     * @.post RESULT != null
     * @return recipe id
     */
    public int getId() {
        return id;
    }

    /**
     * @.pre true
     * @.post getIId() == id
     * @param id identification number
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @.pre true
     * @.post RESULT != null
     * @return arraylist of stage objects
     */
    public List<Stage> getStages() {
        return stages;
    }

    /**
     * @.pre stages != null
     * @.post getStages() != null && getStages() == stages
     * @param stages
     */
    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    /**
     * Adds a single stage to the end of the list
     * @.pre stage != null
     * @.post getStages().size() == PRE.getResults().size() + 1
     * @param stage the stage object
     */
    public void addStage(Stage stage) {
        this.stages.add(stage);
        LOG.info("Adding stage " + stage.getId());
    }

    /**
     * Adds a single stage to the end of the list
     * ID of the stage are used for linking stage dependecies
     * @return THIS for streaming
     * @.pre FORALL(s : getStages(); s.getId() != id) &
     *          description != null
     * @.post
     * @param id stage id
     * @param description stage full description
     * @param dependencies list of stages which this stage depends on
     */
    public Recipe addStage(int id, String description, int... dependencies) {
        Stage s = StageFactory.getStage(id, description);
        if(dependencies.length > 0) {
            Arrays.stream(dependencies).forEach((d) -> {
                s.addParent(StageFactory.fetchStage(stages, d));
            });
            LOG.fine("Added dependendencies " + s.getParents().toString() + " to " + id);
        }
        addStage(s);
        return this;
    }

    /**
     * Finish building a recipe
     * Fixes the stage tree, aka fills the missing links between stages
     * @return THIS
     */
    public Recipe finish() {
        StageFactory.fixChildren(stages);
        LOG.info("Finishing new recipe " + getName());
        return this;
    }

    /**
     * @.pre true
     * @.post RESULT != null
     * @return ArrayList of Recipe Ingredient-objects
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * @.pre ingredients != null
     * @.post getIngredients() != ingredients
     * @param ingredients
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Constructs and appends a new Ingredients to the recipe's list
     * @.pre description != null
     * @.post getIngredients().size() == PRE.getIngredients().size() + 1
     * @param description ingredient description
     * @return THIS for streaming
     */
    public Recipe addIngredient(String description) {
        addIngredient(IngredientFactory.getIngredient(description));
        return this;
    }

    /**
     * Creates and adds multiple new ingredients
     * @.pre descriptions.length > 0
     * @.post getIngredients().size() > 0
     * @param descriptions list of string descriptions
     * @return THIS for streaming
     */
    public Recipe addIngredients(String... descriptions) {
        for(String s : descriptions) {
            addIngredient(s);
        }
        return this;
    }

    /**
     * Add an existing Ingredient object to the recipe
     * @.pre ingredient != null
     * @.post getIngredients().size() > 0
     * @param ingredient
     */
    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        LOG.info("Adding ingredient " + ingredient.getDescription());
    }

    /**
     * @.pre name has been set with setName
     * @.post RESULT != null
     * @return name of the recipe
     */
    public String getName() {
        return name;
    }

    /**
     * @.pre name != null
     * @.post getName() != null
     * @param name name of the recipe
     * @return THIS reference
     */
    public Recipe setName(String name) {
        this.name = name;
        return this;
    }

    private Logger LOG = Logger.getLogger(Recipe.class.getName());
}
