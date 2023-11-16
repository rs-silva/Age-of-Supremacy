package org.example.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonPropertySourceLoader implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        Map<String, Object> source = loadJsonProperties(encodedResource);

        if (source.isEmpty()) {
            return new MapPropertySource(name, source);
        } else {
            return new OriginTrackedMapPropertySource(name, source);
        }
    }

    private Map<String, Object> loadJsonProperties(EncodedResource resource) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

        Map<String, Object> source = new HashMap<>();
        flattenJsonNode("", rootNode, source);

        return source;
    }

    private void flattenJsonNode(String currentPath, JsonNode node, Map<String, Object> flattened) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String path = StringUtils.isEmpty(currentPath) ? entry.getKey() : currentPath + "." + entry.getKey();
                flattenJsonNode(path, entry.getValue(), flattened);
            }
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                flattenJsonNode(currentPath + "[" + i + "]", node.get(i), flattened);
            }
        } else {
            flattened.put(currentPath, node.asText());
        }
    }

}