package com.samlinz.oom.recipe;

import com.samlinz.oom.stage.Stage;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Package-private class which is used in shuffling algorithm
 * Represents a direct line of interrelated recipe stages
 * The line starts with a node stage which has multiple parents
 * or no parents if it's a stage which begins a 'line', aka root stage
 *
 * Class holds information about the line and methods to retrieve the preceding
 * stagelines
 *
 * Last stage aka node is NOT counter to be belonging to the 'line' because it has multiple
 * parents. It is used to link the linked lines
 *
 * @author Samuel Lindqvist
 */
class StageLine implements Serializable {

    // node stage which ends the line
    private Stage node;
    // references to the stages, in order
    private List<Stage> stageLine;
    // flag to indicate whether this line ends the tree of stages
    private boolean isLastLeaf = false;

    /**
     * Constructor, package-private
     * @.pre true
     * @.post true
     */
    StageLine() {
    }

    /**
     * @.pre true
     * @.post true
     * @return true if the last stage belonging
     */
    public boolean getIsLastLeaf() {
        return isLastLeaf;
    }

    /**
     * @.pre isLastLeaf != null
     * @.post getIsLastLeaf() != null
     * @param isLastLeaf
     */
    public void setIsLastLeaf(boolean isLastLeaf) {
        this.isLastLeaf = isLastLeaf;
    }

    /**
     * @.pre getStageLine() != null & !getStageLine.isEmpty()
     * @.post RESULT = getStageLine().get(0).isRoot()
     * @return true if this line is a root line, meaning it has no dependecies
     */
    public boolean isRoot() {
        return stageLine.get(0).isRoot();
    }

    /**
     * @.pre getStageLine().size() > 0
     * @.post RESULT = getStageLine().get(0)
     * @return the first stage in the line
     */
    public Stage getFirst() {
        return stageLine.get(0);
    }

    /**
     * Return the last stage in the line
     * @.pre getStageLine().size() > 0
     * @.post RESULT = getStageLine().get(0)
     * @return the first stage in the line
     */
    public Stage getNode() {
        return node;
    }

    /**
     * @.pre node stage is set
     * @.post RESULT = this.node
     */
    public void setNode(Stage node) {
        this.node = node;
    }

    /**
     * @.pre stageLine != null
     * @.post getStageLine() != null
     */
    public void setStageLine(List<Stage> stageLine) {
        this.stageLine = stageLine;
    }

    /**
     * @.pre stageLine is set with setStageLine
     * @.post RESULT = this.stageLine
     */
    public List<Stage> getStageLine() {
        return stageLine;
    }

    /**
     * Returns the stage lines which are the parents of the given node,
     * means that they all end in the node stage
     * @param stagelines list of all stage lines
     * @param node node stage
     * @.pre stageLines != null & node != null
     * @.post FOREACH(l : RESULT; l.getNode() == node)
     * @return the list of lines which precede the given node
     */
    public static List<StageLine> getPrecedingLines(List<StageLine> stagelines, Stage node) {
        List<StageLine> result = new ArrayList<>();
        for (StageLine s : stagelines) {
            if (s.getNode() == node && !s.getIsLastLeaf())
                result.add(s);
        }
        return result;
    }

    /**
     * Checks if a stage line beginning with the given stage exists in given list
     * @param stageLines list of stagelines to check
     * @param first stage object which begins the searched stageline
     * @.pre stageLines != null & first != null
     * @.post RESULT == getStageLine(stageLines, first) != null
     * @return true if such stageline exists
     */
    public static boolean stageLineExits(List<StageLine> stageLines, Stage first) {
        if (getStageLine(stageLines, first) != null) return true;
        return false;
    }

    /**
     * Returns the stage line, if it exists, which begins with the given stage
     * @param stageLines list of stage line
     * @param first the stage which begins the line
     * @.pre stageLines != null & first != null
     * @.post true
     * @return StageLine object if it exists, null if not
     */
    public static StageLine getStageLine(List<StageLine> stageLines, Stage first) {
        for (StageLine sl : stageLines) {
            if (sl.getFirst() == first)
                return sl;
        }
        return null;
    }
}

/**
 * Class to eclose various utility functions related to handling Recipes
 * Namely, shuffling and outputting the data
 * @author Samuel Lindqvist
 */
public class RecipeUtils {

    /**
     * Shuffles the ingredients in random order
     * @param recipe the recipe which ingredients are to be shuffled
     * @.pre recipe.getIngredients() != null
     * @.post true
     */
    public static void shuffleIngredients(Recipe recipe) {
        Collections.shuffle(recipe.getIngredients());
    }

    /**
     * Shuffles the stages of the given recipe
     * If a stage depends on multiple stages, meaning it's a 'node'
     * then it will be ensured that all the dependencies and their dependencies are
     * outputted to new list before them so the recipe makes sense
     *
     * Uses an algorithm which
     * 1. Looks for the root lines
     * 2. Creates the first stage lines from them
     * 3. Recursively creates the lines from root lines' ending nodes
     * until the end of the recipe, or 'leaf' is encountered
     * 4. Looks for each lines' node, fetches their dependency nodes
     * 5. Checks that those 'parents' have their dependencies outputted already
     * 6. Randomizes the not-related dependencies to the new list so that the different lines'
     * stages are interlaced randomly, but keep their correct order
     * 7. Repeats this until the final line is met and ensures all the dependencies are already
     * in the new list
     *
     *
     * @.pre recipe != null && recipe.getStages != null && recipe.getStages().size() > 0 &
     *          ALL THE DEPENDENCIES ARE PROPERLY SET WHEN CREATING THE RECIPE
     * @.post recipe has a new list which has it's stages mixed but the end result is the same and
     * equally tasty
     * @param recipe the recipe to be handled
     */
    public static void shuffleStages(Recipe recipe) {
        LOG.fine("Shuffling stages for recipe" + recipe.getId());

        // random number generator
        Random rng = new Random();

        // form the stage lines
        List<StageLine> stageLines = getStageLines(recipe);
        List<Stage> newList = new ArrayList<>();
        Set<StageLine> passed = new HashSet<>();

        // repeat while there are still stage lines to output
        while(passed.size() < stageLines.size()) {
            // go through each stage line and get it's node stage
            for (StageLine sl : stageLines) {
                // do not output twice
                if (passed.contains(sl)) break;
                // fetch it's node stage
                Stage node = sl.getNode();
                // get all of the node's preceding stages
                final List<StageLine> precedingLines = StageLine.getPrecedingLines(stageLines, node);
                // list to contain all the 'equal' stage lines that can be shuffled with each other
                List<StageLine> linesToBeShuffled = new ArrayList<>();
                // flag to indicate if the preceding lines can be outputted
                // do not allow if there's at least one line which has unoutputted dependencies
                AtomicBoolean canProceed = new AtomicBoolean(true);
                // go through the preceding lines and check if it can be allowed through
                for (StageLine sl2 : precedingLines) {
                    if (!passed.contains(sl2)) {
                        if (!sl2.isRoot()) {
                            // go through the grandparents and check if they are all outputted already
                            StageLine.getPrecedingLines(stageLines, sl2.getFirst())
                                .stream()
                                .forEach((sl3) -> {
                                    if (!passed.contains(sl3)) {
                                        canProceed.set(false);
                                    }
                                });
                        }
                        linesToBeShuffled.add(sl2);
                    }
                }
                // if all the preceding lines were legit
                if(canProceed.get() == true) {
                    // proceed to outputting them
                    // alternate to shuffle them but keep their own order
                    while(true) {
                        if(sl.getIsLastLeaf()) {
                            newList.add(sl.getNode());
                            linesToBeShuffled.clear();
                            linesToBeShuffled.add(sl);
                            break;
                        }
                        for (int i = 0; i < linesToBeShuffled.size(); i++) {
                            // choose line to output from
                            int random = rng.nextInt(linesToBeShuffled.size());
                            List<Stage> tempSl = linesToBeShuffled.get(random).getStageLine();
                            // do not output from empty list or the node stage
                            if(tempSl.size() <= 1) break;
                            List<Stage> tempStage = linesToBeShuffled.get(random).getStageLine();
                            // shift the first
                            newList.add(tempStage.get(0));
                            tempStage.remove(0);
                        }

                        boolean done = true;
                        // check if everything is outputted
                        for(StageLine sl4 : linesToBeShuffled) {
                            if(sl4.getStageLine().size() > 1)
                                done = false;
                        }
                        if(done == true) break;
                    }
                    // mark lines as passed
                    linesToBeShuffled.stream().forEach((l) -> { passed.add(l); });
                }
            }
        }

        recipe.setStages(newList);

        LOG.info("Recipe " + recipe.getId() + " stages shuffled");
    }

    /**
     * Builds StageLine objects from the list of Recipe's stages using the information
     * withing each stage about their relations to other stages
     * @.pre recipe != null && (recipe.getId() != null & recipe.getStages() != null)
     * @.post FORALL(s : getRootStages(); s.getFirst().isRoot() == true) &
     *        EXISTS(s : RESULT; s.getIsLeafStage() == true)
     * @param recipe recipe
     * @return list of newly built StageLine objects from the recipe's stages
     */
    private static List<StageLine> getStageLines(Recipe recipe) {
        LOG.fine("Forming stage lines");

        // get the root stages, which begin from nothing
        final List<Stage> rootStages = getRootStages(recipe);
        // form the first lines from root stages
        final List<StageLine> lines = rootStages.stream().map((r) ->
                getStageLine(r)).collect(Collectors.toList());
        List<StageLine> resultLines = new ArrayList<>();

        LOG.fine("Forming recursively the stage lines beginning from roots");

        lines.stream().forEach((sl) -> {
            resultLines.add(sl);
            Stage node = sl.getNode();
            // form the rest of the stage lines recursively
            while (true) {
                if (StageLine.stageLineExits(resultLines, node)) break;
                StageLine tempLine = getStageLine(node);
                resultLines.add(tempLine);
                if (tempLine.getIsLastLeaf()) break;
                node = tempLine.getNode();
            }
        });

        return resultLines;
    }

    /**
     * Forms a stage line beginning from the given stage
     * @param stage stage to begin the line from
     * @return StageLine object
     */
    private static StageLine getStageLine(Stage stage) {

        LOG.fine("Forming a stage line from " + stage.getId());

        // composes a straight 'line' of stages that follow each other until a node is found
        // node will be the last element in line
        List<Stage> line = new ArrayList<>();
        StageLine sLine = new StageLine();
        int count = 0;
        while (true) {
            line.add(stage);
            // bump into a leaf
            if (stage.isLeaf()) {
                sLine.setNode(stage);
                if(line.size() == 1) {
                    sLine.setIsLastLeaf(true);
                } else {
                    if(stage.getParents().size() == 1) {
                        sLine.setIsLastLeaf(true);
                    }
                }
                break;
            }
            // bump into a node
            // ignore this for the first stage because it's going to be a node
            if (stage.isNode() && count > 0) {
                sLine.setNode(stage);
                break;
            }
            // follow to the next in line
            stage = stage.getChildren().get(0);
            count++;
        }
        sLine.setStageLine(line);
        return sLine;
    }

    /**
     * Outputs the stages one by one
     * @.pre recipe != null & out != null
     * @.post the stages' textual description will be outputted one by one, in correct order
     * @param out PrintStream into which the stages will be outputted to
     */
    public static void outputRecipeStages(Recipe recipe, PrintStream out) {
        LOG.fine("Outputting stages of recipe " + recipe.getId());
        recipe.getStages().stream().forEach((s) -> {
            out.println(s.getDescription());
        });
    }

    /**
     * Outputs the ingredients one by one
     * @.pre recipe != null & out != null
     * @.post the ingredients' textual description will be outputted one by one, in correct order
     * @param out PrintStream into which the stages will be outputted to
     */
    public static void outputRecipeIngredients(Recipe recipe, PrintStream out) {
        recipe.getIngredients().stream().forEach((s) -> {
            out.println(s.getDescription());
        });
    }

    /**
     * Creates and returns a list of the root stages of the recipe
     * Meaning stages which have no parents
     * @param recipe
     * @return list of root Stages
     */
    private static List<Stage> getRootStages(Recipe recipe) {
        LOG.fine("Fetching root lines of recipe " + recipe.getId());
        List<Stage> stages = recipe.getStages();
        return stages.stream().filter((t) -> t.isRoot()).collect(Collectors.toList());
    }

    // class logger
    private static Logger LOG = Logger.getLogger(RecipeUtils.class.getName());
}
