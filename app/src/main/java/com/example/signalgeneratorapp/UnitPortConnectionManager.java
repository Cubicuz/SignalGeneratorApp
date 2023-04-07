package com.example.signalgeneratorapp;

import com.example.signalgeneratorapp.signals.Signal;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UnitPortConnectionManager {

    public class UnitInputPortAbstraction{
        public final Signal signal;
        public final UnitInputPort unitInputPort;
        protected UnitInputPortAbstraction(Signal signal, UnitInputPort unitInputPort){
            this.signal = signal;
            this.unitInputPort = unitInputPort;
        }
    }
    private HashMap<Signal, UnitInputPortAbstraction> signalToAbstraction;
    private HashMap<UnitInputPort, UnitInputPortAbstraction> unitInputPortToAbstraction;

    // Any output can be connected to many inputs.
    private HashMap<UnitOutputPort, LinkedList<UnitInputPort>> outputToInputs;
    // Any input can be connected to one output
    private HashMap<UnitInputPort, UnitOutputPort> inputToOutput;

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
        uip.connect(uop);
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
            uip.disconnect(uop);
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

    public LinkedList<UnitInputPort> getConnected(UnitOutputPort uop){
        return outputToInputs.get(uop);
    }

    private UnitPortConnectionManager(){    }
    private static UnitPortConnectionManager instance;
    public static UnitPortConnectionManager getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new UnitPortConnectionManager();
    }
}
