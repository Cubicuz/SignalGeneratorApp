package com.example.signalgeneratorapp.Games;

import com.jsyn.ports.UnitInputPort;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MarbleGame {

    private MarbleGameView view;

    private float positionX, positionY, velocityX, velocityY;
    private float normedPositionX, normedPositionY;
    private float friction = 0.005f;
    private double maximumTilt = 10;
    private float bouncingVelocityLossfactor = -1.0f; // this has to be negative
    private long fieldMax = 1000;
    private long lastMillisTimeStamp;
    private GameThread gameThread;
    private UnitInputPortForSensorGrab unitInputPortForSensorGrabX, unitInputPortForSensorGrabY;

    public MarbleGame(){
        unitInputPortForSensorGrabX = new UnitInputPortForSensorGrab("unitInputPortForSensorGrabX", true);
        unitInputPortForSensorGrabY = new UnitInputPortForSensorGrab("unitInputPortForSensorGrabY", false);
    }

    public void start(){
        gameThread = new GameThread();
        gameThread.start();
    }

    public void stop(){
        running.set(false);
        dontSpinnlock.offer(1);
    }

    public void reset(){
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
        update(0, 0);
    }


    public UnitInputPort getXTiltPort(){ return unitInputPortForSensorGrabX; }
    public UnitInputPort getYTiltPort(){ return unitInputPortForSensorGrabY; }

    public float getX(){ return normedPositionX; }
    public float getY() {return normedPositionY; }

    public void setMarbleGameView(MarbleGameView marbleGameView){
        view = marbleGameView;
    }

    /**
     * tilts go from -1 to 1
     * @param tiltx
     * @param tilty
     */
    protected void update(float tiltx, float tilty){
        long newTimestamp = java.lang.System.currentTimeMillis();
        float timeDelta = (float)(newTimestamp - lastMillisTimeStamp) / 1000;
        lastMillisTimeStamp = newTimestamp;

        velocityX = velocityX + tiltx * timeDelta - velocityX * friction * timeDelta;
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

        velocityY = velocityY + tilty * timeDelta - velocityY * friction * timeDelta;
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
    private TiltUpdate tiltUpdate = new TiltUpdate();
    private AtomicBoolean updateAvailable = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(true);
    private BlockingQueue<Integer> dontSpinnlock = new ArrayBlockingQueue<>(1);
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
                    update(tiltUpdate.tiltX, tiltUpdate.tiltY);

                    updateAvailable.set(false);
                }
            }
        }
    }

    private class UnitInputPortForSensorGrab extends UnitInputPort{

        private boolean isX; // is x? if not it is y
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
