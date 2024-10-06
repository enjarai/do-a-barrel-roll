package nl.enjarai.doabarrelroll.config.serialization;

import com.google.gson.*;
import nl.enjarai.doabarrelroll.math.ExpressionParser;

import java.lang.reflect.Type;

public class ExpressionParserTypeAdapter implements JsonSerializer<ExpressionParser>, JsonDeserializer<ExpressionParser> {
    @Override
    public ExpressionParser deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new ExpressionParser(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(ExpressionParser expressionParser, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(expressionParser.getString());
    }
}
