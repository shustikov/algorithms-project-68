package hexlet.code;

import java.util.List;
import java.util.Map;

public class Router {
    public static Map<String, String> serve(List<Map<String, Object>> routes, String request) {
        return routes.stream()
                .filter(item -> item.get("path").equals(request))
                .map(item -> item.get("handler"))
                .map(handler -> (Map<String, String>) handler)
                .findFirst().orElseThrow();
    }
}
