public class Filter implements AudioComponent {


    double factorVolume; //how much to incrase/decrease
    AudioClip tempClip;

    Filter(double vol) {
        factorVolume = vol;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {

        tempClip = input.getClip();

    }

    @Override
    public AudioClip getClip() {
        AudioClip volClip = new AudioClip();

        for (int i = 0; i < AudioClip.duration * AudioClip.rateSample; i++) {

            volClip.setSample(i, (int) (factorVolume * (tempClip.getSample(i))));
        }
        return volClip;
    }


}
