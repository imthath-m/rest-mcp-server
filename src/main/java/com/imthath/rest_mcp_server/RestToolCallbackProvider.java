package com.imthath.rest_mcp_server;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.util.ToolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class RestToolCallbackProvider implements ToolCallbackProvider {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public MethodToolCallback[] getToolCallbacks() {
        return getRestControllerBeans(applicationContext)
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

    private Stream<Object> getRestControllerBeans(ApplicationContext applicationContext) {
        return Arrays
                .stream(applicationContext.getBeanDefinitionNames())
                .flatMap(beanName -> {
                    // Skip the menuTools bean itself to avoid circular dependency
                    if ("menuTools".equals(beanName)) {
                        return Stream.empty();
                    }
                    try {
                        Object bean = applicationContext.getBean(beanName);
                        if (bean.getClass().isAnnotationPresent(RestController.class)) {
                            return Stream.of(bean);
                        }
                    } catch (Exception e) {
                        // Skip if there's an error getting the bean
                    }
                    return Stream.empty();
                });
    }
}