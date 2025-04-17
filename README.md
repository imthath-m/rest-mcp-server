# SpringBoot-MCP

A zero-configuration tool for automatically exposing Spring Boot endpoints as Model Context Protocol (MCP) tools.

![Maven Central](https://img.shields.io/maven-central/v/[your-group-id]/springboot-mcp)
![Java Version](https://img.shields.io/badge/Java-21%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)

## Features

* **Direct integration** - Mount an MCP server directly to your Spring Boot application
* **Zero configuration** required - just add the dependency and minimal configuration
* **Automatic discovery** of all Spring REST endpoints and conversion to MCP tools
* **Preserving schemas** of your request and response models
* **Preserve documentation** of all your endpoints from OpenAPI/Swagger
* **Flexible deployment** - Mount your MCP server to the same app, or deploy separately

## Installation

Add the dependency to your Maven `pom.xml`:

```xml
<dependency>
    <groupId>[your-group-id]</groupId>
    <artifactId>springboot-mcp</artifactId>
    <version>[latest-version]</version>
</dependency>
```

Or to your Gradle build file:

```groovy
implementation '[your-group-id]:springboot-mcp:[latest-version]'
```

## Basic Usage

The simplest way to use SpringBoot-MCP is to add it to your Spring Boot application:

```java
import com.example.springbootmcp.MCPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public MCPServer mcpServer() {
        return MCPServer.builder()
            .name("My API MCP")
            .description("My API description")
            .baseUrl("http://localhost:8080")
            .build();
    }
}
```

That's it! Your auto-generated MCP server is now available at `https://app.base.url/mcp`.

> **Note on `baseUrl`**: While `baseUrl` is optional, it is highly recommended to provide it explicitly. The `baseUrl` tells the MCP server where to send API requests when tools are called. Without it, the library will attempt to determine the URL automatically, which may not work correctly in deployed environments where the internal and external URLs differ.

## Tool Naming

SpringBoot-MCP uses the operation ID from your OpenAPI/Swagger documentation as the MCP tool names. When you don't specify an operation ID, it will be auto-generated based on the method name and path.

Compare these two endpoint definitions:

```java
// Auto-generated operation ID (something like "getUserUsingGET")
@GetMapping("/users/{userId}")
public User getUser(@PathVariable Long userId) {
    return userService.findById(userId);
}

// Explicit operation ID (tool will be named "get_user_info")
@Operation(operationId = "get_user_info")
@GetMapping("/users/{userId}")
public User getUser(@PathVariable Long userId) {
    return userService.findById(userId);
}
```

For clearer, more intuitive tool names, we recommend adding explicit `@Operation(operationId = "...")` annotations to your controller methods.

## Advanced Usage

### Customizing Schema Description

```java
@Bean
public MCPServer mcpServer() {
    return MCPServer.builder()
        .name("My API MCP")
        .baseUrl("http://localhost:8080")
        .describeAllResponses(true)     // Include all possible response schemas
        .describeFullResponseSchema(true)  // Include full JSON schema
        .build();
}
```

### Customizing Exposed Endpoints

You can control which endpoints are exposed as MCP tools using operation IDs or tags:

```java
@Bean
public MCPServer mcpServer() {
    return MCPServer.builder()
        .includeOperations(List.of("get_user", "create_user"))
        // or
        .excludeOperations(List.of("delete_user"))
        // or
        .includeTags(List.of("users", "public"))
        // or
        .excludeTags(List.of("admin", "internal"))
        .build();
}
```

## Examples

See the `examples` directory for complete examples.

## Connecting to the MCP Server

Once your Spring Boot app with MCP integration is running, you can connect to it with any MCP client supporting SSE or stdio protocols.

### Using SSE (e.g., with Cursor)

1. Run your application
2. In Cursor -> Settings -> MCP, use the URL `http://localhost:8080/mcp` as SSE endpoint
3. Cursor will discover all available tools automatically


## Development and Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Requirements

* Java 17 or higher
* Spring Boot 3.x

## License

MIT License. Copyright (c) [Year] [Your Name/Organization]

