package com.samlinz.oom.stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Factory class for stages
 * Created by samlinz on 25.11.2016.
 */
public class StageFactory {

    /**
     * Find and return a stage from the given list, by it's
     * identification number
     * @.pre FORALL(s, b  : stages, stages; s != b && s.getId() != b.getId()))
     * @.post RESULT == the only Stage with the identification number of id
     * @param stages list of stages to search from
     * @param id id to search from
     * @return the Stage object
     */
    public static Stage fetchStage(List<Stage> stages, int id) {
        AtomicReference<Stage> result = new AtomicReference<>(null);
        stages.stream().forEach((s) -> {
            if(id == s.getId())
                result.set(s);
        });
        return result.get();
    }

    /**
     * Fix the children relations for each stage
     * When building the stages, only parent relations aka dependencies are set
     * so this fixes the links by setting the child references forming a two-way
     * linked list
     * @.pre stages != null
     * @.post FORALL(s, b : stages, stages; s.getChildren().getParents().contains(s) == true)
     * @param stages list of all stages
     */
    public static void fixChildren(List<Stage> stages) {
        // go through each stage and link them to their children
        for(Stage s : stages) {
            for(Stage c : stages) {
                // if they are not the same
                if(s != c) {
                    if(c.getParents().contains(s)) {
                        s.addChild(c);
                    }
                }
            }
        }
    }

    /**
     * Builds and returns a new Stage object with the given parameters
     * @param id identification number
     * @param description stage textual description
     * @.pre description != null
     * @.post RESULT != null && (RESULT.getId() == id & RESULT.getDescription() == description)
     * @return a new Stage object
     */
    public static Stage getStage(int id, String description) {
        Stage newStage = new Stage();
        newStage.setId(id);
        newStage.setDescription(description);
        return newStage;
    }
}
