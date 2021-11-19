package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.subsystems.tank.TankSubsystem;

public class PowerController {

    private static PowerDistributionPanel PDP = new PowerDistributionPanel(0);

    private TankSubsystem tankSubsystem;

    private List<ControllableSubsystem> subsystems;

    public PowerController(TankSubsystem tankSubsystem) {
        subsystems = new ArrayList<ControllableSubsystem>();

        subsystems.add(tankSubsystem);

        this.tankSubsystem = tankSubsystem;
    }

    public void check() {

        System.out.println("Checking for a brownout...");
        // Check PDP voltage; if close to a brownout:
        // if (PDP.getVoltage() < 7) {

        // TODO Calculate total sustained current
        double totalSustainedCurrent = 54;

        // Sum up total current drawn from all subsystems
        double totalCurrent = 0;
        double nonTankCurrent = 0;
        for (ControllableSubsystem subsystem : subsystems) {

            totalCurrent += subsystem.getTotalCurrentDrawn();

            if (subsystem instanceof TankSubsystem) {
                nonTankCurrent += subsystem.getTotalCurrentDrawn();
            }
        }

        System.out.print("total current drawn: " + totalCurrent + "; non tank current drawn: " + nonTankCurrent);

        // Set scale of the current drawn by TankSubsystem
        if (totalCurrent > totalSustainedCurrent) {
            // New scale = current available after other components draw divided by current
            // drawn by tank
            tankSubsystem.setCurrentScale((totalCurrent - nonTankCurrent) / tankSubsystem.getTotalCurrentDrawn());

            System.out.println("New scale for tank subsystem: " + tankSubsystem.getCurrentScale());
        }

        // }
    }

    public static double getCurrentDrawnFromPDP(int... PDPChannel) {
        double sum = 0;

        for (int channel : PDPChannel) {
            sum += PDP.getCurrent(channel);
        }

        return sum;
    }
}
