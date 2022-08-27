import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
NAME            a domain name to which this resource record pertains.

TYPE            two octets containing one of the RR type codes.  This
                field specifies the meaning of the data in the RDATA
                field.

CLASS           two octets which specify the class of the data in the
                RDATA field.

TTL             a 32 bit unsigned integer that specifies the time
                interval (in seconds) that the resource record may be
                cached before it should be discarded.  Zero values are
                interpreted to mean that the RR can only be used for the
                transaction in progress, and should not be cached.

RDLENGTH        an unsigned 16 bit integer that specifies the length in
                octets of the RDATA field.

RDATA           a variable length string of octets that describes the
                resource.  The format of this information varies
                according to the TYPE and CLASS of the resource record.
                For example, the if the TYPE is A and the CLASS is IN,
                the RDATA field is a 4 octet ARPA Internet address.
 **/
public class DNSRecord {

    static String[] NAME;
    static byte[] TYPE;
    static byte[] CLASS;
    static int TTL;
    static int RDLENGTH;
    static String RDATA;
    static Date dateMade;


    static DNSRecord decodeRecord(InputStream is, DNSMessage dnsMessage) throws IOException {

        DNSRecord dnsRecord = new DNSRecord();

        Date();

        is.readNBytes(1);

        NAME = DNSMessage.domainName;//1 byte dnsMessage.readDomainName(is)
        TYPE = is.readNBytes(2);
        CLASS = is.readNBytes(2);

        is.readNBytes(2);

//        for(int i = 0; i<10; i++){
//            int val = (((is.readNBytes(1))[0] & 0xFF));
//            System.out.println(val);
//        }

        byte[] ttl = is.readNBytes(4);
        TTL = 0;
        TTL |= ttl[0] & 0xFF;
        System.out.println(TTL + "  1");
        TTL <<= 8;
        TTL |= ttl[1] & 0xFF;
        System.out.println(TTL+ "  2");
        TTL <<= 8;
        TTL |= ttl[2] & 0xFF;
        System.out.println(TTL+ "  3");
        TTL <<= 8;
        TTL |= ttl[3] & 0xFF;
        System.out.println(TTL+ "  4");



        byte[] length = is.readNBytes(2);
        RDLENGTH=(short)(((length[0] & 0xFF) << 8) | (length[1] & 0xFF));

//        System.out.println(RDLENGTH);

        byte[] IP = is.readNBytes(4);
        RDATA = dnsRecord.convertIpv4AddressToString(IP);
//        System.out.println(RDATA);
//        142.250.69.238


        return dnsRecord;
    }

    void writeBytes(ByteArrayOutputStream byteArrayOutputStream, HashMap<String, Integer>hashMap) throws IOException {

        String domain = "google.com";
        InetAddress ipAddress = InetAddress.getByName(("8.8.8.8"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        DataOutputStream dos = new DataOutputStream(baos);

        // *** Build a DNS Request Frame ****

        //ID
        dos.writeShort(0x1234);

        //Query Flags
        dos.writeShort(0x0100);

        // Question Count
        dos.writeShort(0x0001);

        // Answer Record Count
        dos.writeShort(0x0000);

        // Authority Record Count:
        dos.writeShort(0x0000);

        // Additional Record Count
        dos.writeShort(0x0000);

        String[] domainParts = domain.split("\\.");
        System.out.println(domain + " has " + domainParts.length + " parts");

        for (int i = 0; i < domainParts.length; i++) {
            System.out.println("Writing: " + domainParts[i]);
            byte[] domainBytes = domainParts[i].getBytes("UTF-8");
            dos.writeByte(domainBytes.length);
            dos.write(domainBytes);
        }

        // No more parts
        dos.writeByte(0x00);

    }


    /** return whether the creation date + the time to live is after the current time.
     * The Date and Calendar classes will be useful for this.*/

    @Override
    public String toString() {
        String headerstring = "";

        int rclass=Short.toUnsignedInt((short)(((CLASS[0] & 0xFF) << 8) | (CLASS[1] & 0xFF)));

        String first = "";

//        System.out.println(rclass);

        if(rclass == 0x0001){
            first = "A";
        } else if (rclass == 0x000f) {
            first = "MX";
        } else if (rclass == 0x0002){
            first = "NS";
        }

        headerstring += NAME[0] + "." + NAME[1] + "\t\t" + (TTL) + "\t" + "IN" + "\t" + first + "\t" + RDATA + "\n";

        return headerstring;
    }

    private static void Date() {
        dateMade = new Date();
    }

    boolean timestampValid(){
        return ((DNSRecord.TTL - (DNSRecord.dateMade.getTime() - new Date().getTime()) ) <= 0);
    }


    String convertIpv4AddressToString(byte[] ipv4Address) {

        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < 4; i++) {
            int currentOctet = ipv4Address[i] & 0xFF;

            sb.append(currentOctet);

            if (i != 3) {
                sb.append(".");
            }
        }

        return sb.toString();
    }
}
