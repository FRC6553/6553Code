/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Compressor;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public static ExampleSubsystem m_subsystem = new ExampleSubsystem();
  public static OI m_oi;
  public static Spark leftWheel;
  public static Spark rightWheel;
  public static Spark rightReverseWheel;
  public static Spark leftReverseWheel;
  public static Joystick driveStick;
  public static DifferentialDrive myDrive;
  public static Spark leftMotors;
  public static Spark rightMotors;
  public static SpeedControllerGroup leftDrive;
	public static SpeedControllerGroup rightDrive;
  //pneumatics
  public static XboxController xbox;
  public static DoubleSolenoid front;
  public static DoubleSolenoid rear;
  public static Compressor air;
//!!controllVariables for grabbers
  public static int leftGrabberDirection = 1;
  public static int rightGrabberDirection = 1;
  public static double leftGrabberSpeed = 0.4;
  public static double rightGrabberSpeed = 0.4;
  public double grabberAcceleration = 0;
   

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    //robot motor controlls
    leftWheel = new Spark(2);
    rightWheel = new Spark(3);
    //rightReverseWheel = new Spark(2);
    //leftReverseWheel = new Spark(1);
    driveStick = new Joystick(0);
    leftMotors=new Spark(1);
		leftDrive =new SpeedControllerGroup(leftMotors);
		rightMotors=new Spark(0);
    rightDrive=new SpeedControllerGroup(rightMotors);
    myDrive = new DifferentialDrive(leftMotors, rightMotors);
    //robot pneumatic controlls
    xbox = new XboxController(1);
    front = new DoubleSolenoid(1,0);
    air = new Compressor(0);
    air.setClosedLoopControl(false);
   // m_oi = new OI();
    m_chooser.setDefaultOption("Default Auto", new ExampleCommand());
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();
    
    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  //Joystick
    myDrive.arcadeDrive(-driveStick.getY(),driveStick.getX());

  //Trigger & Side Button
    if (driveStick.getRawButton(1)&&!driveStick.getRawButton(2)){
      grabberAcceleration = grabberAcceleration + 0.01; 
      if (grabberAcceleration >= 1.0){
        grabberAcceleration = 1;
      }
    leftWheel.setSpeed(leftGrabberDirection*leftGrabberSpeed*grabberAcceleration);
    rightWheel.setSpeed(rightGrabberDirection*rightGrabberSpeed*grabberAcceleration);
  }else if (driveStick.getRawButton(2)&&!driveStick.getRawButton(1)){
    leftWheel.setSpeed(-1*leftGrabberDirection*leftGrabberSpeed);
    rightWheel.setSpeed(-1*rightGrabberDirection*rightGrabberSpeed);
  }else{
    grabberAcceleration = 0;
   leftWheel.stopMotor();
   rightWheel.stopMotor();
  }
//Xbox controller (pneumatics)
//Start Button
if(xbox.getStartButton()&&!xbox.getBackButton()) {
  air.setClosedLoopControl(true);
}
//Back Button
if(xbox.getBackButton()) {
	air.setClosedLoopControl(false);
}
//X Button
if(xbox.getXButton()){
  front.set(DoubleSolenoid.Value.kForward);				
}
//Y Button
else if(xbox.getYButton()){
  front.set(DoubleSolenoid.Value.kReverse);
}
//off
else {
  front.set(DoubleSolenoid.Value.kOff);
}
//A Button
if(xbox.getAButton()){
  rear.set(DoubleSolenoid.Value.kForward);				
}
//B Button
else if(xbox.getBButton()){
  rear.set(DoubleSolenoid.Value.kReverse);
}
//off
else {
  rear.set(DoubleSolenoid.Value.kOff);
}

}
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}

//