# SpringBoot-MCP

A zero-configuration tool for automatically exposing Spring Boot endpoints as Model Context Protocol (MCP) tools.

![Static Badge](https://img.shields.io/badge/JitPack-ready-green)
![Java Version](https://img.shields.io/badge/Java-21%2B-blue)
![](https://badge.mcpx.dev?type=dev 'MCP Dev')
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)

## Features

* **Direct integration** - Mount an MCP server directly to your Spring Boot application with zero configuration
* **Automatic discovery** of all Spring REST endpoints and conversion to MCP tools
* **Preserving schemas** of your request and response models

## Installation

Add the dependency to your Maven `pom.xml`:

```xml
<dependency>
    <groupId>com.github.imthath-m</groupId>
    <artifactId>rest-mcp-server</artifactId>
    <version>0.2.1</version>
</dependency>
```

Currently, SpringBoot-MCP is not available on Maven central repository. So you have to add the following respoistory in `pom.xml`, to ensure JitPack serves it directly from GitHub.
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

## Basic Usage

After adding the dependency, you need to remove the following starter web dependency from your `pom.xml` file.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
This step is required because, this package depends on `spring-ai-starter-mcp-server-webflux` which already does the basic setup with a Netty server.

That's it! Your auto-generated MCP server is now available at `https://localhost:8080/sse`. You can configure this and other features of the MCP server by using variables specified [here](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html#_configuration_properties).

Once you run the app, you should see the following line your console.
```
o.s.a.m.s.a.McpServerAutoConfiguration   : Registered tools: <number of endpoints>, notification: true
```

FYI, The REST endpoints in your service will continue to be available in the respective paths.


## Tool Naming

SpringBoot-MCP uses the method names from your REST Controllers as the MCP tool names.

## Connecting to the MCP Server

Once your Spring Boot app with MCP integration is running, you can connect to it with any MCP client supporting SSE or stdio protocols.

### Using SSE (e.g., with Cursor)

1. Run your Spring Boot application
2. In Cursor -> Settings -> MCP -> Add new MCP server, use the URL `http://localhost:8080/sse` as SSE endpoint
3. Cursor will discover all available tools automatically

Example cursor config file named `mcp.json`.

```
{
    "mcpServers": {
      "server-name": {
        "url": "http://localhost:8080/sse"
      }
    }
}
```

## Development and Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Requirements

* Java 21 or higher
* Spring Boot 3.x

## License

MIT License. Copyright (c) 2025. Mohammed Imthathullah.