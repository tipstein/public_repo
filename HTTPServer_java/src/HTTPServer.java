import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;


// Open a channel and start listening
// Take in a new client
//


class HTTPServer {
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (Exception e) {
            System.out.println("Failed to open server socket");
            System.exit(-1);
        }

        while (true) {
            System.out.println("Now listening for messages");
            Socket client = serverSocket.accept();

            try {

                System.out.println("Got a new client " + client.toString());

                InputStreamReader isr = new InputStreamReader(client.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder request = new StringBuilder();
                String line = br.readLine();
                ArrayList<String> httpRequest = new ArrayList<String>();
                HashMap<String, String> requestHeaders = new HashMap<String, String>();

                while (!(line.isBlank())) {
                    request.append(line + "\r\n");
                    httpRequest.add(line + "\r\n");
                    line = br.readLine();
                    ///use a delimiter split here to create the headers and store in a hash map
                    if (line.contains(":")) {
                        String[] stringarray = line.split(": ");
                        String keyVal = stringarray[0]; //key
                        String keyPath = stringarray[1]; //value
                        requestHeaders.put(keyVal, keyPath);
                    }
                }
                System.out.println(request);
                String headerOne = httpRequest.get(0);
                String[] stringarray = headerOne.split(" ");
                String method = stringarray[0];
                String path = stringarray[1];
                String version = stringarray[2];

                if (requestHeaders.get("Connection").contains("Upgrade")) {
                    System.out.println("Initiating handshake");
                    String userKey = requestHeaders.get("Sec-WebSocket-Key");
                    userKey.trim();
                    System.out.println("User Key is__" + userKey + "__");
                    handShake(userKey, client, path);
                }

            } catch (Exception e) {
                System.out.println("Error with client's connection...");
            }
        }
    }

    private static void handShake(String userKey, Socket client, String path) throws IOException {
        // create user with magic string
        String wsKey = userKey;
        Socket socket_ = client;
        String path_ = path;
        //long threadID_ = threadID;

        System.out.println("user is:__" + wsKey + "__");

        String given = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
//this is what is given to you from the WebSocket Protocol website page

        wsKey += given;
        MessageDigest myMessageDigest = null;
        try {
            myMessageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        byte[] digestedByteArray = myMessageDigest.digest(wsKey.getBytes());

        String result = Base64.getEncoder().encodeToString(digestedByteArray);

        String responseHeader = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Connection: Upgrade\r\n" +
                "Upgrade: websocket\r\n" +
                "Sec-WebSocket-Accept: " + result + "\r\n" +
                "Sec-Websocket-Protocol: chat\r\n"
                + "\r\n";

        System.out.println("Flushing before writing response header\n");

        System.out.println("Here is the Response Header\n" + responseHeader);
        //Thread.sleep (5000);
        OutputStream os = socket_.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write(responseHeader);
        pw.flush();


    }

    private static void handleRequestOther(Socket client) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        PrintWriter pw = new PrintWriter(clientOutput);
        pw.println("HTTP/1.1 300 FAIL");
        pw.println();


    }

    private static void handleRequestGet(Socket client, String path) throws IOException {
        System.out.println("now in get request handler");

        OutputStream clientOutput = client.getOutputStream();
        PrintWriter pw = new PrintWriter(clientOutput);
        pw.println("HTTP/1.1 200 OK");
        pw.println("ContentType: text/html");
        pw.println();
        String path_ = path;


        System.out.println("File path is: " + path_);

        if (path_.equals("/")) {
            path_ = "resources/index.html";
        } else if (!(path_.equals("/"))) {
            System.out.println("Actually I'm looking for: " + path_);
            path_ = "resources" + path_;
        }

        try {
            System.out.println("filepath is: " + path_);

            Scanner scanner = new Scanner(new File(path_));
            String htmlString = scanner.useDelimiter("\\Z").next();
            scanner.close();
            pw.println(htmlString);
            System.out.println("html string" + htmlString);

            pw.flush();
            pw.close();


        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.toString() + "No!");
            Scanner scanner = new Scanner(new File("resources/ErrorHTML.html"));
            String htmlString = scanner.useDelimiter("\\Z").next();
            scanner.close();
            pw.println(htmlString);
            System.out.println("html string" + htmlString);

            pw.flush();
            pw.close();
        }

    }
}
