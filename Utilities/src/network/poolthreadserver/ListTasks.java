/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.poolthreadserver;

import java.util.LinkedList;

/**
 *
 * @author jona1993
 */
public class ListTasks implements SourceTaches {
    private LinkedList taskslist;
    
    public ListTasks(){
        taskslist = new LinkedList();
    }

    @Override
    public synchronized Runnable getTask() throws InterruptedException {
        System.err.println("getTask avt wait");
        while(!existTask()) wait();
        
        return (Runnable)taskslist.remove();
    }

    @Override
    public synchronized boolean existTask() {
        return !taskslist.isEmpty();
    }

    @Override
    public synchronized void recordTask(Runnable r) {
        taskslist.addLast(r);
        System.err.println("ListTask: Tâche dans la file");
        notify();
    }
    
}
