package com.example.signalgeneratorapp.Games.Marble;

import com.example.signalgeneratorapp.SignalManager;
import com.example.signalgeneratorapp.signals.LinearRampSignal;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MarbleGame {

    private MarbleGameView view;

    private float positionX, positionY, velocityX, velocityY;
    private float normedPositionX, normedPositionY;
    private final float friction = 0.010f;
    private final double maximumTilt = 10;
    private final float bouncingVelocityLossfactor = -0.9f; // this has to be negative
    private final long fieldMax = 1000;
    private final float speedFactor = 50f;
    private long lastMillisTimeStamp;
    private float tiltOffsetX, tiltOffsetY;
    private boolean doCalibration;
    private GameThread gameThread;
    private final UnitInputPortForSensorGrab unitInputPortForSensorGrabX, unitInputPortForSensorGrabY;

    private final LinearRampSignal xlow, xhigh, ylow, yhigh;

    public MarbleGame(){
        unitInputPortForSensorGrabX = new UnitInputPortForSensorGrab("unitInputPortForSensorGrabX", true);
        unitInputPortForSensorGrabY = new UnitInputPortForSensorGrab("unitInputPortForSensorGrabY", false);
        xlow = SignalManager.getInstance().addOrGetSignal("marbleXLowOutput", LinearRampSignal::new);
        xhigh = SignalManager.getInstance().addOrGetSignal("marbleXHighOutput", LinearRampSignal::new);
        ylow = SignalManager.getInstance().addOrGetSignal("marbleYLowOutput", LinearRampSignal::new);
        yhigh = SignalManager.getInstance().addOrGetSignal("marbleYHighOutput", LinearRampSignal::new);

        xlow.time().set(0.01);
        xhigh.time().set(0.01);
        ylow.time().set(0.01);
        yhigh.time().set(0.01);
    }

    public void start(){
        gameThread = new GameThread();
        gameThread.start();
    }

    public void stop(){
        running.set(false);
        dontSpinnlock.offer(1);
        xlow.input().set(0.0);
        xhigh.input().set(0.0);
        ylow.input().set(0.0);
        yhigh.input().set(0.0);
    }

    public void reset(){
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
        update(0, 0);
    }
    public void calibrate(){
        doCalibration = true;
    }


    public UnitInputPort getXTiltPort(){ return unitInputPortForSensorGrabX; }
    public UnitInputPort getYTiltPort(){ return unitInputPortForSensorGrabY; }
    public UnitOutputPort getXLow() { return xlow.firstOutputPort(); }
    public UnitOutputPort getXHigh() { return xhigh.firstOutputPort(); }
    public UnitOutputPort getYLow() { return ylow.firstOutputPort(); }
    public UnitOutputPort getYHigh() { return yhigh.firstOutputPort(); }

    public float getX(){ return normedPositionX; }
    public float getY() {return normedPositionY; }

    public void setMarbleGameView(MarbleGameView marbleGameView){
        view = marbleGameView;
    }

    /**
     * tilts go from -1 to 1
     */
    protected void update(float tiltx, float tilty){
        long newTimestamp = java.lang.System.currentTimeMillis();
        float timeDelta = (float)(newTimestamp - lastMillisTimeStamp) / 1000;
        lastMillisTimeStamp = newTimestamp;

        velocityX = velocityX + speedFactor * tiltx * timeDelta - velocityX * friction * timeDelta;
        positionX = positionX + velocityX * timeDelta;
        if (positionX > fieldMax){
            float overshoot = positionX - fieldMax;
            positionX = fieldMax - overshoot;       //bounce from the wall
            velocityX = velocityX * bouncingVelocityLossfactor;
        } else if (positionX < -fieldMax){
            float overshoot = positionX + fieldMax;
            positionX = overshoot - fieldMax;       //bounce from the wall
            velocityX = velocityX * bouncingVelocityLossfactor;
        }

        velocityY = velocityY + speedFactor * tilty * timeDelta - velocityY * friction * timeDelta;
        positionY = positionY + velocityY * timeDelta;
        if (positionY > fieldMax){
            float overshoot = positionY - fieldMax;
            positionY = fieldMax - overshoot;       //bounce from the wall
            velocityY = velocityY * bouncingVelocityLossfactor;
        } else if (positionY < -fieldMax){
            float overshoot = positionY + fieldMax;
            positionY = overshoot - fieldMax;       //bounce from the wall
            velocityY = velocityY * bouncingVelocityLossfactor;
        }
        normedPositionX = positionX / fieldMax;
        normedPositionY = positionY / fieldMax;


        if (normedPositionX > 0){
            xlow.input().set(0.0);
            xhigh.input().set(normedPositionX*normedPositionX);
        } else {
            xlow.input().set(-normedPositionX*normedPositionX);
            xhigh.input().set(0.0);
        }
        if (normedPositionY > 0){
            ylow.input().set(0.0);
            yhigh.input().set(normedPositionY*normedPositionY);
        } else {
            ylow.input().set(-normedPositionY*normedPositionY);
            yhigh.input().set(0.0);
        }

        if (view != null){
            view.update(normedPositionX, normedPositionY);
        }
    }


    private static class TiltUpdate {
        public float tiltX;
        public float tiltY;
        public boolean xUpdated;
        public boolean yUpdated;
    }
    private final TiltUpdate tiltUpdate = new TiltUpdate();
    private final AtomicBoolean updateAvailable = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final BlockingQueue<Integer> dontSpinnlock = new ArrayBlockingQueue<>(1);
    private float calcOffset(float value, float offset){
        if (value - offset > maximumTilt){
            // wrap from positive to negative
            return (float) (value - offset - 2 * maximumTilt);
        } else if (value + offset < -maximumTilt){
            // wrap from negative to positive
            return (float) (value - offset + 2* maximumTilt);
        }
        // default
        return value - offset;
    }
    private class GameThread extends Thread{
        @Override
        public void run() {
            while (running.get()){
                try {
                    dontSpinnlock.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (updateAvailable.get()) {
                    if (doCalibration){
                        tiltOffsetX = tiltUpdate.tiltX;
                        tiltOffsetY = tiltUpdate.tiltY;
                        doCalibration = false;
                    }
                    update(-calcOffset(tiltUpdate.tiltX, tiltOffsetX), calcOffset(tiltUpdate.tiltY, tiltOffsetY));
                    tiltUpdate.xUpdated = false;
                    tiltUpdate.yUpdated = false;
                    updateAvailable.set(false);
                }
            }
        }
    }

    private class UnitInputPortForSensorGrab extends UnitInputPort{

        private final boolean isX; // is x? if not it is y
        public UnitInputPortForSensorGrab(String s, boolean X) {
            super(s);
            isX = X;
        }

        @Override
        public double getMaximum() {
            return maximumTilt;
        }

        @Override
        public double getMinimum() {
            return -maximumTilt;
        }

        @Override
        public void set(double v) {
            if (!updateAvailable.get()){
                if (isX){
                    tiltUpdate.tiltX = (float) v;
                    tiltUpdate.xUpdated = true;
                } else {
                    tiltUpdate.tiltY = (float) v;
                    tiltUpdate.yUpdated = true;
                }
                if (tiltUpdate.xUpdated && tiltUpdate.yUpdated){
                    updateAvailable.set(true);
                    dontSpinnlock.offer(1);
                }
            }
        }
    }

 }
