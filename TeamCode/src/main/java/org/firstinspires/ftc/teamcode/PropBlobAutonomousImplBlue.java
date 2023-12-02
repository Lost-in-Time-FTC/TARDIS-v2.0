package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

@SuppressWarnings("unused")
@Autonomous(name = "BluePropBlobAutonomousImpl")
public class PropBlobAutonomousImplBlue extends PropBlobAutonomousABC {
    @Override
    public void runOpMode() throws InterruptedException {
        initHardware("BLUE");
        trackTelemetryWhileNotIsStarted();
        waitForStart();

        Hardware hardware = new Hardware(hardwareMap);

        // hardware.armMotor.setPositionPIDFCoefficients(20);
        // hardware.armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Store detected parking position
        PropBlobDetection.PropBlobPosition position = propBlobDetection.getPosition();

        // Pick up preloaded pixels and adjust arm position
        hardware.rightClawServo.setPosition(0);
        hardware.leftClawServo.setPosition(1);
        telemetry.addData("first claw close?", "did it work?");


        if (position == PropBlobDetection.PropBlobPosition.LEFT) {
            telemetry.addData("left", "4324");
            // Go forward
            setAllWheelMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setAllWheelMotorTargetPosition(820);
            setAllWheelMotorPower(0.6);
            setAllWheelMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
            trackAllWheelCurrentPositionTelemetryWhileMotorIsBusy();
            setAllWheelMotorPower(0);
            // Rotate left
            setAllWheelMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rotateLeft(500);
            setAllWheelMotorPower(0.25);
            setAllWheelMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
            trackAllWheelCurrentPositionTelemetryWhileMotorIsBusy();
            setAllWheelMotorPower(0);
            // Go forward
            setAllWheelMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setAllWheelMotorTargetPosition(500);
            setAllWheelMotorPower(0.6);
            setAllWheelMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
            trackAllWheelCurrentPositionTelemetryWhileMotorIsBusy();
            setAllWheelMotorPower(0);
        } else if (position == PropBlobDetection.PropBlobPosition.CENTER) {
            telemetry.addData("right", "4324");
            // Go forward
            setAllWheelMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setAllWheelMotorTargetPosition(870);
            setAllWheelMotorPower(0.6);
            setAllWheelMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
            trackAllWheelCurrentPositionTelemetryWhileMotorIsBusy();
            setAllWheelMotorPower(0);
        } else if (position == PropBlobDetection.PropBlobPosition.RIGHT) {
            telemetry.addData("right", "4324");
            hardware.rightClawServo.setPosition(0);
            // Go forward
            setAllWheelMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setAllWheelMotorTargetPosition(820);
            setAllWheelMotorPower(0.6);
            setAllWheelMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
            trackAllWheelCurrentPositionTelemetryWhileMotorIsBusy();
            setAllWheelMotorPower(0);
            // Rotate right
            setAllWheelMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rotateRight(500);
            setAllWheelMotorPower(0.25);
            setAllWheelMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
            trackAllWheelCurrentPositionTelemetryWhileMotorIsBusy();
            setAllWheelMotorPower(0);
            // Go forward
            setAllWheelMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setAllWheelMotorTargetPosition(500);
            setAllWheelMotorPower(0.6);
            setAllWheelMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
            trackAllWheelCurrentPositionTelemetryWhileMotorIsBusy();
            setAllWheelMotorPower(0);

            hardware.rightClawServo.setPosition(1);
            hardware.leftClawServo.setPosition(0);
            hardware.verticalServo.setPosition(1);
        }
    }
}

