package hexlet.code.router;

import java.util.*;
import java.util.regex.Pattern;


public class Router {
    public static Map<String, Object> serve(List<Map<String, Object>> routes, Map<String, String> request) {
        var path = request.get("path");
        var method = request.getOrDefault("method", "GET");
        var root = buildPrefixTree(routes);
        var result = new HashMap<String, Object>();
        result.put("path", path);
        result.put("method", method);
        result.putAll(getHandler(root, path, method));
        return result;
    }

    private static Map<String, Object> getHandler(PrefixTreeNode root, String request, String method) {
        var requestArray = ("/" + request).split("/");
        var res = explore(root, requestArray, method).getFirst();
        if (!res.containsKey("params")) {
            res.put("params", Map.of());
        }
        return res;
    }

    private static List<HashMap<String, Object>> explore(PrefixTreeNode node, String[] requestArray, String method) {
        var res = new ArrayList<HashMap<String, Object>>();
        var element = new HashMap<String, Object>();
        if (requestArray.length > 1) {
            var newRequestArray = Arrays.copyOfRange(requestArray, 1, requestArray.length);
            if (node.children.containsKey(newRequestArray[0])) {
                res.addAll(explore(node.children.get(newRequestArray[0]), newRequestArray, method));
            } else {
                var paramsValues = node.children.keySet().stream().filter(ch -> ch.startsWith(":")).toList();
                var items = paramsValues.stream().map(key -> node.children.get(key))
                        .filter(paramNode -> paramMatchesConstrain(paramNode, newRequestArray[0]))
                        .map(n -> Map.entry(n, explore(n, newRequestArray, method)))
                        .filter(out -> !out.getValue().isEmpty())
                        .flatMap(entry -> entry.getValue().stream().map(out -> Map.entry(entry.getKey(), out)))
                        .map(mapEntry -> addParamToMap(mapEntry.getValue(), mapEntry.getKey().value.replace(":", ""), newRequestArray[0]))
                        .toList();
                res.addAll(items);
            }
        } else {
            if (node.handler.containsKey(method)) {
                element.put("handler", node.handler.get(method));
                res.add(element);
            }
        }
        return res;
    }

    private static HashMap<String, Object> addParamToMap(Map<String, Object> map, String name, String value) {
        var params = map.getOrDefault("params", new HashMap<String, String>());
        ((HashMap<String, String>) params).put(name, value);
        map.put("params", params);
        return (HashMap<String, Object>) map;
    }

    private static boolean paramMatchesConstrain(PrefixTreeNode node, String value) {
        if (node.constrain == null) {
            return true;
        } else {
            return Pattern.compile(node.constrain).matcher(value).find();
        }
    }

    private static PrefixTreeNode buildPrefixTree(List<Map<String, Object>> routes) {
        var root = new PrefixTreeNode("");
        for (var routeRule : routes) {
            var node = root;
            var constrains = (Map<String, String>) routeRule.getOrDefault("constraints", Map.of());
            var routArray = ((String) routeRule.get("path")).split("/");
            for (var routeItem : routArray) {
                if (!node.children.isEmpty() && node.children.containsKey(routeItem)) {
                    node = node.children.get(routeItem);
                } else {
                    node = node.addNode(routeItem);
                }
                if (routeItem.startsWith(":") && constrains.size() > 0) {
                    node.setConstrain(constrains.getOrDefault(routeItem.replace(":", ""), routeItem));
                }
            }
            node.setHandler((String) routeRule.getOrDefault("method", "GET"), (Map<String, Object>) routeRule.get("handler"));
        }
        return root;
    }

    private static class PrefixTreeNode {
        private final String value;
        private HashMap<String, PrefixTreeNode> children;
        private Map<String, Object> handler;
        private String constrain;

        PrefixTreeNode(String value) {
            this.children = new HashMap<>();
            this.handler = new HashMap<>();
            this.value = value;
        }

        public PrefixTreeNode addNode(String value) {
            var node = new PrefixTreeNode(value);
            this.children.put(node.value, node);
            return node;
        }

        public void setHandler(String method, Map<String, Object> handler) {
            this.handler.put(method, handler);
        }

        public void setConstrain(String constrain) {
            this.constrain = constrain;
        }
    }
}
