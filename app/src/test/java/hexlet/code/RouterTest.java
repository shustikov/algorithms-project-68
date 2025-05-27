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
                    "handler", Map.of("body", "exercise!"),
                    "constraints", Map.of("id", "\\d+", "course_id", "^[a-z]+$")
            )
    );

    @Test
    void serve() {
        var request = Map.of("path", "/courses", "method", "POST");
        var res = Router.serve(routes, request);

        assertEquals("created!",  ((Map) res.get("handler")).get("body"));
    }

    @Test
    void serve_withConstraint() {
        var request = Map.of("path", "/courses/js/exercises/1", "method", "GET");

        var res = Router.serve(routes, request);

        assertEquals("exercise!", ((Map) res.get("handler")).get("body"));
    }
}