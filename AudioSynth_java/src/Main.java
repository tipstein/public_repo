import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class Main {

    public static void main(String[] args) throws LineUnavailableException {

        //Application.launch( ACWidget.class );

        // Get properties from the system about samples rates, etc.
// AudioSystem is a class from the Java standard library.
        Clip c = AudioSystem.getClip(); //terrible name, different from our AudioClip class

        // This is the format that we're following, 44.1 KHz mono audio, 16 bits per sample.
        AudioFormat format16 = new AudioFormat(44100, 16, 1, true, false);

//        AudioComponent noise = new WhiteNoise();
//        AudioComponent vol = new Filter(5);
//        vol.connectInput(noise);
//        AudioClip clip = vol.getClip();

//        AudioComponent ramp = new LinearRamp(50, 2000);
//        AudioClip clip = ramp.getClip();

//        AudioComponent note1 = new SineWave(880); // natural A5
//        AudioComponent note2 = new SineWave(523); // natural C5
//        AudioComponent note3 = new SineWave(659); // natural E5
//
//
//        AudioComponent vol = new Filter(2);
//        AudioComponent vol2 = new Filter(2);
//        AudioComponent vol3 = new Filter(2);
//
//        vol.connectInput(note1);
//        vol2.connectInput(note2);
//        vol3.connectInput(note3);
//
//        AudioClip clip = vol.getClip();
//        AudioClip clip2 = vol2.getClip();
//        AudioClip clip3 = vol3.getClip();
//
//        AudioComponent mixer = new Mixer();
//        mixer.connectInput(vol);
//        mixer.connectInput(vol2);

        AudioComponent gen = new SquareWave(440);
        AudioClip clip = gen.getClip();
//
        c.open(format16, clip.getData(), 0, clip.getData().length); // Reads data from our byte array to play it.


        //c.open(format16, mixer.getClip().getData(), 0, clip.getData().length);


        // c.open(format16, clip2.getData(), 0, clip.getData().length); // Reads data from our byte array to play it.

        System.out.println("About to play...");
        c.start(); // Plays it.
        c.loop(1); // Plays it 2 more times if desired, so 6 seconds total

// Makes sure the program doesn't quit before the sound plays.
        while (c.getFramePosition() < AudioClip.TOTAL_SAMPLES || c.isActive() || c.isRunning()) {
            // Do nothing.
        }

        System.out.println("Done.");
        c.close();
    }
}
