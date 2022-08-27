import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DNSMessage {

    public static DNSHeader dnsHeader;
    public static String[] domainName; //use arrayList
    public static DNSQuestion dnsQuestion;
    public static ArrayList<DNSRecord> dnsRecords;
    public static Short dnsAuthority;
    public static Short dnsAdditional;
    public static int totalLengthInBytes;
    public static byte[] response;
    public static long queryTime;


    static DNSMessage decodeMessage(byte[] messageInBytes) throws Exception {

        DNSMessage decodedMessage = new DNSMessage();

        InputStream byteArrayInputStream = new ByteArrayInputStream(messageInBytes);

        totalLengthInBytes = messageInBytes.length;

//        System.out.println("the message is " + totalLengthInBytes + " long");

        dnsHeader = DNSHeader.decodeHeader(byteArrayInputStream);

        dnsAuthority = (DNSHeader.authorityCount);

        dnsAdditional = (DNSHeader.additionalCount);

        domainName = decodedMessage.readDomainName(byteArrayInputStream);

        dnsQuestion = DNSQuestion.decodeQuestion(byteArrayInputStream, decodedMessage);

        Date start = new Date();

        dnsRecords = new ArrayList<>();

        if(DNSHeader.answerCount == 0) {
            boolean cached = DNSCache.IsCached(dnsQuestion);
            if (!cached) {
                dnsRecords = (DNSCache.pingGoogle(DNSServer.datagramPacket));
                if (dnsRecords.size() == 0){
                    throw new Exception("no answers given");
                } else {
                    DNSCache.hashList(dnsQuestion, dnsRecords.get(0));
                }
            } else {
                dnsRecords.add(DNSCache.queryList.get(dnsQuestion));
            }
        } else {
            for(int i = 0; i<DNSHeader.answerCount; i++){
                dnsRecords.add(DNSRecord.decodeRecord(byteArrayInputStream,decodedMessage));
            }
        }

        Date end = new Date();
        queryTime = end.getTime() - start.getTime();

        return decodedMessage;
    }

    /**
     * read the pieces of a domain name starting from the current position of the input stream
     */
    String[] readDomainName(InputStream is) throws IOException {

        DataInputStream dataInputStream = new DataInputStream(is);

        short nameServerLength = dataInputStream.readByte();

        String[] domainName = new String[2];

        StringBuilder name = new StringBuilder();

        StringBuilder footer = new StringBuilder();

        for (int i = 0; i < nameServerLength; i++) {
            char c = (char) dataInputStream.readByte();
            name.append(c);
        }

        short footerLength = dataInputStream.readByte();

        for (int i = 0; i < footerLength; i++) {
            char c = (char) dataInputStream.readByte();
            footer.append(c);
        }

        domainName[0] = name.toString();
        domainName[1] = footer.toString();

        return domainName;
    }

    /**
     * used when there's compression and we need to find the
     * domain from earlier in the message. This method should make a
     * ByteArrayInputStream that starts at the specified byte and
     * call the other version of this method
     */
    String[] readDomainName(int firstByte) {

        String[] domainName = new String[2];

        StringBuilder name = new StringBuilder();

        StringBuilder footer = new StringBuilder();

        domainName[0] = name.toString();
        domainName[1] = footer.toString();

        return domainName;

    }

    /**
     * build a response based on the request and the answers you intend to send back.
     */
    static DNSMessage buildResponse(DNSMessage request, ArrayList<DNSRecord> answers) {

        return null;
    }

    /**
     * get the bytes to put in a packet and send back
     */
    byte[] toBytes() throws IOException {
        String domain = domainName[0] + "." + domainName[1];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        DataOutputStream dos = new DataOutputStream(baos);

        // *** Build a DNS Request Frame ****

        //ID
        dos.writeShort(DNSHeader.lineOneIDValue);

        //Query Flags
        dos.writeShort(0x0020);

        // Question Count
        dos.writeShort(DNSHeader.questions);

        // Answer Record Count
        dos.writeShort(DNSHeader.answerCount);

        // Authority Record Count:
        dos.writeShort(DNSHeader.authorityCount);

        // Additional Record Count
        dos.writeShort(DNSHeader.additionalCount);

        String[] domainParts = domain.split("\\.");

        for (int i = 0; i<domainParts.length; i++) {
            byte[] domainBytes = domainParts[i].getBytes("UTF-8");
            dos.writeByte(domainBytes.length);
            dos.write(domainBytes);
        }

        // No more parts
        dos.writeByte(0x00);

        // Type 0x01 = A (Host Request)
        dos.writeShort(0x0001);

        // Class 0x01 = IN
        dos.writeShort(0x0001);

//        dos.writeByte(0x01);
        dos.writeShort(0xC00C);

        // Type 0x01 = A (Host Request)
        dos.writeShort(0x0001);

        // Class 0x01 = IN
        dos.writeShort(0x0001);

        // ttl
//        dos.writeLong(DNSRecord.TTL);
        dos.writeInt((int)(DNSRecord.TTL));

        // length of record
        dos.writeShort(DNSRecord.RDLENGTH);

        // record
        for (int i = 0; i<DNSRecord.RDLENGTH; i++) {
            dos.writeByte(DNSRecord.rdata[i]);
        }

        writeOut = baos.toByteArray();
        totalLengthInBytes = writeOut.length;

        return writeOut;
    }

    /**
     * if this is the first time we've seen this domain name in the packet,
     * write it using the DNS encoding (each segment of the domain prefixed with its length,
     * 0 at the end), and add it to the hash map.
     * Otherwise, write a back pointer to where the domain has been seen previously.
     */
    static void writeDomainName(ByteArrayOutputStream byteArrayOutputStream, HashMap<String,
            Integer> domainLocations, String[] domainPieces) {

    }

    /**
     * join the pieces of a domain name with dots ([ "utah", "edu"] -> "utah.edu" )
     */
    String octetsToString(String[] octetsToString) {
        return null;
    }

    @Override
    public String toString() {
        String domain = domainName[0] + "." + domainName[1];

        StringBuilder records = new StringBuilder();

        long numb = queryTime;

        for(DNSRecord rec : dnsRecords){
            records.append(rec.toString()).append("\n");
        }

        String messageString = "<<>> DiG 9.10.6 <<>> " + domain + " @127.0.0.1 -p 8053\n" +
                "global options: +cmd\n" +
                "Got answer:\n" +
                dnsHeader.toString() + "\n\n" +
                dnsQuestion.toString() + "\n\n" +
                "ANSWER SECTION:\n" +
                records + "\n" +
                "Query time: " + numb + " msec\n" +
                "SERVER: 127.0.0.1#8053(127.0.0.1)\n" +
                "WHEN: " + DNSRecord.dateMade + "\n" +
                "MSG SIZE  rcvd: " + totalLengthInBytes;
        return messageString;
    }
}
