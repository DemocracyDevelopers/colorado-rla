/*
  Copyright 2023 Democracy Developers

 */

package au.org.democracydevelopers.raire.responsefromraire;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

/**
 * Everything that could go wrong when retrieving assertions.
 *
 **/
@JsonDeserialize(using= GetAssertionError.GetAssertionsErrorDeserializer.class)
public abstract class GetAssertionError {
    public static class NoAssertions extends GetAssertionError {}
    public static class ErrorRetrievingAssertions extends GetAssertionError {}
    public static class InvalidRequest extends GetAssertionError {}

    /** Custom JSON serializer for Jackson */
    public static class GetAssertionsErrorDeserializer extends StdDeserializer<GetAssertionError> {

        public GetAssertionsErrorDeserializer() { this(null); }
        public GetAssertionsErrorDeserializer(Class<GetAssertionError> t) { super(t); }

        @Override
        public GetAssertionError deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (node.isTextual()) {
                String text = node.asText();
                switch (text) {
                    case "NoAssertionsForThisContest":
                        return new GetAssertionError.NoAssertions();
                    case "ErrorRetrievingAssertions":
                        return new GetAssertionError.ErrorRetrievingAssertions();
                    case "InvalidGetAssertionRequest":
                        return new GetAssertionError.InvalidRequest();
                }
            }

            throw new IOException("Do not understand RaireError "+node);
        }
    }
}
