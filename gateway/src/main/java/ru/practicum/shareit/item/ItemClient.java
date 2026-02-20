package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ItemClient(String url, RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(ItemRequestDto itemDto, Integer ownerId) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(Integer itemId, ItemRequestDto updateDto, Integer ownerId) {
        return patch("/" + itemId, ownerId, updateDto);
    }

    public ResponseEntity<Object> getByIdForOwner(Integer itemId, Integer userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getById(Integer itemId) {
        return get("/simple/" + itemId);
    }

    public ResponseEntity<Object> getAllByOwner(Integer ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text, Integer userId) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> delete(Integer itemId, Integer ownerId) {
        return delete("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> addComment(Integer itemId, Integer userId, CommentRequestDto commentRequestDto) {
        return post("/" + itemId + "/comment", userId, commentRequestDto);
    }
}