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

    /*@Override
    public PropertySource<?> createPropertySource(
            String name, EncodedResource resource)
            throws IOException {
        Map readValue = new ObjectMapper()
                .readValue(resource.getInputStream(), Map.class);
        return new MapPropertySource("json-property", readValue);
    }*/

    /*@Override
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
        JsonParser parser = objectMapper.getFactory().createParser(resource.getResource().getInputStream());

        ObjectNode node = objectMapper.readTree(parser);
        Map<String, Object> source = objectMapper.convertValue(node, Map.class);

        // Flatten the structure to handle nested properties
        source = flattenMap(source, null);

        return source;
    }

    private Map<String, Object> flattenMap(Map<String, Object> source, String parentKey) {
        return source.entrySet().stream()
                .flatMap(entry -> flattenEntry(entry, parentKey).entrySet().stream())
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Object> flattenEntry(Map.Entry<String, Object> entry, String parentKey) {
        String key = StringUtils.isEmpty(parentKey) ? entry.getKey() : parentKey + "." + entry.getKey();

        if (entry.getValue() instanceof Map) {
            Map<String, Object> nestedMap = (Map<String, Object>) entry.getValue();
            return flattenMap(nestedMap, key);
        } else {
            return Map.of(key, entry.getValue().toString());
        }
    }*/

}