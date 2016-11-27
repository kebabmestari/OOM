package com.samlinz.oom;

import com.samlinz.oom.ingredient.Ingredient;
import com.samlinz.oom.ingredient.IngredientFactory;
import com.samlinz.oom.stage.Stage;
import com.samlinz.oom.stage.StageFactory;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by samlinz on 25.11.2016.
 */
public class Recipe {

    private int id;
    private String name;

    List<Stage> stages;

    List<Ingredient> ingredients;

    public Recipe() {
        stages = new ArrayList<>();
        ingredients = new ArrayList<>();
        LOG.fine("New recipe object created");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public void addStage(Stage stage) {
        this.stages.add(stage);
        LOG.info("Adding stage " + stage.getId());
    }

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

    public Recipe finish() {
        StageFactory.fixChildren(stages);
        LOG.info("Finishing new recipe " + getName());
        return this;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Recipe addIngredient(String description) {
        addIngredient(IngredientFactory.getIngredient(description));
        return this;
    }

    public Recipe addIngredients(String... descriptions) {
        for(String s : descriptions) {
            addIngredient(s);
        }
        return this;
    }

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        LOG.info("Adding ingredient " + ingredient.getDescription());
    }

    public String getName() {
        return name;
    }

    public Recipe setName(String name) {
        this.name = name;
        return this;
    }

    public void shuffleIngredients() {
        Collections.shuffle(ingredients);
        LOG.info("Shuffled ingredient list");
    }

    public void outputStages(PrintStream out) {
        stages.stream().forEach((s) -> {
            out.println(s.getDescription());
        });
    }

    private Logger LOG = Logger.getLogger(Recipe.class.getName());
}
