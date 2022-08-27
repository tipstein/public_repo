import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class LinearRamp implements AudioComponent {


    int duration = 2;
    int rateSample = 44100;
    double amp = 3000;
    short value = 0;
    float start = 0;
    float stop = 0;


    public LinearRamp(int start, int stop) {
        this.start = start;
        this.stop = stop;
    }

    public AudioClip getClip() {
        AudioClip sample = new AudioClip();
        for (int i = 0; i < duration * rateSample; i++) {
            value = (short) ((start * (rateSample - i) + stop * i) / rateSample);
            System.out.println("value is " + value);
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

