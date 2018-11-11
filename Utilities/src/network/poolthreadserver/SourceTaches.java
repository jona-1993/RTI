/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.poolthreadserver;

/**
 *
 * @author jona1993
 */
public interface SourceTaches {
    public Runnable getTask() throws InterruptedException;
    public boolean  existTask();
    public void recordTask(Runnable r);
}
