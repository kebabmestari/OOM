package com.samlinz.oom.stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by samlinz on 25.11.2016.
 */
public class StageFactory {

    public static Stage fetchStage(List<Stage> stages, int id) {
        AtomicReference<Stage> result = new AtomicReference<>(null);
        stages.stream().forEach((s) -> {
            if(id == s.getId())
                result.set(s);
        });
        return result.get();
    }

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

    public static Stage getStage(int id, String description) {
        Stage newStage = new Stage();
        newStage.setId(id);
        newStage.setDescription(description);
        return newStage;
    }
}
