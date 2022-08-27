import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class SquareWave implements AudioComponent {
    int duration = 1;
    int rateSample = 44100;
    int frequency = 0;


    SquareWave(int frequency) {
        this.frequency = frequency;
    }

    public AudioClip getClip() {
        AudioClip sample = new AudioClip();
        short value = 0;
        for (int i = 0; i < duration * rateSample; i++) {
            value = (short) ((frequency * i / rateSample) % 1);
            System.out.println("value to check is: " + value);
//            if ((frequency * i / rateSample) % 1 > 0.5) {
//                value = (short) maxValue;
//            } else {
//                value = (short) -maxValue;

            sample.setSample(i, value);
        }
        return sample;
    }

    public boolean hasInput() {
        return false;
    }

    public void connectInput(AudioComponent input) {

    }

}
