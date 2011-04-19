// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package edu.ycp.cs.netcoder.server.problems;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * I'm going to use the stop() method in thread.  I feel a little dirty.
 * 
 * I'm 97% sure that this is a safe use of stop().  Each
 * thread that might be stopped will put its result into
 * a separate object; if that thread is killed early, the
 * result simply won't show up and instead we'll put
 * in a user-supplied default result.  So long as the
 * instances of Task don't hold any locks or put any resources
 * into an inconsistent state, the threads spawned to execute
 * those tasks should be able to be stop()ed without any
 * negative repercussions.
 * 
 * 
 * 
 * @author jspacco
 *
 */
public class KillableTaskManager<T>
{
    /** list of "isolated tasks" to be executed */
    private List<IsolatedTask<T>> tasks;
    /** List of Outcomes; essentially placeholders objects where tasks will put their results */
    private List<Outcome<T>> results;
    private long maxRunTime;
    private int numPauses=5;
    // Buffers for stdout/stderr of each task thread
    private Map<Integer,String> stdOutMap=new HashMap<Integer,String>();
    private Map<Integer,String> stdErrMap=new HashMap<Integer,String>();
    private final PrintStream originalStdOut=System.out;
    private final PrintStream originalStdErr=System.err;
    /** Handles timeouts by producing a T representing a timeout event */
    private TimeoutHandler<T> timeoutHandler;
    /** SecurityManager for sandboxing student code*/
    private SecurityManager securityManager;
    
    /**
     * Callback handler to create a new task outcome of type T
     * when a task times out.
     * 
     * It's necessary to have a callback here because several 
     * task threads may time out, requiring the creation of multiple
     * objects for each timeout.
     * 
     */
    public interface TimeoutHandler<T> {
        public T handleTimeout();
    }
    
    public KillableTaskManager(List<IsolatedTask<T>> tasks, 
            long maxRunTime, 
            TimeoutHandler<T> timeoutHandler)
    {
        // will default to using current security manager
        this(tasks, maxRunTime, System.getSecurityManager(), timeoutHandler);
    }
    
    public KillableTaskManager(List<IsolatedTask<T>> tasks, 
            long maxRunTime,
            SecurityManager securityManager,
            TimeoutHandler<T> timeoutHandler)
    {
        this.tasks=tasks;
        this.maxRunTime=maxRunTime;
        this.timeoutHandler=timeoutHandler;
        this.securityManager=securityManager;
        
        this.results=new ArrayList<Outcome<T>>(tasks.size());
        for (int i=0; i<tasks.size(); i++) {
            results.add(new Outcome<T>());
        }
    }
    public boolean isFinished(int x) {
        return results.get(x).finished;
    }
    public T getResult(int x) {
        return results.get(x).result;
    }
    public List<T> getOutcomes(){
        List<T> ret=new LinkedList<T>();
        for (int i=0; i<results.size(); i++) {
            ret.add(getResult(i));
        }
        return ret;
    }
    
    public void run() {
        // re-direct stdout/stderr to print stream monitors
        // that will buffer the outputs for each thread
        ThreadedPrintStreamMonitor stdOutMonitor=
            new ThreadedPrintStreamMonitor(System.out);
        ThreadedPrintStreamMonitor stdErrMonitor=
            new ThreadedPrintStreamMonitor(System.err);
        System.setOut(stdOutMonitor);
        System.setErr(stdErrMonitor);

        Thread[] pool=new Thread[tasks.size()];
        for (int i=0; i<tasks.size(); i++) {
            IsolatedTask<T> task=tasks.get(i);
            pool[i]=new WorkerThread<T>(task, results.get(i), securityManager);
            pool[i].setDaemon(true);
            pool[i].start();
        }
        
        // now pause a couple of times
        for (int i=1; i<=numPauses; i++) {
            if (!pauseAndPoll(maxRunTime/numPauses, pool)) {
                // we can stop pausing
                break;
            }
        }
        
        // Go through and kill any threads that haven't finished yet
        // Also put the buffered output from stdout/stderr into the map
        for (int i=0; i<pool.length; i++) {
            Thread t=pool[i];
            if (t.isAlive()) {
                //XXX Yes, I know that stop() is deprecated.  
                //But this is a necessary use of stop!
                t.stop();
                
                //TODO Log that a thread is being stopped

                // stop the monitors
                stdOutMonitor.flush(t);
                stdOutMonitor.close(t);
                
                stdErrMonitor.flush(t);
                stdErrMonitor.close(t);
                
                // handle a timeout
                results.get(i).result=timeoutHandler.handleTimeout();
            }
            stdOutMap.put(i, stdOutMonitor.getBufferedOutput(t));
            stdErrMap.put(i, stdErrMonitor.getBufferedOutput(t));
        }
        
        // put stdout/stderr back to normal
        System.setOut(originalStdOut);
        System.setErr(originalStdErr);
    }
    
    /**
     * Pause for a certain amount of time.  Return true if any
     * of the threads in the given threadpool are still alive,
     * and false if they are all finished.
     * @param time
     * @param pool
     * @return
     */
    private boolean pauseAndPoll(long time, Thread[] pool) {
        try {
            Thread.sleep(time);
            for (Thread t : pool) {
                if (t.isAlive()) {
                    return true;
                }
            }
        } catch (InterruptedException e) {
            // should never happen; to be safe, assume a thread may
            // still be running.
            return true;
        }
        // no threads are alive, so we can stop waiting
        return false;
    }
    
    /**
     * Worker thread takes a given Task, calls its execute() method
     * to produce a result of type T, and puts the result into the 
     * given outcome container.
     * 
     * This thread is set up so that, assuming that the Task doesn't
     * access any shared resources, it is safe to use the stop() method
     * in thread to halt this thread.
     * 
     * @author jspacco
     *
     * @param <T>
     */
    private static class WorkerThread<T> extends Thread
    {
        private IsolatedTask<T> task;
        private Outcome<T> out;
        private SecurityManager securityManager;
        
        /**
         * Create a thread that executes the given task and puts
         * the result of the task into the given container.
         * 
         * @param task The task to execute
         * @param out The container in which to put the result of the task
         * @param sm SecurityManager to use for the task
         */
        public WorkerThread(IsolatedTask<T> task, Outcome<T> out, SecurityManager sm)
        {
            super();
            this.task=task;
            this.out=out;
            this.securityManager=sm;
        }

        /**
         * Given a task and a container, execute the task and put
         * its result in the container.  The entire run method catches
         * Throwable, so that if another thread uses stop() to kill this
         * thread, nothing bad should happen.
         * 
         * The IsolatedTask to be executed can be run with a different
         * security manager.
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            SecurityManager originalSecurityManager=System.getSecurityManager();
            // set the security manager for the isolated-task to use
            System.setSecurityManager(securityManager);
            try {
                T o=task.execute();
                out.result=o;
                out.finished=true;
            } catch (Throwable e) {
                // Make sure that the thread dies very quietly
                // "Attaching an exception-catching silencer to my thread-killing gun"
                // XXX Log this
                System.err.println("Thread killed in go!");
            } finally {
                // revert back to the original security manager
                System.setSecurityManager(originalSecurityManager);
            }
        }
    }
    
    /**
     * Simple container for a result of type T and whether the task
     * producing T finished normally.
     * 
     * @author jspacco
     *
     * @param <T>
     */
    private static class Outcome<T> {
        Outcome() {}
        boolean finished;
        T result;
    }

    public Map<Integer, String> getBufferedStdout() {
        return stdOutMap;
    }
    public Map<Integer, String> getBufferedStderr() {
        return stdErrMap;
    }
}
