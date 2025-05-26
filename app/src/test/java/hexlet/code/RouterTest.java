package hexlet.code;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RouterTest {
    private static final List<Map<String, Object>> routes =
            List.of(
                    Map.of("path", "/courses", "handler", Map.of("body", "courses")),
                    Map.of("path", "/courses/basics", "handler", Map.of("body", "basics"))
            );

    @Test
    void serve() {
        var res = Router.serve(routes, "/courses");

        assertEquals("courses", res.get("body"));
    }
}