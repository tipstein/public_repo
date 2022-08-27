import javax.sound.sampled.Port;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.HashMap;

public class DNSServer {

    static HashMap<String, byte[]> queryList;
    static DatagramPacket datagramPacket;

    public static void main(String[] args) throws Exception {

        final int LOCAL_PORT = 8053;  //8053: local port

        DatagramSocket datagramSocket = new DatagramSocket(LOCAL_PORT);

        //step 1: open a socket//
        System.out.println("Created UDP  server socket at " + datagramSocket.getLocalSocketAddress() + "...");
        while (true) {
            System.out.println("\n\n\n\nWaiting for a  UDP  packet...");

            byte[] datagramData;

            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                //params:
                //buf - buffer for holding the incoming datagram.
                //length - the number of bytes to read.

            datagramPacket = packet;

            datagramSocket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            DNSMessage dnsMessage = DNSMessage.decodeMessage(datagramData);

            System.out.println(dnsMessage);
            datagramSocket.send(packet);
        }
    }
}
