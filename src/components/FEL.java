package components;

import java.util.LinkedList;
import java.util.List;

<<<<<<< HEAD
public class FEL {
    private final List<Event> fel;
    private final FEL instance = new FEL();
    private double simTime;
    
    private FEL() {
        fel = new LinkedList<>();
=======

    public FEL(int nUsers) {
        fel = new double[nUsers + 1];
        size = nUsers + 1;
        fel[nUsers] = 0.01;
>>>>>>> origin/master
    }
<<<<<<< HEAD
    
    public FEL getInstance() {
        return instance;
    }
    
    /* Return the event with minimum timestamp */
    public Event getNextEvent() {
        double min = fel.get(0).getTimestamp();
=======

    public int getNextEvent() {
        double min = fel[0];
>>>>>>> origin/master
        int index = 0;
        for (int i = 1; i < fel.size(); i++) {
            if (fel.get(i).getTimestamp() < min) {
                min = fel.get(i).getTimestamp();
                index = i;
            }
        }
        return fel.get(index);
    }

    public void scheduleNextEvent(int index) {}

    
<<<<<<< HEAD
    /* Add a new event in the FEL */
    public void scheduleNextEvent(Event event) {
        fel.add(event);
    }
    
    public double getSimTime() {
        return simTime;
    }
    
    public void setSimTime(double simTime) {
        this.simTime = simTime;
    }
=======
    /* GETTER E SETTER */
    public double getEventTime(int index) { return fel[index]; }

    public void setEventTime(int index, double time) { this.fel[index] += time; }
>>>>>>> origin/master
}
