
import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    public static void main(String[] args) throws IOException {
        var server = new Server();
        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png",
                "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html",
                "/classic.html", "/events.html", "/events.js");
        ExecutorService executor = Executors.newFixedThreadPool(64);

        try (final var serverSocket = server.start(9999)) {
            while (true) {
                try (
                        final var socket = server.startSocket(serverSocket);
                ) {

                            server.processingConnection(socket, validPaths);
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}












