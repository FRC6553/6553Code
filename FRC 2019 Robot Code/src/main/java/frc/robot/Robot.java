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
  public static Spark elevatorMotor;
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
  public double grabberSpeedRatio = 0;
  public double grabberSpeedStep = 0.01;
//!!ControllVariables for Elevator
public static double elevatorMotorSpeed = 1;
public static int elevatorMotorDirection = 1;
public double elevatorMotorSpeedRatio = 0;
public double elevatorMotorSpeedStep = 0.05;

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  

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
    elevatorMotor = new Spark(5);
   // m_oi = new OI();
    m_chooser.setDefaultOption("Default Auto", new ExampleCommand());
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }


  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();
    

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

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

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  //Joystick
    myDrive.arcadeDrive(-driveStick.getY(),driveStick.getX());
    
  //Trigger & Side Button
    if (driveStick.getRawButton(1)&&!driveStick.getRawButton(2)){
      grabberSpeedRatio = grabberSpeedRatio + grabberSpeedStep; 
      if (grabberSpeedRatio >= 1.0){
        grabberSpeedRatio = 1;
      }
    leftWheel.setSpeed(leftGrabberDirection * leftGrabberSpeed * grabberSpeedRatio);
    rightWheel.setSpeed(rightGrabberDirection * rightGrabberSpeed * grabberSpeedRatio);
  }else if (driveStick.getRawButton(2)&&!driveStick.getRawButton(1)){
    leftWheel.setSpeed(-1*leftGrabberDirection * leftGrabberSpeed);
    rightWheel.setSpeed(-1*rightGrabberDirection * rightGrabberSpeed);
  }else{
    grabberSpeedRatio = 0;
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
// //A Button
// if(xbox.getAButton()){
//   rear.set(DoubleSolenoid.Value.kForward);				
// }
// //B Button
// else if(xbox.getBButton()){
//   rear.set(DoubleSolenoid.Value.kReverse);
// }
// //off
// else {
//   rear.set(DoubleSolenoid.Value.kOff);
// }
//xbox axis
if(xbox.getRawButton(5)){
  elevatorMotorSpeedRatio = elevatorMotorSpeedRatio + elevatorMotorSpeedStep; 
  if (elevatorMotorSpeedRatio >= 1.0){
    elevatorMotorSpeedRatio = 1;
  }
  elevatorMotor.setSpeed(elevatorMotorDirection * elevatorMotorSpeed * elevatorMotorSpeedRatio);
}else if(xbox.getRawButton(6)){
  elevatorMotorSpeedRatio = elevatorMotorSpeedRatio + elevatorMotorSpeedStep; 
  if (elevatorMotorSpeedRatio >= 1.0){
    elevatorMotorSpeedRatio = 1;
  }
  elevatorMotor.setSpeed(-elevatorMotorDirection * elevatorMotorSpeed * elevatorMotorSpeedRatio);
}else{
  elevatorMotor.stopMotor();

}
}

  @Override
  public void testPeriodic() {
  }
}

//