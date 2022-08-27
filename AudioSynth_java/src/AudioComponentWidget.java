import java.awt.*;

public class AudioComponentWidget extends Pane {

    private AnchorPane parent_;
    private HBox baseLayout_ = new HBox();
    private AudioComponent audioComp_ = null;
    private String name_ = "Name not initialize";

    AudioComponent(AudioComponent ac, AnchorPane parent, String componentName) {
        VBox rightSide = new VBox();
        Button closeBTn -new Button("x");
        Circle outputCircle = new Circle(10);
        outputCircle.setFill(Color.BLUE);

        rightSide.getChildren().add(closeBTn);
        rightSide.getChildren().add(outputCircle);

        baseLayout.getChildren().add(rightSide);

        this.getChildren().add(baseLayout_);
        parent.getChildren().add(this);

    }
}
