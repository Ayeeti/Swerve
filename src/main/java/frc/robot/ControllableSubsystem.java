package frc.robot;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class ControllableSubsystem extends SubsystemBase {

    private double currentScale;

    public ControllableSubsystem() {

        this.currentScale = 1;
    }

    /**
     * Sets the scale of the amount of current drawn.
     * 
     * @param newScale the new scale
     */
    public void setCurrentScale(double newScale) {
        this.currentScale = newScale;
    }

    /**
     * Returns the scale of the amount of current drawn.
     * 
     * @return the scale
     */
    public double getCurrentScale() {
        return currentScale;
    }

    /**
     * Returns the total amount of current the subsystem is drawing.
     * 
     * @return the total amount of current, in amps
     */
    public abstract double getTotalCurrentDrawn();

}
