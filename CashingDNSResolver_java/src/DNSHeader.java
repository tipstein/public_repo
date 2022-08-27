import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;

public class DNSHeader {

    public static int qrCode;
    public static int opCode;
    public static short questions;
    public static short answerCount;
    public static short authorityCount;
    public static short additionalCount;
    public static byte[]lineOneIDValue;
//    public static String[] QNAME;
//    public static byte[] QTYPE;
//    public static byte[] QCLASS;

    /** read the header from an input stream (we'll use a ByteArrayInputStream but
     * we will only use the basic read methods of input stream to read 1 byte,
     * or to fill in a byte array, so we'll be generic).*/
    static DNSHeader decodeHeader(InputStream is) throws IOException {

        lineOneIDValue = is.readNBytes(2);   //2 bytes for DNS ID

        byte[]lineTwoHeader = is.readNBytes(2);    //2 bytes for DNS Header

        DNSHeader dnsHeader = new DNSHeader();

        qrCode = lineTwoHeader[0] & 0x80; //possibly store in boolean  // 0x 1000 0000; //mask byte for single bit = QRCode -> needs to be base 2 (convert binary to hex)

                                  //0111 1000  // 0x78
        opCode = lineTwoHeader[0] & 0x78; //01; //mask first byte for bits 1-3 (4 bits) for OPCode

        //inputstream is now at line three: read # of questions (2 byte int)

        DataInputStream dataInputStream = new DataInputStream (is);

        questions = dataInputStream.readShort(); //two bytes: number of ?'s

        answerCount = dataInputStream.readShort(); //two bytes: number of answers

        authorityCount = dataInputStream.readShort(); //two bytes: authority

        additionalCount = dataInputStream.readShort(); //two bytes: additional info

        return dnsHeader;
    }

    /** This will create the header for the response. It will copy some fields from the request */
    static DNSHeader buildResponseHeader(DNSMessage request, DNSMessage response) {

        DNSHeader dnsHeader = new DNSHeader();

        return dnsHeader;
    }

    /** encode the header to bytes to be sent back to the client.
     * The OutputStream interface has methods to write a single byte or an array of bytes. */
    void writeBytes(OutputStream outputStream) {


    }


    /** */
    @Override
    public String toString() {

        String headerstring = "";
        String opcode = "";
        String status = "";

        if(opCode == 0){
            opcode = "QUERY";
        } else {
            opcode = "ANSWER";
        }
        if(qrCode == 0){
            status = "NOERROR";
        } else {
            status = "ERROR";
        }

        int val=Short.toUnsignedInt((short)(((lineOneIDValue[0] & 0xFF) << 8) | (lineOneIDValue[1] & 0xFF)));

        headerstring += "->>HEADER<<- opcode: " + opcode + ", status: " + status + ", id: " + val + "\n";

        headerstring += "flags: rd ad;  QUERY: " + questions + ", ANSWER: " + answerCount + ", AUTHORITY: " + authorityCount + ", ADDITIONAL: " + additionalCount;
        //in flags above, responses may be: AA AuthoritativeAnswer; TC Truncation; RD Recursion Desired; RA Recursion Available
        //no flags are used in my response

        return headerstring;

    }

}
