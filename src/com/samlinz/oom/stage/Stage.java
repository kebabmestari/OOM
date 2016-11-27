package com.samlinz.oom.stage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A stage in cooking process
 * Stage has an id for identifications
 * References to parents and children for linking to other stages
 * A description to tell the customer what the IRL stage consists of
 *
 * @author Samuel Lindqvist
 */
public class Stage {

    // identification number, must be unique or funny stuff happens
    private int id;
    // stages which are dependents of this stage
    private List<Stage> children;
    // the stage's dependents, must be completed before this stage
    private List<Stage> parents;
    // stage description
    private String description;

    /**
     * Constructor, package-private
     * Use StageFactory to instatiate a new Stage object in a controlled manner
     * @.pre true
     * @.post this.getChildren().size() == 0 & this.getParents().size() == 0
     */
    Stage() {
        children = new ArrayList<>();
        id = -1;
        parents = new ArrayList<>();
        description = "no description set";

        LOG.fine("Empty Stage object initialized");
    }

    /**
     * Adds a reference to a child stage
     * Child stage depends on this production stage
     * Children are set with Recipe.finish(), not manually
     * @.pre stage != null && EXISTS(s : stage.getParents(); s == this)
     * @.post a legal link between related classes is instatiated
     * @param stage reference to child
     */
    public void addChild(Stage stage) {
        children.add(stage);
    }

    /**
     * Adds a parent/dependency stage to this stage
     * @.pre stage != null
     * @.post
     * @param stage a reference to parent
     */
    public void addParent(Stage stage) {
        parents.add(stage);
    }

    /**
     * @.pre children are set with addChild
     * @.post RESULT != null
     * @return list of children
     */
    public List<Stage> getChildren() {
        return children;
    }

    /**
     * @.pre parents are set with addParent
     * @.post RESULT != null
     * @return list of children
     */
    public List<Stage> getParents() {
        return parents;
    }

    /**
     * @.pre id is set with setId
     * @.post RESULT > 0
     * @return id of the stage
     */
    public int getId() {
        return id;
    }

    /**
     * @.pre true
     * @.post getId() == id
     */
    void setId(int id) {
        this.id = id;
    }

    /**
     * @.pre true
     * @.post true
     * @return true if the stage is a root stage
     */
    public boolean isRoot() {
        return parents.isEmpty();
    }

    /**
     * Gets if the stage is a node stage
     * Node stage has either multiple parents or multiple children
     * @.pre true
     * @.post true
     * @return true if the stage is a node in the 'tree' of related stages
     */
    public boolean isNode() {
        return parents.size() > 1 || children.size() > 1;
    }

    /**
     * Gets whether the stage is a 'leaf' stage
     * Leaf stage ends the line of related stages, having no children
     * @.pre true
     * @.post RESULT == getChildren.isEmpty()
     * @return true if the stage is a leaf
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * @.pre true
     * @.post RESULT == this.description
     * @return stage textual description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the stage description, the text is free form and
     * is supposed to describe the actions in the phase properly
     * @.pre description != null
     * @.post getDescription() == description
     * @param description The stage textual description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    // Logger
    private Logger LOG = Logger.getLogger(Stage.class.getName());

}
