package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@SuppressWarnings("unused")
@TeleOp(name = "LiT Drive Program 2022-2023", group = "Linear OpMode")

public class LiTDrive extends LinearOpMode {

    private PIDController controller;

    public static double p = -0.007, i = 0, d = 0;
    public static double f = 0;

    public static int target = 0;

    private PIDController slidecontroller;

    public static double p1 = -0.006, i1 = 0, d1 = 0;
    public static double f1 = 0;

    public static int target2 = 0;

    private final double tickes_in_degree = 700 / 180.0;

    enum ClawToggleTriState {
        FALSE,
        OPEN,
        WIDE_OPEN
    }

    // Declare OpMode members
    private final ElapsedTime runtime = new ElapsedTime();
    boolean clawToggle = false;
    ClawToggleTriState rightClawToggle = ClawToggleTriState.FALSE;
    ClawToggleTriState leftClawToggle = ClawToggleTriState.FALSE;
    private Hardware hardware = null;
    public void runOpMode() {
        Gamepad currentGamepad2 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();

        hardware = new Hardware(hardwareMap);

        hardware.backLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        hardware.backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        hardware.frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        hardware.frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        hardware.armMotor.setDirection(DcMotor.Direction.FORWARD);
        hardware.elevatorMotor.setDirection(DcMotor.Direction.FORWARD);

        controller = new PIDController(p, i, d);
        slidecontroller = new PIDController(p1, i1, d1);

        hardware.armMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        hardware.elevatorMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        hardware.armMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        hardware.elevatorMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();


        // Run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            toggle(currentGamepad2, previousGamepad2);
            claw();
            drive();
            elevator();
            armPivot();
            airplane();
            climb();
            telemetry.addData("slide", hardware.elevatorMotor.getCurrentPosition());
            telemetry.update();

            telemetry.addData("arm", hardware.armMotor.getCurrentPosition());
            telemetry.addData("armtarget", target);
            telemetry.addData("slidetarget", target2);

        }
    }

    // toggling system
    public void toggle(Gamepad currentGamepad2, Gamepad previousGamepad2) {
        try {
            previousGamepad2.copy(currentGamepad2);
            currentGamepad2.copy(gamepad2);
        } catch (Exception e) {
            // :)
        }

        if (currentGamepad2.a && !previousGamepad2.a) {
            clawToggle = !clawToggle;
        }

        if (currentGamepad2.left_bumper && !previousGamepad2.left_bumper) {
            switch (leftClawToggle) {
                case OPEN:
                    leftClawToggle = ClawToggleTriState.WIDE_OPEN;
                    break;
                case WIDE_OPEN:
                    leftClawToggle = ClawToggleTriState.FALSE;
                    break;
                case FALSE:
                    leftClawToggle = ClawToggleTriState.OPEN;
                    break;
            }

//            leftClawToggle = !leftClawToggle;
        }

        if (currentGamepad2.right_bumper && !previousGamepad2.right_bumper) {
            switch (rightClawToggle) {
                case OPEN:
                    rightClawToggle = ClawToggleTriState.WIDE_OPEN;
                    break;
                case WIDE_OPEN:
                    rightClawToggle = ClawToggleTriState.FALSE;
                    break;
                case FALSE:
                    rightClawToggle = ClawToggleTriState.OPEN;
                    break;
            }

//            rightClawToggle = !rightClawToggle;
        }
    }

    // control everything on the claw: pincers + flipping
    public void claw() {

        // variables for fun
        final double LEFT_CLAW_OPEN = 1;
        final double LEFT_CLAW_CLOSE = 0;
        final double RIGHT_CLAW_OPEN = 0;
        final double RIGHT_CLAW_CLOSE = 1;

        // open/close both claws at the same time for fast pickup/dropoff
        if (gamepad2.a) {
            hardware.rightClawServo.setPosition(RIGHT_CLAW_CLOSE);
            hardware.leftClawServo.setPosition(LEFT_CLAW_CLOSE);
        }

        // open/wide-open/close the claws individually for more precise placement
        switch (rightClawToggle) {
            case OPEN:
                hardware.rightClawServo.setPosition(RIGHT_CLAW_OPEN);
                break;
            case WIDE_OPEN:
                hardware.rightClawServo.setPosition(RIGHT_CLAW_CLOSE);
                break;
            case FALSE:
                hardware.rightClawServo.setPosition(0.25);
                break;
        }

        switch (leftClawToggle) {
            case OPEN:
                hardware.leftClawServo.setPosition(LEFT_CLAW_OPEN);
                break;
            case WIDE_OPEN:
                hardware.leftClawServo.setPosition(LEFT_CLAW_CLOSE);
                break;
            case FALSE:
                hardware.leftClawServo.setPosition(LEFT_CLAW_OPEN-0.25);
                break;
        }

        if (gamepad2.left_trigger > 0.25) {
            hardware.verticalServo.setPosition(hardware.verticalServo.getPosition()-0.01);
        }

        if (gamepad2.right_trigger > 0.25) {
            hardware.verticalServo.setPosition(hardware.verticalServo.getPosition()+0.01);
        }
    }

    // plaen shooty thingy
    public void airplane() {
        if (gamepad1.right_bumper) {
            hardware.planeLaunchServo.setPosition(1);
            sleep(2500);
            hardware.planeLaunchServo.setPosition(0);
        }
    }



    // control arm extension
    public void elevator() {

        slidecontroller.setPID(p1, i1, d1);
        int armPos = hardware.elevatorMotor.getCurrentPosition();
        double pid = slidecontroller.calculate(armPos, target2);
        double ff = Math.cos(Math.toRadians(target2 / tickes_in_degree)) * f;

        if (Math.abs(gamepad2.right_stick_y) < .1)
        { double power = pid + ff;
            hardware.elevatorMotor.setPower(power); }

        else {hardware.elevatorMotor.setPower(gamepad2.right_stick_y);
            target2 = hardware.elevatorMotor.getCurrentPosition();}

        final double LEFT_CLAW_OPEN = 1;
        final double LEFT_CLAW_CLOSE = 0;
        final double RIGHT_CLAW_OPEN = 0;
        final double RIGHT_CLAW_CLOSE = 1;

        if (gamepad2.y) {
            target2 = 1000;
            switch (leftClawToggle) {
                case OPEN:
                    leftClawToggle = ClawToggleTriState.WIDE_OPEN;}
            hardware.verticalServo.setPosition(.65);

        }

        if (gamepad2.b) {
            target2 = 0;
        }


    }



    // control arm's circular motion
    public void armPivot() {
        //double armPivotSpeed = 0.85;
        //double armPower = gamepad2.left_stick_y;
        controller.setPID(p, i, d);
        int armPos = hardware.armMotor.getCurrentPosition();
        double pid = controller.calculate(armPos, target);
        double ff = Math.cos(Math.toRadians(target / tickes_in_degree)) * f;

        if (Math.abs(gamepad2.left_stick_y) < .1)
        { double power = pid + ff;
            hardware.armMotor.setPower(power); }

        else {hardware.armMotor.setPower(gamepad2.left_stick_y);
            target = hardware.armMotor.getCurrentPosition();}

        if (gamepad2.y) {
            target = -600;
            switch (rightClawToggle) {
                case OPEN:
                    rightClawToggle = ClawToggleTriState.WIDE_OPEN;}
        }

        if (gamepad2.b) {
            target = 300;

        }


    }



    // TODO: climb initial impl (UNTESTED)
    public void climb() {
        if (gamepad1.dpad_up) {
            hardware.climbMotor.setPower(-1); // retract to climb quickly while dpad up is held
        } else {
            // brake and stay suspended in the air when dpad up is released
            hardware.climbMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            hardware.climbMotor.setPower(0); // TODO: Tweak depending if ZeroPowerBehavior.BRAKE actually works or not
        }

        // bring the robot down by extending slowly
        if (!gamepad1.dpad_up && gamepad1.dpad_down) {
            hardware.climbMotor.setPower(1);
            sleep(3000); // TODO: Test if this timing is enough
            hardware.climbMotor.setPower(0);
        }
    }


    // drive code
    public void drive() {
        // mecanum
        double drive = gamepad1.left_stick_y;
        double turn = -gamepad1.right_stick_x;
        double strafe = -gamepad1.left_stick_x;

        // strafing
        double FL = Range.clip(drive + strafe + turn, -0.5, 0.5);
        double FR = Range.clip(drive - strafe - turn, -0.5, 0.5);
        double BL = Range.clip(drive - strafe + turn, -0.5, 0.5);
        double BR = Range.clip(drive + strafe - turn, -0.5, 0.5);

        double driveSpeed = 1.75;
        double sniperPercent = 0.25;

        // sniper mode = slower and more precise movement
        if (gamepad1.left_trigger > 0) {
            hardware.frontLeftMotor.setPower(FL * driveSpeed * sniperPercent);
            hardware.frontRightMotor.setPower(FR * driveSpeed * sniperPercent);
            hardware.backLeftMotor.setPower(BL * driveSpeed * sniperPercent);
            hardware.backRightMotor.setPower(BR * driveSpeed * sniperPercent);
        }

        // brakes (doesn't really do anything, safety feature)
        else if (gamepad1.right_trigger > 0) {
            hardware.frontLeftMotor.setPower(FL * 0);
            hardware.frontRightMotor.setPower(FR * 0);
            hardware.backLeftMotor.setPower(BL * 0);
            hardware.backRightMotor.setPower(BR * 0);

        }

        // drive normally
        else {
            hardware.frontLeftMotor.setPower(FL * driveSpeed);
            hardware.frontRightMotor.setPower(FR * driveSpeed);
            hardware.backLeftMotor.setPower(BL * driveSpeed);
            hardware.backRightMotor.setPower(BR * driveSpeed);
        }
    }
}