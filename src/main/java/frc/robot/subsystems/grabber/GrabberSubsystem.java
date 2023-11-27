package frc.robot.subsystems.grabber;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.controller.LinearQuadraticRegulator;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class GrabberSubsystem extends SubsystemBase {

  protected static class Constants {
    protected static final double dtSeconds = 0.020;
    private final static int leftGripperDeviceId = 14;
    private final static int rightGripperDeviceId = 24;
    private final static double gearing = 12;
    private final static double kV = 0.121272;
    private final static double kA = 0.00251016;
    private final static double maxVelocityErrorRadPerSec = 46.3;

    private final static LinearSystem<N1, N1, N1> plant = LinearSystemId.identifyVelocitySystem(kV,kA);
    private final static  Vector<N1> qelms = VecBuilder.fill(maxVelocityErrorRadPerSec);
    private final static Vector<N1> relms = VecBuilder.fill(RobotController.getBatteryVoltage());
    private final static LinearQuadraticRegulator<N1, N1, N1> velocityController = new LinearQuadraticRegulator<>(
      plant, 
      qelms, 
      relms, 
      dtSeconds);
    private final static double kPVelocity = velocityController.getK().get(0, 0);
    private final static double KIVelocity = 0.0;
    private final static  double KDVelocity = 0.0;
  }

  // fields
  private final TrapezoidProfile leftTrapezoidProfile;
  private final TrapezoidProfile rightTrapezoidProfile;
  private final PIDController leftVelocityPIDController;
  private final PIDController rightVelocityPIDController;
  private final SimpleMotorFeedforward leftSimpleMotorFeedforward;
  private final SimpleMotorFeedforward rightSimpleMotorFeedforward;
  private double lastVelocityLeftGripper;
  private double lastVelocityRightGripper;  

  // constructor
  public GrabberSubsystem(double kS) {
    leftSimpleMotorFeedforward = new SimpleMotorFeedforward(kS, Constants.kV, Constants.kA);
    rightSimpleMotorFeedforward = new SimpleMotorFeedforward(kS, Constants.kV, Constants.kA);
    var maxVelocity = leftSimpleMotorFeedforward.maxAchievableVelocity(12, 0);
    var maxAcceleration = leftSimpleMotorFeedforward.maxAchievableAcceleration(12, 0);
    var constraints = new TrapezoidProfile.Constraints(maxVelocity, maxAcceleration);
    leftTrapezoidProfile = new TrapezoidProfile(constraints);
    rightTrapezoidProfile = new TrapezoidProfile(constraints);
    leftVelocityPIDController = new PIDController(Constants.kPVelocity, Constants.KIVelocity, Constants.KDVelocity);
    rightVelocityPIDController =  new PIDController(Constants.kPVelocity, Constants.KIVelocity, Constants.KDVelocity);
    double lastVelocityLeftGripper = 0.0;
    double lastVelocityRightGripper = 0.0;
  }

  // telemetry methods
  public abstract double getLeftGripperVelocityRPM();

  public abstract double getLeftGripperVelocityRadPerSec();

  public abstract double getRightGripperVelocityRPM();

  public abstract double getRightGripperVelocityRadPerSec();

  public double getLeftGripperAccelerationRadPerSecondSquared() {
    double currentVelocity = getLeftGripperVelocityRadPerSec();
    return (currentVelocity - lastVelocityLeftGripper) / 0.020;
  }

  public double getRightGripperAccelerationRadPerSecondSquared() {
    double currentVelocity = getRightGripperVelocityRadPerSec();
    return (currentVelocity - lastVelocityRightGripper) / 0.020;
  }

  // control methods
  public void driveLeftGripperAtVelocity(double rpm) {
    double measurementVelocity = getLeftGripperVelocityRadPerSec();
    double setpoint = Units.rotationsPerMinuteToRadiansPerSecond(rpm);
    State current = new State(measurementVelocity, getLeftGripperAccelerationRadPerSecondSquared());
    State goal = new State(setpoint, 0.0);
    State achievableSetpoint = leftTrapezoidProfile.calculate(Constants.dtSeconds, goal, current);
    double feedbackVoltage = leftSimpleMotorFeedforward.calculate(measurementVelocity, achievableSetpoint.position);

    // TODO: create a double called feedforwardVoltage and get from
    // leftSimpleMotorFeedForward using measurementVelocity,
    // achievableSetpoint.position, and dtSeconds

    double feedforwardVoltage = getleftSimpleMotorFeedforward(measurementVelocity, achievableSetpoint, Constants.dtSeconds);
    
    double voltage = feedforwardVoltage + feedbackVoltage; 
    double leftSimVolts = voltage;

    // TODO: set leftSimVolts to voltage
    // TODO: setInputVoltage on leftGripperSim
  }

  public void driveRightGripperAtVelocity(double rpm) {
    // TODO: same code as driveLeftGripperAtVelocity but for rightGripper
  }

  public abstract void setLeftGripperInputVoltage(double voltage);

  public abstract void setRightGripperInputVoltage(double voltage);

  // command creation methods
  public Command createDriveAtVelocityCommand(double rpm) {
    // TODO: create a Runnable called resetVelocityPIDControllersCommandRunnable and
    // reset both PIDControllers
    // TODO: create a runOnce command called resetVelocityPIDControllersCommand
    // using previous runnable
    // TODO: create a Runnable called driveGrabberAtVelocityCommandRunnable and set
    // left to rpm and right to -rpm
    // TODO: create a run command called driveGrabberAtVelocityCommand using
    // previous runnable
    // TODO: create a runnable called stopGrabberCommandRunnable that sets each
    // Gripper's inputVoltage to 0 and each simVolts to 0
    // TODO: create a Command called commmand set equal to
    // resetVelocityPIDControllesCommand.andThen(driveGrabberAtVelocityCommand).finallyDo(stopGrabberCommandRunnable)
    // TODO: setName of command to "Drive at " + rpm
    // TODO: return command
    return null; // TODO: remove this line when done
  }

  public void setDefaultCommand() {
    // TODO: this.setDefaultCommand(createDriveAtVelocityCommand(0.0))
  }

  @Override
  public void periodic() {
    // TODO: lastVelocityLeftGripper = getLeftGripperVelocityRadPerSec and also for right
  }

  // initSendable handles Dashboard
  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);
    builder.addDoubleProperty("Left Gripper Speed (RPM)", () -> getLeftGripperVelocityRPM(), null);
    builder.addDoubleProperty("Right Gripper Speed (RPM)", () -> getRightGripperVelocityRPM(), null);
    builder.addDoubleProperty("Left Gripper Acceleration (RadPerSecSquared)",
        () -> getLeftGripperAccelerationRadPerSecondSquared(), null);
    builder.addDoubleProperty("Right Gripper Acceleration (RadPerSecSquared)",
        () -> getRightGripperAccelerationRadPerSecondSquared(), null);
  }
}
