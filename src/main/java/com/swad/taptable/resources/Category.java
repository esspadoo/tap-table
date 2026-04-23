package com.swad.taptable.resources;

import java.io.OutputStream;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Represents the category of a menu dish.
 *
 * @author SWAD Team
 */
public class Category extends AbstractResource {

    private final String name;

    public Category(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    protected void writeJSON(OutputStream out) throws Exception {
        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();
        if (name == null)
            jg.writeNullField("name");
        else
            jg.writeStringField("name", name);
        jg.writeEndObject();

        jg.flush();
    }
}