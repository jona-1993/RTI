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
public class ThreadClient extends Thread {
    private SourceTaches tasksexec;
    private String name;
    
    private Runnable currentTask;
    
    public  ThreadClient(SourceTaches st, String n){
        tasksexec = st;
        name = n;
    }
    
    public void run(){
        while(!isInterrupted()){
            try
            {
                System.err.println("Thread Client avant get");
                currentTask = tasksexec.getTask();
            }
            catch(InterruptedException e)
            {
                System.err.println("Interrupted");
            }
            System.err.println("Run de currentTask - Client connecté");
            currentTask.run();
            System.err.println("Un client s'est déconnecté");
            ThreadServeur.nbClients -=1;
        }
    }
}
