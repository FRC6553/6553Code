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
import edu.wpi.first.wpilibj.CameraServer;


public class Robot extends TimedRobot {
  public static ExampleSubsystem m_subsystem = new ExampleSubsystem();
  public static OI m_oi;
  public static Spark grabberLeftWheel;
  public static Spark grabberRightWheel;
  public static Spark GrabberRightReverseWheel;
  public static Spark GrabberLeftReverseWheel;
  public static Joystick driveStick;
  public static DifferentialDrive myDrive;
  public static Spark grabberLeftMotors;
  public static Spark grabberRightMotors;
  public static SpeedControllerGroup leftDrive;
  public static SpeedControllerGroup rightDrive;
  public static Spark elevatorMotor;
  //actuators
  public static XboxController xbox;
  public static Spark fAct;
  //fAct = Front Actuator
  public static Spark rAct;
  //rAct = Rear Actuator

  // public static DoubleSolenoid rear;
  // public static DoubleSolenoid front;
  // public static Compressor air;
//!!controllVariables for grabbers
  public static int leftGrabberDirection = 1;
  public static int rightGrabberDirection = 1;
  public static double leftGrabberSpeed = 0.4;
  public static double rightGrabberSpeed = 0.4;
  public double grabberSpeedRatio = 0;
  public double grabberSpeedStep = 0.4;
//!!ControllVariables for Elevator
public static double elevatorMotorSpeed = 1.0;
public static int elevatorMotorDirection = 1;
public double elevatorMotorSpeedRatio = 0;
public double elevatorMotorSpeedStep = 0.1;
//!!controllVariables for Platform
public double platformMotorSpeed = 1.0;
public double driveMotorSpeed = 1.0;
//!!controll Variables for Actuators
public static double frontActuator = 1.0;
public static int frontActuatorDirection = 1;
public static double rearActuator = 1.0;
public static int rearActuatorDirection = 1;

//PWM spots
public int rightMotorsPWM = 0;
public int leftMotorsPWM = 1;
public int leftWheelPWM = 2;
public int elevatorMotorPWM = 5;

//Xbox buttons
public int leftBumper = 5;
public int rightBumper = 6;

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  

  @Override
  public void robotInit() {
    //robot motor controlls
    grabberLeftWheel = new Spark(2);
    grabberRightWheel = new Spark(3);
    //rightReverseWheel = new Spark(2);
    //leftReverseWheel = new Spark(1);
    driveStick = new Joystick(0);
    grabberLeftMotors=new Spark(1);
		leftDrive =new SpeedControllerGroup(grabberLeftMotors);
		grabberRightMotors=new Spark(0);
    rightDrive=new SpeedControllerGroup(grabberRightMotors);
    myDrive = new DifferentialDrive(grabberLeftMotors, grabberRightMotors);
    //robot pneumatic controlls
    xbox = new XboxController(1);
    fAct = new Spark(7);
    rAct = new Spark(6);

    // rear = new DoubleSolenoid(1,0);
    // front = new DoubleSolenoid(2,3);
    // air = new Compressor(0);
    // air.setClosedLoopControl(false);
    elevatorMotor = new Spark(elevatorMotorPWM);
   // m_oi = new OI();
    m_chooser.setDefaultOption("Default Auto", new ExampleCommand());
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);
    CameraServer.getInstance().startAutomaticCapture();
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
    myDrive.arcadeDrive(driveStick.getY() * driveMotorSpeed,driveStick.getX() * driveMotorSpeed);
    
  //Trigger & Side Button
  //intake cargo
    if (driveStick.getRawButton(1)&&!driveStick.getRawButton(2)){
      grabberSpeedRatio = grabberSpeedRatio + grabberSpeedStep; 
      if (grabberSpeedRatio >= 1.0){
        grabberSpeedRatio = 1;
      }
    grabberLeftWheel.setSpeed(leftGrabberDirection * leftGrabberSpeed * grabberSpeedRatio);
    grabberRightWheel.setSpeed(rightGrabberDirection * rightGrabberSpeed * grabberSpeedRatio);
//shoot out cargo
  }else if (driveStick.getRawButton(2)&&!driveStick.getRawButton(1)){
    grabberLeftWheel.setSpeed(-1 * leftGrabberDirection * leftGrabberSpeed);
    grabberRightWheel.setSpeed(-1 * rightGrabberDirection * rightGrabberSpeed);
  }else{
    grabberSpeedRatio = 0;
   grabberLeftWheel.stopMotor();
   grabberRightWheel.stopMotor();
  }
//Xbox controller (pneumatics)
//Start Button



// if(xbox.getStartButton()&&!xbox.getBackButton()) {
//   air.setClosedLoopControl(true);
// }

//Back Button


// if(xbox.getBackButton()) {
// 	air.setClosedLoopControl(false);
// }

//X Button
//Front Actuator In

if(xbox.getXButton()){
  fAct.setSpeed(-1 * frontActuatorDirection * frontActuator);
}

// if(xbox.getXButton()){
//   rear.set(DoubleSolenoid.Value.kForward);	
//   driveMotorSpeed = 0.5;
// }

//Y Button
//Front Actuator Out

else if(xbox.getYButton()){
  fAct.setSpeed(frontActuatorDirection * frontActuator);
}

else{
  fAct.stopMotor();
}

// else if(xbox.getYButton()){
//   rear.set(DoubleSolenoid.Value.kReverse);
// }

//off



// else {
//   rear.set(DoubleSolenoid.Value.kOff);
//   driveMotorSpeed = 1.0;
// }

//A Button
//Rear Actuator In
if(xbox.getAButton()){
  rAct.setSpeed(-1 * frontActuatorDirection * frontActuator);
}

// if(xbox.getAButton()){
//   front.set(DoubleSolenoid.Value.kForward);
//   platformMotorSpeed = 0.5;				
// }

//B Button
//Rear Actuator Out
else if(xbox.getBButton()){
  rAct.setSpeed(frontActuatorDirection * frontActuator);
}

else{
  rAct.stopMotor();
}


// else if(xbox.getBButton()){
//   front.set(DoubleSolenoid.Value.kReverse);
// }

//off



// else {
//   front.set(DoubleSolenoid.Value.kOff);
//   platformMotorSpeed = 1.0;
// }

//elevator up (Left and Right Bumpers)
if(xbox.getRawButton(leftBumper)){
  elevatorMotorSpeedRatio = elevatorMotorSpeedRatio + elevatorMotorSpeedStep; 
  if (elevatorMotorSpeedRatio >= 1.0){
    elevatorMotorSpeedRatio = 1;
  }
  elevatorMotor.setSpeed(elevatorMotorDirection * elevatorMotorSpeed * elevatorMotorSpeedRatio);
}else if(xbox.getRawButton(rightBumper)){
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