// Development server (wasmJsBrowserDevelopmentRun) in front of the Spring Boot server (bootRun on port 8080):
// it serves the browser bundle, reloads the page when the bundle changes, and proxies everything else
// except the source maps and the Kotlin sources they reference
if (config.devServer) {
    config.devServer.port = 8081;
    config.devServer.proxy = [{
        context: (path) => !/\.(map|kt)$/.test(path),
        target: "http://localhost:8080"
    }];
}
