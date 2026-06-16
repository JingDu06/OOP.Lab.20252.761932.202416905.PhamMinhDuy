package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.trafficlight.TrafficLight;
import util.SoundManager;

public class TrafficController {

    private final List<TrafficLight> trafficLights;
    private final SoundManager soundManager;
    private LightControlMode controlMode;

    public TrafficController() {
        trafficLights = new ArrayList<>();
        soundManager = new SoundManager();
        controlMode = LightControlMode.AUTOMATIC;
    }

    
    public void addTrafficLight(TrafficLight light) {
        if (light != null) {
            trafficLights.add(light);
        }
    }

    public void removeTrafficLight(TrafficLight light) {
        trafficLights.remove(light);
    }

    public List<TrafficLight> getTrafficLights() {
        return Collections.unmodifiableList(trafficLights);
    }

    public void updateLights() {
        if (controlMode == LightControlMode.MANUAL) {
            return;
        }

        for (TrafficLight light : trafficLights) {
            TrafficLight.LightState before = light.getCurrentState();
            light.update();
            if (before != light.getCurrentState()) {
                soundManager.playTrafficLightChange();
            }
        }
    }

    public void switchLight(TrafficLight light) {
        if (light != null) {
            light.switchState();
            soundManager.playTrafficLightChange();
        }
    }

    public LightControlMode getControlMode() {
        return controlMode;
    }

    public void setControlMode(LightControlMode controlMode) {
        this.controlMode = controlMode == null ? LightControlMode.AUTOMATIC : controlMode;
    }
}