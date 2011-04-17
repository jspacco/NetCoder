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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
 * TODO: Track stdout and stderr and return with TestResult.
 * 
 * 
 * @author jspacco
 *
 */
public class KillableTaskManager<T>
{
    private List<IsolatedTask<T>> tasks;
    private List<Outcome<T>> results;
    private T defaultFailure;
    private long maxRunTime;
    private int numPauses=5;
    
    public KillableTaskManager(List<IsolatedTask<T>> tasks, long maxRunTime, T defaultFailure) {
        this.tasks=tasks;
        this.maxRunTime=maxRunTime;
        this.defaultFailure=defaultFailure;
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
        Thread[] pool=new Thread[tasks.size()];
        
        for (int i=0; i<tasks.size(); i++) {
            IsolatedTask<T> task=tasks.get(i);
            pool[i]=new WorkerThread<T>(task, results.get(i));
            pool[i].setDaemon(true);
            pool[i].start();
        }
        // now pause a couple of times
        for (int i=1; i<=numPauses; i++) {
            if (!pauseAndPoll(maxRunTime/numPauses, pool)) {
                return;
            }
        }
        
        for (int i=0; i<pool.length; i++) {
            Thread t=pool[i];
            if (t.isAlive()) {
                //XXX Yes, I know I shouldn't do this.  But this will totally work!
                //XXX Log that a thread is being stopped
                t.stop();
                // Supply the "default failure"
                results.set(i, new Outcome<T>(defaultFailure));
            }
        }
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
        
        /**
         * Create a thread that executes the given task and puts
         * the result of the task into the given container.
         * 
         * @param task The task to execute
         * @param out The container in which to put the result of the task
         */
        public WorkerThread(IsolatedTask<T> task, Outcome<T> out) {
            super();
            this.task=task;
            this.out=out;
        }

        /**
         * Given a task and a container, execute the task and put
         * its result in the container.  The entire run method catches
         * Throwable, so that if another thread uses stop() to kill this
         * thread, nothing bad should happen.
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            try {
                T o=task.execute();
                out.result=o;
                out.finished=true;
            } catch (Throwable e) {
                // Make sure that the thread dies very quietly
                // "Attaching an exception-catching silencer to my thread-killing gun"
                // XXX Log this
                System.err.println("Thread killed in go!");
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
        Outcome(T t) {
            finished=false;
            result=t;
        }
        boolean finished;
        T result;
    }
}
