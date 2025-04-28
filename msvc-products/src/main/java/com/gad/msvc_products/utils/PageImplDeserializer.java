package com.gad.msvc_products.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public class PageImplDeserializer extends JsonDeserializer<PageImpl> {
    @Override
    public PageImpl deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        JsonNode dataNode = node.get("data");

        List<Object> content = jsonParser.getCodec().treeToValue(node.get("content"), List.class);

        JsonNode pageNode = dataNode.get("page");
        int pageNumber = pageNode.get("number").asInt();
        int pageSize = pageNode.get("size").asInt();
        long totalElements = dataNode.get("totalElements").asLong();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(content, pageable, totalElements);
    }
}
