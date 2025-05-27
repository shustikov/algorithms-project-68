package hexlet.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        var requestArray = request.split("/");
        var node = root;
        var params = new ArrayList<String>();
        for (var requestItem : requestArray) {
            if (node.children.size() == 1) {
                var childKey = node.children.keySet().iterator().next();
                if (childKey.startsWith(":")) {
                    node = node.children.get(childKey);
                    params.add(requestItem);
                    continue;
                }
            }
            node = node.children.get(requestItem);
        }

        var paramMap = new HashMap<String, String>();
        for (int i = 0; i < params.size(); i++) {
            var constrain = node.paramsWithConstraint.get(i).getValue();
            var name = node.paramsWithConstraint.get(i).getKey();
            var value = params.get(i);
            if (Pattern.matches(constrain, value)) {
                paramMap.put(name, value);
            } else {
                throw new IllegalStateException();
            }
        }
        return Map.of(
                "handler", node.handler.get(method),
                "params", paramMap
        );
    }

    private static PrefixTreeNode buildPrefixTree(List<Map<String, Object>> routes) {
        var root = new PrefixTreeNode("", null);
        for (var routeRule : routes) {
            var node = root;
            var params = new ArrayList<String>();
            Map<String, String> costraints;
            if (routeRule.containsKey("constraints")) {
                costraints = (Map<String, String>) routeRule.get("constraints");
            } else {
                costraints = Map.of();
            }
            var routArray = ((String) routeRule.get("path")).split("/");
            for (var routeItem : routArray) {
                if (!node.children.isEmpty() && node.children.containsKey(routeItem)) {
                    node = node.children.get(routeItem);
                } else {
                    node = node.addNode(routeItem, null);
                }
                if (routeItem.startsWith(":")) {
                    params.add(routeItem.replace(":", ""));
                }
            }
            node.setHandler((String) routeRule.get("method"), (Map<String, Object>) routeRule.get("handler"));
            node.paramsWithConstraint = params.stream().map(param -> Map.entry(param, costraints.getOrDefault(param, ""))).toList();
        }
        return root;
    }

    private static class PrefixTreeNode {
        private final String value;
        private HashMap<String, PrefixTreeNode> children;
        private Map<String, Object> handler;
        private List<Map.Entry<String, String>> paramsWithConstraint;

        PrefixTreeNode(String value, Map<String, Object> handler) {
            this.handler = handler;
            this.children = new HashMap<>();
            this.handler = new HashMap<>();
            if (value.startsWith(":")) {
                this.value = ":param";
            } else {
                this.value = value;
            }
        }

        public PrefixTreeNode addNode(String value, Map<String, Object> handler) {
            var node = new PrefixTreeNode(value, handler);
            this.children.put(node.value, node);
            return node;
        }

        public void setHandler(String method, Map<String, Object> handler) {
            this.handler.put(method, handler);
        }
    }
}
