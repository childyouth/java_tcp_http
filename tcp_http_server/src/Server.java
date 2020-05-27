import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String arg[]){
        TCPServer tcpServer = new TCPServer();

        String path = System.getProperty("user.dir");
        System.out.println("Working Directory = " + path);
        tcpServer.run(80);
    }
}
