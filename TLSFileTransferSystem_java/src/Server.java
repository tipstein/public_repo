//Adam Weinstein
//4.4.22
//6014 Networks
import com.google.common.primitives.Bytes;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

//Server Class starts server:
//Steps to completion:
//1: bitlength is sent from client
//2: nonce is sent from client
//3: vals p + g are received from client ->
//  -> p + g used with Diffie Helman  DHParameterSpec
//4: certificate signed with bitlength ->
//  -> used to write the server key ->
//  -> server key used to gen public key
//5: public key broadcast to client
//6: server secret key + public key ->
//  ->shared secret -> HMAC -> keys
//7: encryption with HMAC

public class Server {

    static void startServer(int port) throws IOException, InvalidAlgorithmParameterException, InvalidParameterSpecException, NoSuchAlgorithmException, CertificateEncodingException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        Security.addProvider(new BouncyCastleProvider());
        ServerSocketFactory factory = ServerSocketFactory.getDefault();
        try (ServerSocket listener = factory.createServerSocket(port)){
            System.out.println("listening for messages");
            try (Socket socket = listener.accept()) {

                //first step: wait for object
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

                //bitlength comes from step 2
                int bitLength = (int) is.readObject();
                System.out.println("Server side bitlength: " + bitLength + "\n");

                //nonce comes after bitlength
                //receive nonce as hex string
                String n = (String) is.readObject();
                System.out.println("Server side nonce " + n + "\n" );

                //convert nonce to byte[] for keyserver hashmap
                byte[] nonce = hexStringToByteArray(n);
                System.out.println("server side results of nonce conversion " + getHexString(nonce) + "\n");

                //listening for p + g
                BigInteger p = (BigInteger) is.readObject();
                BigInteger g = (BigInteger) is.readObject();

                System.out.println("server side p: " + p + "\n");
                System.out.println("server side g: " + p + "\n");


                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
                DHParameterSpec dhSpec = new DHParameterSpec(p, g, bitLength);

                keyGen.initialize(dhSpec);

                X509Certificate server_cert = createSelfSigned(bitLength);

                KeyPair server_key = keyGen.generateKeyPair();

                saveKeyPair("server", server_key, server_cert);

                //step 4: made key above and sends
                System.out.println("server side public keypair" + server_key.getPublic() + "\n");
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                os.writeObject(server_key.getPublic());
                os.flush();

                DHPublicKey pubKey = (DHPublicKey) is.readObject();

//                System.out.printf("pub: %s%n", getHexString(pubKey.getEncoded()));
//                System.out.printf("mypub: %s%n", getHexString(server_key.getPublic().getEncoded()));
//                System.out.printf("state: %s%n", Objects.equals(getHexString(pubKey.getEncoded()), getHexString(server_key.getPublic().getEncoded())));

                SecretKey secret_server = combine(server_key.getPrivate(), pubKey);

                BigInteger sharedSecret = new BigInteger(secret_server.getEncoded());

                System.out.println("server side: shared secret key is: " + sharedSecret);

                HashMap<String, byte[]> keys_server = makeSecretKeys(nonce, secret_server.getEncoded());

                byte[] testKey = keys_server.get("serverMAC");
                System.out.println("results of test key(serverMAC): " + getHexString(testKey));

                //writes out serverMAC
                os.writeObject(testKey);
                os.flush();

                //receives clientMAC
                byte[] clientMac = (byte[]) is.readObject();
                System.out.println("results of clientMac: " + getHexString(clientMac));
                System.out.println("stored clientMAC is " + getHexString(keys_server.get("clientMAC")) + "\n");
                byte[]storedClientMAC = keys_server.get("clientMAC");

//                boolean retval = Arrays.equals(clientMac, storedClientMAC);
//                System.out.println(retval);

                if (Arrays.equals(clientMac, storedClientMAC) == true ) {
                    System.out.println("SUCCESS!!!!!");
                } else {
                    System.out.println("FAILURE!!!!!");
                }

                String receivedMessage = (String) is.readObject();
                String dMSG = decryptMessage(receivedMessage, keys_server);
                System.out.println("Server decryption report: " + dMSG);


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, InvalidAlgorithmParameterException, CertificateEncodingException, InvalidParameterSpecException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        startServer(8080);
    }

    public static String encrypt (String secretMessage, HashMap<String, byte[]> keyChain) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] keyBytes = keyChain.get("clientMAC");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public static String decryptMessage(String encryptedMessage, HashMap<String, byte[]> keyChain) throws Exception {
        Cipher decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] keyBytes = keyChain.get("clientMAC");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static SecretKey combine(PrivateKey private1, PublicKey public1) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(private1);
//        System.out.println(getHexString(private1.getEncoded()));
//        System.out.println(getHexString(public1.getEncoded()));
        ka.doPhase(public1, true);
        byte[] rawValue = ka.generateSecret();
        SecretKey secretKey = new SecretKeySpec(rawValue, 0, rawValue.length, "DES");
        return secretKey;
    }

    public static void saveKeyPair(String keyName, KeyPair keyPair, X509Certificate cert) throws IOException, CertificateEncodingException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(keyName + "Public.key");
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        fos = new FileOutputStream(keyName + "Private.key");
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();

        // Store Cert.
        byte[] buf = cert.getEncoded();
        fos = new FileOutputStream(keyName + "Cert.crt");
        fos.write(buf);
        fos.close();
    }

    private static String getHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    private static X509Certificate createSelfSigned(int bitLength) throws CertificateEncodingException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(bitLength, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        X509V3CertificateGenerator cert = new X509V3CertificateGenerator();
        cert.setSerialNumber(BigInteger.valueOf(1));   //or generate a random number
        cert.setSubjectDN(new X509Principal("CN=localhost"));  //see examples to add O,OU etc
        cert.setIssuerDN(new X509Principal("CN=localhost")); //same since it is self-signed
        cert.setPublicKey(keyPair.getPublic());
        cert.setNotBefore(Date.from(Instant.now()));
        cert.setNotAfter(Date.from(Instant.now()));
        cert.setSignatureAlgorithm("SHA1WithRSAEncryption");
        PrivateKey signingKey = keyPair.getPrivate();

// finally, sign the certificate with the private key of the same KeyPair
        X509Certificate certificateS = cert.generate(keyPair.getPrivate(), "BC");
        return certificateS;
    }

    private static byte[] HMAC(byte[] input, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HMACSHA256");
        String algorithm  = "HMACSHA256";
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
        mac.init(secretKeySpec);
        byte[] macBytes = mac.doFinal(input);
        return macBytes;
    }

    private static byte[] hkdfExpand(byte[] input, String tag) throws NoSuchAlgorithmException, InvalidKeyException { //tag is a string, but probably convenient to take its contents as byte[]
        byte[] val = new byte[1];
        val[0] = 1;
        byte[] TAG = Bytes.concat(tag.getBytes(StandardCharsets.UTF_8), val);
        byte[] okm = HMAC( input, TAG);
        return Arrays.copyOfRange(okm, 0, 16);
    }

    private static HashMap<String, byte[]> makeSecretKeys(byte[] nonce, byte[] secretKey) throws NoSuchAlgorithmException, InvalidKeyException {


        byte[] prk = HMAC(nonce, secretKey);

        byte[] serverEncrypt = hkdfExpand(prk, "server encrypt");
        System.out.println("key 1: " + getHexString(serverEncrypt) + "\n");

        byte[] clientEncrypt = hkdfExpand(serverEncrypt, "client encrypt");
        System.out.println("key 2: " + getHexString(clientEncrypt) + "\n");

        byte[] serverMAC = hkdfExpand(clientEncrypt, "server MAC");
        System.out.println("key 3: " + getHexString(serverMAC) + "\n");

        byte[] clientMAC = hkdfExpand(serverMAC, "client MAC");
        System.out.println("key 4: " + getHexString(clientMAC) + "\n");

        byte[] serverIV = hkdfExpand(clientMAC, "server IV");
        System.out.println("key 5: " + getHexString(serverIV) + "\n");

        byte[] clientIV = hkdfExpand(serverIV, "client IV");
        System.out.println("key 6: " + getHexString(clientIV) + "\n");

        HashMap<String, byte[]> keys = new HashMap<String, byte[]>();

        keys.put("serverEncrypt", serverEncrypt);
        keys.put("clientEncrypt", clientEncrypt);
        keys.put("serverMAC", serverMAC);
        keys.put("clientMAC", clientMAC);
        keys.put("serverIV", serverIV);
        keys.put("clientIV", clientIV);

        return keys;
    }


}