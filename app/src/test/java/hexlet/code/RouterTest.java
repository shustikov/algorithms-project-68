package hexlet.code;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RouterTest {
    private static final List<Map<String, Object>> routes = List.of(
            Map.of(
                    "method", "GET",
                    "path", "/courses/:id",
                    "handler", Map.of("body", "course!")
            ),
            Map.of(
                    "method", "POST",
                    "path", "/courses",
                    "handler", Map.of("body", "created!")
            ),
            Map.of(
                    "method", "GET",
                    "path", "/courses/:course_id/exercises/:id",
                    "handler", Map.of("body", "exercise!")
            )
    );

    @Test
    void serve() {
        var request = Map.of("path", "/courses", "method", "POST");
        var res = Router.serve(routes, request);

        assertEquals("created!", res.get("body"));
    }
}