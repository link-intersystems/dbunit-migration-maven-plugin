package com.link_intersystems.dbunit.maven;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class Placeholder {

    private String name;
    private String replacement;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }
}
