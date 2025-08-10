/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.google.gson.Strictness
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
 *  com.google.gson.stream.MalformedJsonException
 */
package net.minecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class StrictJsonParser {
    public static JsonElement parse(Reader $$0) throws JsonIOException, JsonSyntaxException {
        try {
            JsonReader $$1 = new JsonReader($$0);
            $$1.setStrictness(Strictness.STRICT);
            JsonElement $$2 = JsonParser.parseReader((JsonReader)$$1);
            if (!$$2.isJsonNull() && $$1.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonSyntaxException("Did not consume the entire document.");
            }
            return $$2;
        } catch (MalformedJsonException | NumberFormatException $$3) {
            throw new JsonSyntaxException($$3);
        } catch (IOException $$4) {
            throw new JsonIOException((Throwable)$$4);
        }
    }

    public static JsonElement parse(String $$0) throws JsonSyntaxException {
        return StrictJsonParser.parse(new StringReader($$0));
    }
}

