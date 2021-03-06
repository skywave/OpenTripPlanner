package org.opentripplanner.api.ws;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

/** 
 * Holds information to be included in the REST Response for debugging and profiling purposes.
 * 
 * startedCalculating is called in the routingContext constructor.
 * finishedCalculating and finishedRendering are all called in PlanGenerator.generate().
 * finishedPrecalculating and foundPaths are called in the SPTService implementations.
 */
@XmlRootElement
public class Debug { // rename to DebugOutput

    /* Only public fields are serialized by JAX-RS, make interal ones private? */
    private long startedCalculating;
    private long finishedPrecalculating;
    private List<Long> foundPaths = Lists.newArrayList();    
    private long finishedCalculating;    
    private long finishedRendering;    
    
    /* Results, public to cause JAX-RS serialization */
    public long precalculationTime;
    public long pathCalculationTime;
    public List<Long> pathTimes = Lists.newArrayList();
    public long renderingTime;
    public long totalTime;
    public boolean timedOut;
    
    public Debug () { }
    
    /** 
     * Record the time when we first began calculating a path for this request 
     * (before any heuristic pre-calculation). Note that timings will not 
     * include network and server request queue overhead, which is what we want.
     */
    public void startedCalculating() {
        startedCalculating = System.currentTimeMillis();
    }

    /** Record the time when we finished heuristic pre-calculation. */
    public void finishedPrecalculating() {
        finishedPrecalculating = System.currentTimeMillis();
    }

    /** Record the time when a path was found. */
    public void foundPath() {
        foundPaths.add(System.currentTimeMillis());
    }

    /** Record the time when we finished calculating paths for this request. */
    public void finishedCalculating() {
        finishedCalculating = System.currentTimeMillis();
    }
    
    /** Record the time when we finished converting paths into itineraries. */
    public void finishedRendering() {
        finishedRendering= System.currentTimeMillis();
        computeSummary();
    }
    
    /** Summarize and calculate elapsed times. */
    private void computeSummary() {
        precalculationTime = finishedPrecalculating - startedCalculating;
        pathCalculationTime = finishedCalculating - finishedPrecalculating;
        long last_t = finishedPrecalculating;
        for (long t : foundPaths) {
            pathTimes.add(t - last_t);
            last_t = t;
        }
        renderingTime = finishedRendering - finishedCalculating;
        totalTime = finishedRendering - startedCalculating;
    }

}
