import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Objects;

public class DNSQuestion {

    public static String[] QNAME;
    public static byte[] QTYPE;
    public static byte[] QCLASS;


    /** read a question from the input stream. Due to compression,
     * you may have to ask the DNSMessage containing this question to read some of the fields. */
    static DNSQuestion decodeQuestion(InputStream is , DNSMessage dnsMessage) throws IOException {

        DNSQuestion dnsQuestion = new DNSQuestion();
        QNAME = DNSMessage.domainName;
        QTYPE = is.readNBytes(2);
        QCLASS = is.readNBytes(2);

        return dnsQuestion;
    }

    /**  Write the question bytes which will be sent to the client.
     * The hash map is used for us to compress the message, see the DNSMessage class below. */
    void writeBytes(ByteArrayOutputStream byteArrayOutputStream,
                    HashMap<String,Integer> domainNameLocations) throws IOException {

    }

    @Override
    public String toString() {
        String headerstring = "";

        int qclass=Short.toUnsignedInt((short)(((QCLASS[1] & 0xFF) << 8) | (QCLASS[0] & 0xFF)));

        String first = "";

        if(qclass == 0x0001){
            first = "A";
        } else if (qclass == 0x000f) {
            first = "MX";
        } else if (qclass == 0x0002){
            first = "NS";
        }

        headerstring += "QUESTION SECTION:\n";
        headerstring += QNAME[0] + "." + QNAME[1] + "\t\t\t" + "IN" + "\t" + first + "\n";

        return headerstring;
    }

    /** */
    @Override
    public int hashCode() {
        int val = 0;
        for (String s : this.QNAME) {
            val += s.length();
        }

        return val;
    }

    @Override
    public boolean equals(Object o) {
        return (Objects.equals(this.toString(), o.toString()));
    }
}
