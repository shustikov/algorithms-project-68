package hexlet.code;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RouterTest {
    private static final List<Map<String, Object>> routes = List.of(
            Map.of(
                    "path", "/courses/:id",
                    "handler", Map.of("body", "course!")
            ),
            Map.of(
                    "path", "/courses/:course_id/exercises/:id",
                    "handler", Map.of("body", "exercise!")
            )
    );

    @Test
    void serve() {
        var res = Router.serve(routes, "/courses/php_trees");

        assertEquals("course!", res.get("body"));
    }
}