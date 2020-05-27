import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    Socket socket = null;
    ServerSocket serverSocket = null;



    TCPServer(){

    }

    void run(int port){
        try{
            serverSocket = new ServerSocket(port);


            while(true) {
                socket = serverSocket.accept();
                System.out.println("client connected : " + socket.getInetAddress().getHostName());

                new HTTPHandleThread(socket).start();
            }


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void GET(){

    }
}
