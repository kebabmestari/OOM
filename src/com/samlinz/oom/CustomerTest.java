package com.samlinz.oom;

import com.samlinz.oom.recipe.Recipe;
import com.samlinz.oom.recipe.RecipeFactory;
import com.samlinz.oom.recipe.RecipeUtils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A test class
 * Represents a customer who inputs a recipe and then
 * shuffles it to avoid plagiarism
 * Created by samlinz on 25.11.2016.
 */
public class CustomerTest {

    /**
     * Application entry point
     * @param args array of command line arguments
     */
    public static void main(String[] args) throws InterruptedException{

        // remove default logger handler
        Logger globalLogger = Logger.getLogger("");
        Handler[] handlers = globalLogger.getHandlers();
        for (Handler handler : handlers) {
            globalLogger.removeHandler(handler);
        }

        // add custom logger handler
        ConsoleHandler customHandler = new ConsoleHandler();
        customHandler.setLevel(Level.FINE);
        customHandler.setFormatter(new CustomLogFormatter());
        globalLogger.addHandler(customHandler);

        // create a test recipe
        Recipe chiliConCarne = RecipeFactory.getRecipe("Chili sin carne")
                .addIngredients(
                        "1 kpl, iso sipuli",
                        "1-2kpl valkosipulinkynsi",
                        "1 kpl paprika",
                        "2 dl tumma soijarouhe",
                        "1 tlk tomaattimurska",
                        "3 dl vesi",
                        "1 tlk kidneypapu",
                        "0.5 tl suola",
                        "tilkka öljyä",
                        "hyppysellinen chiliä",
                        "persiljaa koristeeksi"
                )
                .addStage(1, "hienonna valkosipuli")
                .addStage(2, "hienonna sipuli", 1)
                .addStage(3, "kullota sipulit öljyssä", 2)
                .addStage(4, "lisää soijarouhe, tomaattimurska ja vesi", 3)
                .addStage(5, "keitä 5min", 4)
                .addStage(6, "suikaloi paprika")
                .addStage(7, "huuhdo ja valuta pavut")
                .addStage(8, "lisää paprika, pavut ja chili", 5, 6, 7)
                .addStage(9, "hauduta 5min", 8)
                .addStage(9, "mausta suolalla", 9)
                .addStage(10, "ripottele päälle persiljaa", 9)
                .addStage(11, "keitä riisi tai peruna")
                .addStage(12, "tarjoile", 10, 11)
                .finish();

        System.out.println("\nThe original ingredients: ");
        Thread.sleep(5);
        RecipeUtils.outputRecipeIngredients(chiliConCarne, System.out);

        System.out.println("\nThe original cookings stages:");
        Thread.sleep(5);
        // output the initial stages in the order they were given
        RecipeUtils.outputRecipeStages(chiliConCarne, System.out);

        System.out.println("\nMixing the ingredients and stages to avoid plagiarism...");

        // shuffle ingredients
        RecipeUtils.shuffleIngredients(chiliConCarne);
        // shuffle stages while keeping them in consistent order
        RecipeUtils.shuffleStages(chiliConCarne);


        System.out.println("\nThe new ingredient list:");
        Thread.sleep(5);

        RecipeUtils.outputRecipeIngredients(chiliConCarne, System.out);

        System.out.println("\nThe new cookings stages:");
        Thread.sleep(5);
        // output the initial stages in the order they were given
        RecipeUtils.outputRecipeStages(chiliConCarne, System.out);
    }
}
