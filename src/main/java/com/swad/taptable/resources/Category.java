package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public static Category fromJSON(final InputStream in) throws IOException, UnexpectedKeyException {
        String jName = null;

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            while (jp.nextToken() != JsonToken.END_OBJECT) {
                if (jp.getCurrentToken() != JsonToken.FIELD_NAME)
                    continue;

                switch (jp.currentName()) {
                    case "name":
                        jp.nextToken();
                        jName = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText();
                        break;
                    default:
                        throw new UnexpectedKeyException("Unexpected field: " + jp.currentName());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Unable to parse a Category object from JSON.", e);
            throw e;
        }

        return new Category(jName);
    }
}
