import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class ThreadedHTTPServer {

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
    private static class MyRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println( "Hello from thread " + Thread.currentThread().getId());
        }
    }


   ////////////////////    MAIN    /////////////////////////////

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        int countClient = 0;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (Exception e) {
            System.out.println("Failed to open server socket");
            System.exit(-1);
        }

        while (true) {
            System.out.println("Now listening for messages");
            Socket client = serverSocket.accept();
            countClient ++;
            MyRunnable runner = new MyRunnable();
            Thread thread = new Thread( runner );
            thread.start();

            try {

                System.out.println("Got a new client " + client.toString());

                Scanner in = new Scanner(client.getInputStream());

                String method = "";
                String path = "";
                String protocol = "";

                method = in.next();
                path = in.next();
                protocol = in.next();

                System.out.println("method: " + method);
                System.out.println("fileName: " + path);
                System.out.println("protocol: " + protocol);

                System.out.println("Now time to send response back to client...");

                if (method.equals("GET")) {
                    System.out.println("GET detected");

                    handleRequestGet(client, path);
                } else if (!(method.equals("GET"))) {
                    System.out.println("No request");
                    handleRequestOther(client);
                }

                client.close();
            } catch (Exception e) {
                System.out.println("Error with client's connection...");
            }
        }
    }


}

