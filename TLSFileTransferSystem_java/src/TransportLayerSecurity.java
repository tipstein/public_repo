//Adam Weinstein
//4.4.22
//6014 Networks
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.SocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.*;
import java.security.spec.*;
import java.time.Instant;
import java.util.*;

//Transport class:
//steps to completion:
//1: generate/send: bitlength/nonce/p&g
//2: receives public key
//3: writes client key ->
//4: uses client key + pub key for shared secret
//5: writes HMAC keys ->
//6: checks HMAC against server keys
//7: encryption with HMAC
import com.google.common.primitives.Bytes;

public class TransportLayerSecurity extends Object {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        SecureRandom out = new SecureRandom();
        byte[] nonce = new byte[32];
        out.nextBytes(nonce);

        String n = getHexString(nonce);

        System.out.println("nonce is: " + getHexString(nonce) + "\n\n\n\n");
        int bitLength = 2048;

        //Step 1: start socket
        SocketFactory factory = SocketFactory.getDefault();
        try (Socket socket = factory.createSocket("localhost", 8080)) {


            //step 2: send bitlength
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            os.writeObject(bitLength);
            System.out.println("Client side bitlength: " + bitLength + "\n");
            os.flush();

            //send nonce
            System.out.println("Client side nonce " + nonce + "\n" );
            os.writeObject(n);
            os.flush();

            //step 3
            SecureRandom rnd = new SecureRandom();
            rnd.setSeed(nonce);

            BigInteger p = BigInteger.probablePrime(bitLength, rnd);
            BigInteger g = BigInteger.probablePrime(bitLength/2, rnd);

            System.out.println("client side p: " + p + "\n");
            System.out.println("client side g: " + p + "\n");

           //sending p+g
            os.writeObject(p);
            os.flush();

            os.writeObject(g);
            os.flush();


            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            DHParameterSpec dhSpec = new DHParameterSpec(p, g, bitLength);

            keyGen.initialize(dhSpec);

            X509Certificate client_cert = createSelfSigned(bitLength);

            KeyPair client_key = keyGen.generateKeyPair();

            saveKeyPair("client", client_key, client_cert);

            System.out.println("client side public keypair" + client_key.getPublic() + "\n");

            //step 4: looking for key-
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            DHPublicKey pubKey = (DHPublicKey) is.readObject();

//            System.out.printf("pub: %s%n", getHexString(pubKey.getEncoded()));
//            System.out.printf("mypub: %s%n", getHexString(client_key.getPublic().getEncoded()));
//            System.out.printf("state: %s%n", Objects.equals(getHexString(pubKey.getEncoded()), getHexString(client_key.getPublic().getEncoded())));

            //step 5 send client key to server
            os.writeObject(client_key.getPublic());
            os.flush();

            SecretKey secret_client = combine(client_key.getPrivate(), pubKey);

            BigInteger sharedSecret = new BigInteger(secret_client.getEncoded());

            System.out.println("client side: shared secret key is: " + sharedSecret + "\n");

            //step 6: waiting for server MAC
            HashMap<String, byte[]> keys_client = makeSecretKeys(nonce, secret_client.getEncoded());

            byte[] testKey = keys_client.get("clientMAC");
            System.out.println("results of test key(clientMAC): " + getHexString(testKey));

            byte[] serverMac = (byte[]) is.readObject();
            System.out.println("results of client-side MAC request: " + getHexString(serverMac));

            os.writeObject(testKey);
            os.flush();

            //public static byte[] hmac256(String secretKey,String message)
            // public static byte[] hmac256(byte[] secretKey,byte[] message)
            String msg = "Hello from the client!";
            String encryptMsg = encrypt(msg, keys_client);
            System.out.println("Encrypted message: " + encryptMsg);
            String decryptMsg = decryptMessage(encryptMsg, keys_client);
            System.out.println("Decrypted message: " + decryptMsg);

            os.writeObject(encryptMsg);
            os.flush();


        }
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

    private static SecretKey combine(PrivateKey private1, PublicKey public1) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(private1);
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

    private static String getHexString(byte[] bytes) {
        String result = "";
//        for (int i = 0; i < b.length; i++) {
//            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
//        }
        String st = "";
        for (byte b : bytes) {
            st += String.format("%02X", b);
//            System.out.print(st);
        }
        return st;
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