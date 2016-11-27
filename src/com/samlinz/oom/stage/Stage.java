package com.samlinz.oom.stage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stage of production
 * Created by samlinz on 25.11.2016.
 */
public class Stage {

    private int id;

    // stages which are dependents of this stage
    private List<Stage> children;

    // the stage's dependents, must be completed before this stage
    private List<Stage> parents;

    private String description;

    Stage() {
        children = new ArrayList<>();
        id = -1;
        parents = new ArrayList<>();
        description = "no description set";

        LOG.fine("Empty Stage object initialized");
    }

    public void addChild(Stage stage) {
        children.add(stage);
    }

    public void addParent(Stage stage) {
        parents.add(stage);
    }

    public List<Stage> getChildren() {
        return children;
    }

    public List<Stage> getParents() {
        return parents;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public boolean isRoot() {
        return parents.isEmpty();
    }

    public boolean isNode() {
        return parents.size() > 1 || children.size() > 1;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Logger
    private Logger LOG = Logger.getLogger(Stage.class.getName());

}
