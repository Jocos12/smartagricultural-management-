package SmartAgricultural.Management.Config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Custom deserializer to handle BigDecimal conversion from JSON
 * Accepts both numeric and string representations
 */
public class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        try {
            JsonNode node = jp.getCodec().readTree(jp);

            if (node == null || node.isNull()) {
                return null;
            }

            String value;

            // Handle numeric nodes
            if (node.isNumber()) {
                return node.decimalValue();
            }

            // Handle text nodes
            if (node.isTextual()) {
                value = node.asText();
            } else {
                value = node.toString();
            }

            // Trim whitespace
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            value = value.trim();

            // Remove any quotes that might be present
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }

            // Validate the string is a valid number format
            if (!value.matches("^-?\\d+(\\.\\d+)?$")) {
                throw new IllegalArgumentException(
                        String.format("Invalid BigDecimal format: '%s'. Expected numeric value.", value)
                );
            }

            return new BigDecimal(value);

        } catch (NumberFormatException e) {
            throw new IOException("Cannot deserialize BigDecimal from invalid value", e);
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}