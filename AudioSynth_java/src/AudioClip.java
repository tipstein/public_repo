import java.util.Arrays;
import java.util.ListResourceBundle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AudioClip {

    public static int duration = 2; //seconds
    public static int bytesPer = 2;
    public static int rateSample = 44100; //samples per second
    public static final int TOTAL_SAMPLES = bytesPer * duration * rateSample;
    byte[] data;

    public AudioClip() {
        data = new byte[bytesPer * duration * rateSample]; // 88200 samples
    }

    public int getSample(int index){
//    data[2*index+1] << 8; // 11111111 + 0000000
//    data[2*index] | (data[2 * index + 1] << 8) // 01101010 | 111111110000000
//
    //((int)data[2*index] & 0xFF) | (((int)data[2*index +1]) << 8);
//    11111111 01101010 | 111011110000000
//    11101111 01101010
       return ((int)data[2*index] & 0xFF) | (((int)data[2*index +1]) << 8);
    }

    public void setSample(int index, int value){
//        index = 2 * index   + 1 -> msb
//        index = 2 * index -> lsb
        data[2 * index]  =  (byte)value;
        data[2 * index + 1] = (byte) (value >> 8 );
    }

    public byte[] getData() {
        byte[] copyData = Arrays.copyOf(data, data.length);
        return copyData;
    }

@Test
    public static void main(String[] args) {
        AudioClip audioClip = new AudioClip();
        audioClip.setSample(2, 32);
        Assertions.assertEquals (32 , audioClip.getSample(2));
        audioClip.setSample(2, -32);
        Assertions.assertEquals (-32 , audioClip.getSample(2));
    audioClip.setSample(2, 0);
    Assertions.assertEquals (0 , audioClip.getSample(2));
    audioClip.setSample(2, -3250);
    Assertions.assertEquals (-3250 , audioClip.getSample(2));
    audioClip.setSample(2, 3500);
    Assertions.assertEquals (3500 , audioClip.getSample(2));




        }
}
