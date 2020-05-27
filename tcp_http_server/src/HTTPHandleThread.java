import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HTTPHandleThread extends Thread {
    private static final int BUFFERSIZE = 10001;

    Socket socket = null;
    String code = null;
    File file = null;
    String content_length = null;

    Map<String, String> code_name = null;

    String response = null;

    HTTPHandleThread(Socket socket){
        this.socket = socket;

        code_name = new HashMap<>();
        code_name.put("200","OK");
        code_name.put("301","Moved Permanently");
        code_name.put("403","Forbidden");
        code_name.put("404","Not Found");
        code_name.put("501","Not Implemented");

    }
    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String string = bufferedReader.readLine();
            httpMethod(string);

            outputStream.write(new String("HTTP/1.1 " + code + " " + code_name.get(code)+"\n").getBytes());
            outputStream.write("Content-Type: text/html\n".getBytes());
            outputStream.write(new String("Content-Length: " + content_length +"\n").getBytes());
            if(file != null){
                FileInputStream fileInputStream = new FileInputStream(file);
                int readbytes = 0;
                byte[] buffer = new byte[BUFFERSIZE];
                while((readbytes = fileInputStream.read(buffer)) > 0){
                    outputStream.write(buffer,0,readbytes);
                }
            }

            inputStream.close();
            outputStream.close();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void httpMethod(String string){
        // METHOD FILE HTTPVERSION
        string = string.toLowerCase();
        String[] args = string.split(" ");
        if(args[0] == null){
            code = "501";
            fileExist("/501");
        }
        else {
            int r = fileExist(args[1]);
            switch (args[0]) {
                case "get":
                case "post":
                case "head":
                    if (r == 1) {
                        code = "200";
                    } else if (r == 0) {
                        code = "404";
                        fileExist("/404");
                    } else if (r == 2) {
                        code = "301";
                        fileExist("/301moved");
                    }
                    if (args[0].equals("head")) {
                        file = null;
                        content_length = "0";
                    }
                    break;
                case "delete":
                    code = "403";
                    fileExist("/403forbidden");
                    break;
                default:
                    code = "501";
                    fileExist("/501");
                    break;
            }
        }
    }

    private int fileExist(String filepath) { // 0 : not exist 1 : exist 2 : moved
        int result = 0;
        if (filepath.equals("/"))
            filepath = "/index";
        if (filepath.equals("/moved"))
            result = 2;
        else {
            file = new File(".".concat(filepath).concat(".html"));
            content_length = Long.toString(file.length());
            result = file.exists() ? 1 : 0; // java는 boolean int가 호환안되나보다
        }
        return result;
    }
}
