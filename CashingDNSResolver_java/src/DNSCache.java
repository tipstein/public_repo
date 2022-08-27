import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

//build bAIS with datagramData
//ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(datagramData);
//build packet with port 53
//datagramSocket.send(packet); //to google on port 53
//create a new packet to store ->
//datagramSocket.receive(packet);
//decode packet

//convert message to bytes in DNS format (using byteArrayOutputStream)

public class DNSCache {

    /** This class is the local cache. It should basically just have a
     * HashMap<DNSQuestion, DNSRecord> in it. You can just store the first answer
     * for any question in the cache (a response for google.com might return
     * 10 IP addresses, just store the first one). This class should have methods
     * for querying and inserting records into the cache. When you look up an entry,
     * if it is too old (its TTL has expired), remove it and return "not found." */

    static byte[] responseData;
    static HashMap<DNSQuestion, DNSRecord>queryList = new HashMap<>();
    static DNSRecord dnsRecordVALUE;
    static DNSQuestion dnsMessageKEY; //domain name: ex: nyt.com
    static boolean isCached;

    //DNSRecord: Everything after the header and question parts of the DNS message
    //Hashmap should store: HashMap<DNSQuestion, DNSRecord>

    static boolean IsCached(DNSQuestion question){

        if (!queryList.containsKey(question)) {
            isCached = false;
        } else if(queryList.get(question).timestampValid()) {
            queryList.remove(question);
            isCached = false;
        } else {
            isCached = queryList.containsKey(question);
        }
        return isCached;
    }

    static void hashList (DNSQuestion question, DNSRecord record){
        queryList.put (question, record);
    }

    static ArrayList<DNSRecord> pingGoogle (DatagramPacket datagramPacket) throws Exception {
    //take a packet and return DNSMessage
        ArrayList<DNSRecord> records = new ArrayList<>();

        String domainName = (DNSMessage.domainName[0] + "." + DNSMessage.domainName[1]);
        System.out.println("pinging Google and sending packet\n");
        System.out.println("Domanin name to query is: " + domainName);

        //send original packet from DNSServer Class

        //create new packet to store response
        int packetLength = datagramPacket.getLength();
        System.out.println("packet length is: " + packetLength);

        DatagramPacket toGoogle = new DatagramPacket(datagramPacket.getData(), packetLength, InetAddress.getByName("8.8.8.8"),53);

        DatagramSocket googleSocket = new DatagramSocket();

        googleSocket.send(toGoogle);

        System.out.println("receiving packet back from Google");

        byte[]googleBuffer = new byte[4096];

        DatagramPacket googleResponse = new DatagramPacket(googleBuffer, googleBuffer.length);

        googleSocket.receive(googleResponse);

        responseData = googleResponse.getData();

        System.out.println("Google response received.\n\n");

        /** next step: decode message, cache data */

        DNSMessage GMessage = DNSMessage.decodeMessage(responseData);

        records = DNSMessage.dnsRecords;

        return records;
    }
}
