// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.commands.tank.DriveTankCommand;
import frc.robot.commands.tank.FollowPathCommand;
import frc.robot.odometry.Odometry;
import frc.robot.commands.elevator.ElevatorUpCommand;
import frc.robot.commands.elevator.ElevatorDownCommand;
import frc.robot.commands.elevator.ElevatorStopCommand;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.tank.TankSubsystem;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // Path of config file, relative to the deploy folder
  private static final String CONFIG_PATH = "config.txt";
  // config file
  private Properties config;

  // Subsystems
  private final TankSubsystem tankSubsystem;
  private final Odometry odometry;

  // Controllers
  private XboxController controlXbox = new XboxController(0);
  private JoystickButton xboxAButton = new JoystickButton(controlXbox, XboxController.Button.kA.value);

  // Commands
  private final FollowPathCommand tankCommand;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   * 
   */
  public RobotContainer() {

    // Load the config file
    this.config = new Properties();

    try {
      FileInputStream stream = new FileInputStream(new File(Filesystem.getDeployDirectory(), CONFIG_PATH));
      config.load(stream);
    } catch (IOException ie) {
      System.out.println("config file not found");
    }

    // Instantiate subsystems
    tankSubsystem = new TankSubsystem();
    odometry = new Odometry();

    // Instantiate commands
    // Drive an S-shaped curve from the origin to 3 meters in front through 2 waypoints
    tankCommand = new FollowPathCommand(
      tankSubsystem, odometry, 
      new Pose2d(), 
      List.of(
        new Translation2d(1, 1), 
        new Translation2d(2, -1)
      ), 
      new Pose2d(3, 0, new Rotation2d())
    );

    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    controllerBindings();
  }

  private void controllerBindings() {
    // Bind A button to zero robot position when pressed
    xboxAButton.whenPressed(new InstantCommand(odometry::zeroPosition));

    Runnable tank = () -> {
      // Set the drive powers based on which controller is being used
      tankSubsystem.setCarDrivePowers(-controlXbox.getY(Hand.kLeft), controlXbox.getX(Hand.kRight));
    };
    tankSubsystem.setDefaultCommand(new RunCommand(tank, tankSubsystem));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return tankCommand;
  }
}
