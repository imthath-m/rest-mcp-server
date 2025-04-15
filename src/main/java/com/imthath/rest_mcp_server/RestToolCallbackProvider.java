package com.imthath.rest_mcp_server;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.util.ToolUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class RestToolCallbackProvider implements ToolCallbackProvider, ApplicationListener<ContextRefreshedEvent> {

    private Map<String, Object> restBeans;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // This method is called when the application context is refreshed
        // You can perform any initialization or setup here if needed
        restBeans = event.getApplicationContext().getBeansWithAnnotation(RestController.class);
    }

    @Override
    public MethodToolCallback[] getToolCallbacks() {
        if (restBeans == null) {
            return new MethodToolCallback[0];
        }
        return restBeans
            .values()
            .stream()
            .flatMap(this::getToolCallBacksFromRestControllerObject)
            .toArray(MethodToolCallback[]::new);
    }

    private MethodToolCallback toolCallback(Method method, Object bean) {
        return MethodToolCallback.builder()
                .toolDefinition(ToolDefinition.from(method))
                .toolMethod(method)
                .toolCallResultConverter(ToolUtils.getToolCallResultConverter(method))
                .toolObject(bean)
                .build();
    }

    private Boolean isRequestMethod(Method method) {
        return method.isAnnotationPresent(RequestMapping.class) ||
                method.isAnnotationPresent(GetMapping.class) ||
                method.isAnnotationPresent(PostMapping.class) ||
                method.isAnnotationPresent(PutMapping.class) ||
                method.isAnnotationPresent(DeleteMapping.class) ||
                method.isAnnotationPresent(PatchMapping.class);
    }

    private Stream<MethodToolCallback> getToolCallBacksFromRestControllerObject(Object object) {
        return Arrays
                .stream(object.getClass().getDeclaredMethods())
                .flatMap(method -> {
                    if (isRequestMethod(method)) {
                        return Stream.of(toolCallback(method, object));
                    }
                    return Stream.empty();
                });
    }
}