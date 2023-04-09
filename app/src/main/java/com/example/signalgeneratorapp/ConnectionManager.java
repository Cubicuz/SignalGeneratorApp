package com.example.signalgeneratorapp;

import com.example.signalgeneratorapp.signals.Signal;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConnectionManager {


    // Any output can be connected to many inputs.
    private HashMap<UnitOutputPort, LinkedList<UnitInputPort>> outputToInputs = new HashMap<>();
    // Any input can be connected to one output
    private HashMap<UnitInputPort, UnitOutputPort> inputToOutput = new HashMap<>();

    public void connect(UnitInputPort uip, UnitOutputPort uop){
        if (inputToOutput.containsKey(uip)){
            // a connection for this input exists already
            if (uop == inputToOutput.get(uip)){
                // this connection is already made
                return;
            }
            // remove the existing connection
            disconnect(uip);
        }

        // add new connection
        uop.connect(uip);
        inputToOutput.put(uip, uop);
        if (outputToInputs.containsKey(uop)){
            outputToInputs.get(uop).add(uip);
        } else {
            outputToInputs.put(uop, new LinkedList<>(List.of(uip)));
        }
    }


    public void disconnect(UnitInputPort uip){
        if (inputToOutput.containsKey(uip)){
            // a connection for this input exists
            UnitOutputPort uop = inputToOutput.get(uip);

            // remove the existing connection
            uop.disconnect(uip);
            outputToInputs.get(uop).remove(uip);
            if (outputToInputs.get(uop).isEmpty()){
                outputToInputs.remove(uop);
            }
            inputToOutput.remove(uip);
        }

    }

    public void disconnect(UnitOutputPort uop){
        if (outputToInputs.containsKey(uop)){
            // make copy of inputlist
            LinkedList<UnitInputPort> inputs = new LinkedList<>(outputToInputs.get(uop));
            inputs.forEach(unitInputPort -> disconnect(unitInputPort));
        }
    }

    public void disconnect(Signal signal){
        signal.inputsPorts().forEach(unitInputPort -> disconnect(unitInputPort));
        signal.outputsPorts().forEach(unitOutputPort -> disconnect(unitOutputPort));
    }

    public UnitOutputPort getConnected(UnitInputPort uip){
        return inputToOutput.get(uip);
    }

    // lineout interface
    private LinkedList<HashSet<UnitOutputPort>> lineoutConnections = new LinkedList<>();
    public void connectLineout(UnitOutputPort uop, int leftRight){
        if (lineoutConnections.get(leftRight).contains(uop)){
            // connection already there
            return;
        }
        uop.connect(0, SignalManager.getInstance().lineout(), leftRight);
        lineoutConnections.get(leftRight).add(uop);
    }

    public boolean isLineoutConnected(UnitOutputPort uop, int leftRight){
        return lineoutConnections.get(leftRight).contains(uop);
    }

    public void disconnectLineout(UnitOutputPort uop, int leftRight){
        if (lineoutConnections.get(leftRight).contains(uop)){
            uop.disconnect(0, SignalManager.getInstance().lineout(), leftRight);
            lineoutConnections.get(leftRight).remove(uop);
        }
    }


    public LinkedList<UnitInputPort> getConnected(UnitOutputPort uop){
        return outputToInputs.get(uop);
    }

    private ConnectionManager(){
        lineoutConnections.add(new HashSet<>());
        lineoutConnections.add(new HashSet<>());
    }
    private static ConnectionManager instance;
    public static ConnectionManager getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new ConnectionManager();
    }
}
