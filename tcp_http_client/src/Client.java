import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] arg){
        TCPclient client = new TCPclient();

        client.connect("172.30.1.43",80);

        System.out.println(client.getData());

        System.out.println("finished");
    }
}

class TCPclient{

    Socket socket = null;
    String data = null;

    TCPclient(){

    }

    int connect(String hostname, int port){
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            socket = new Socket(hostname, port);

            if (socket != null) {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                sendRequest(new PrintStream(outputStream),hostname,"GET");
                getResponse(inputStream);

                //System.out.println(getData());
            }
            else{
                return -1;
            }
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (UnknownHostException e) {
            return 1;
        } catch (IOException e) {
            return 2;
        } catch (Exception e) {
            return 3;
        }

        return 0;
    }

    private void sendRequest(PrintStream printStream, String hostname, String method) {
        printStream.println(method + " / HTTP/1.1");
        printStream.println("HOST: " + hostname);
        printStream.println();
    }

    private void getResponse(InputStream inputStream){
        int content_length = 0;

        data = "";

        String string = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            //TODO content-length 헤더가 먼저 나왔을 경우, chunked transfer encoding 인 경우. chunked는 분리하고 content-length는 while 하나로 묶고 0d0a로 data 확인
            //TODO 해야하지만 내가 직접 서버만들거라서 괜찮다.
            while((string = bufferedReader.readLine()) != null){
                data += string;
                if(string.toUpperCase().contains("content-length".toUpperCase())){
                    content_length = Integer.parseInt(string.replaceAll("[^\\d]",""));
                    System.out.println(content_length);
                    break;
                }
            }
            while(content_length > 0){
                string = bufferedReader.readLine();
                content_length -= string.length() + "\r\n".length(); // 나는 윈도우라서 crlf
                data += string;
            }
//            int bytesread = 0;
//            byte[] buffer = new byte[100];
//            while(bytesread < content_length){
//                bytesread += inputStream.read(buffer,0,2);
//            }
//            data += new String(buffer);
        }catch(Exception e){

        }
    }

    String getData(){
        if(data == null)
            return "null";
        return data;
    }
}