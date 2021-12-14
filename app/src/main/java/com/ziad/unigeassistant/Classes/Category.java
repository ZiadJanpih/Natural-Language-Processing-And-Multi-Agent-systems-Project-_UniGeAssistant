package com.ziad.unigeassistant.Classes;

public class Category {
    public long id;
    public String pattern;
    public String template;
    public String type;
    public String added_by;

    public Category( String pattern, String template, String type, String added_by) {
        this.pattern = pattern;
        this.template = template;
        this.type = type;
        this.added_by = added_by;
    }
    public Category(){}
}
