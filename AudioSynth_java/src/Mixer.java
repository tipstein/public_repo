import java.util.ArrayList;

public class Mixer implements AudioComponent {

    ArrayList<AudioComponent> allInputs = new ArrayList<>();

    double factorVolume; //how much to incrase/decrease
    AudioClip tempClip;


    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {

        allInputs.add(input);
    }

    @Override
    public AudioClip getClip() {
        AudioClip mixerClip = new AudioClip();

        for (int x = 0; x < allInputs.size(); x++) {
            short mLow = 0;
            short mHigh = 0;
            short iLow = 0;
            short iHigh = 0;
            AudioClip checkClip = allInputs.get(x).getClip();

            for (int i = 0; i < AudioClip.duration * AudioClip.rateSample; i++) {

                int mixerValue = mixerClip.getSample(i);
                if (mixerClip.getSample(i) > mHigh) {
                    mHigh = (short) mixerClip.getSample(i);
                }
                if (mixerClip.getSample(i) < mLow) {
                    mLow = (short) mixerClip.getSample(i);
                }

                int inputValue = checkClip.getSample(i);
                if (checkClip.getSample(i) > iHigh) {
                    iHigh = (short) mixerClip.getSample(i);
                }
                if (checkClip.getSample(i) < iLow) {
                    iLow = (short) mixerClip.getSample(i);
                }
                short percentageOver = 0;
                short percentageUnder = 0;

                if (mHigh + iHigh > Short.MAX_VALUE) {
                    System.out.println("percentage over is: " + percentageOver);
                    System.out.println("high value is " + mHigh + iHigh);
                    percentageOver = (short) ((mHigh + iHigh) / Short.MAX_VALUE);
                }


//                System.out.println(allInputs.get(x).getClip().getSample(i));

                int finalValue = inputValue + mixerValue;
                if (finalValue >= Short.MAX_VALUE) {
                    finalValue = Short.MAX_VALUE;
                } else if (finalValue <= Short.MIN_VALUE) {
                    finalValue = Short.MIN_VALUE;
                }
                //System.out.println("finalValue: " + finalValue);
                mixerClip.setSample(i, finalValue);
            }
        }

        return mixerClip;
    }


}

