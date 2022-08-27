import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class SineWave implements AudioComponent {

    int duration = 1;
    int rateSample = 44100;
    int frequency = 0;
    double amp = 3000;

    SineWave(int frequency) {
        this.frequency = frequency;
    }

    public AudioClip getClip() {
        AudioClip sample = new AudioClip();
        for (int i = 0; i < 2 * duration * rateSample; i++) {
            int value = (short) (amp * sin(2 * PI * frequency * i / rateSample));
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

