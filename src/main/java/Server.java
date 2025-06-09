import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;



public class Server {
    int port;

    ServerSocket start(int port) throws IOException {

        return new ServerSocket(port);
    }

    Socket startSocket(ServerSocket serverSocket) throws IOException {

        return serverSocket.accept();
    }
  void processingConnection(Socket socket, List<String> validPaths) throws IOException {
       final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final var out = new BufferedOutputStream(socket.getOutputStream());
        final String[] parts = in.readLine().split(" ");

        final var path = parts[1];
        if (parts.length != 3 && !validPaths.contains(path)) {
            // just close socket
   out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        }
 final Path filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

         if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
        }
         final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }
}