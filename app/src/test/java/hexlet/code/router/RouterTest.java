package hexlet.code.router;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouterTest {
    private static final List<Map<String, Object>> ROUTES = List.of(
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

    private static final List<Map<String, Object>> ROUTES_MAIN = List.of(
            Map.of(
                    "method", "POST",
                    "path", "users/long/:id",
                    "handler", Map.of("body", "handler1"),
                    "constraints", Map.of("id", "\\d+")
            ),
            Map.of(
                    "path", "users/long/:way",
                    "handler", Map.of("body", "handler2"),
                    "constraints", Map.of("way", "[a-z]")
            ),
            Map.of(
                    "path", "users/long/way/:name",
                    "handler", Map.of("body", "handler3"),
                    "constraints", Map.of("name", "[a-z]+")
            ),
            Map.of(
                    "path", "api/:id/:name/risc-v",
                    "handler", Map.of("body", "handler4"),
                    "constraints", Map.of("id", ".", "name", "^[a-z]+$")
            ),
            Map.of(
                    "method", "PUT",
                    "path", "api/:id/:uid",
                    "handler", Map.of("body", "handler5")
            ),
            Map.of(
                    "path", "api/to/Japan/",
                    "handler", Map.of("body", "handler6")
            ),
            Map.of(
                    "path", "/",
                    "handler", Map.of("body", "root")
            ),
            Map.of(
                    "path", "/courses/:course_id/exercises/:id",
                    "handler", Map.of("body", "exercise!"),
                    "constraints", Map.of("id", "\\d+", "course_id", "^[a-z]+$")
            )
    );


    @Test
    void serve() {
        var request = Map.of("path", "/courses", "method", "POST");
        var res = Router.serve(ROUTES, request);

        assertEquals("created!", ((Map) res.get("handler")).get("body"));
    }

    @Test
    void serveWithConstraint() {
        var request = Map.of("path", "/courses/js/exercises/1", "method", "GET");

        var res = Router.serve(ROUTES, request);

        assertEquals("exercise!", ((Map) res.get("handler")).get("body"));
    }

    @Test
    void serveMain() {
        var request = Map.of("path", "api/to/Japan/");

        var res = Router.serve(ROUTES_MAIN, request);

        System.out.println(res);
    }
}
