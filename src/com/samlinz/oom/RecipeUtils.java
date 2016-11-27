package com.samlinz.oom;

import com.samlinz.oom.stage.Stage;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class StageLine implements Serializable {

    private Stage node;
    private List<Stage> stageLine;
    private boolean isLastLeaf;

    public StageLine() {
    }

    public boolean getIsLastLeaf() {
        return isLastLeaf;
    }

    public boolean setIsLastLeaf(boolean isLastLeaf) {
        this.isLastLeaf = isLastLeaf;
    }

    public boolean isRoot() {
        return stageLine.get(0).isRoot();
    }

    public Stage getFirst() {
        return stageLine.get(0);
    }

    public Stage getNode() {
        return node;
    }

    public void setNode(Stage node) {
        this.node = node;
    }

    public void setStageLine(List<Stage> stageLine) {
        this.stageLine = stageLine;
    }

    public List<Stage> getStageLine() {
        return stageLine;
    }

    public static List<StageLine> getPrecedingLines(List<StageLine> stagelines, Stage node) {
        List<StageLine> result = new ArrayList<>();
        for (StageLine s : stagelines) {
            if (s.getNode() == node && !s.isLastLeaf())
                result.add(s);
        }
        return result;
    }

    public static boolean stageLineExits(List<StageLine> stageLines, Stage first) {
        if (getStageLine(stageLines, first) != null) return true;
        return false;
    }

    public static StageLine getStageLine(List<StageLine> stageLines, Stage first) {
        for (StageLine sl : stageLines) {
            if (sl.getFirst() == first)
                return sl;
        }
        return null;
    }
}

/**
 * Created by samlinz on 25.11.2016.
 */
public class RecipeUtils {
    public static void shuffleIngredients(Recipe recipe) {
        Collections.shuffle(recipe.getIngredients());
    }

    public static void shuffleStages(Recipe recipe) {
        LOG.fine("Shuffling stages for recipe" + recipe.getId());

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
                    System.out.println();
                }
            }
        }

        LOG.info("Recipe " + recipe.getId() + " stages shuffled");
    }

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
                if (tempLine.isLastLeaf()) break;
                node = tempLine.getNode();
            }
        });

        return resultLines;
    }

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
                sLine.setIsLastLeaf(false);
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

    public static List<Stage> getRootStages(Recipe recipe) {

        LOG.fine("Fetching root lines");

        List<Stage> stages = recipe.getStages();

        return stages.stream().filter((t) -> t.isRoot()).collect(Collectors.toList());
    }

    private static Logger LOG = Logger.getLogger(RecipeUtils.class.getName());
}
