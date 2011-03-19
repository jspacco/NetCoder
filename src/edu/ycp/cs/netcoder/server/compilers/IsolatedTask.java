package edu.ycp.cs.netcoder.server.compilers;

/**
 * An isolated Task that can be executed in a separately thread, where the thread
 * might be unsafely killed with the stop() method in Thread.
 * 
 * <b>Tasks executed by this functor must not have access to any
 * shared state so that it's impossible for these tasks to leave
 * anything in an inconsistent state</b>
 * 
 * In other words, Tasks are designed to be like a sub-process that produces
 * an output of type T.  If the Task is stopped before completing, then the
 * result type simply won't be there, but no internal state should be left
 * in an inconsistent state.
 * 
 * @author jspacco
 *
 * @param <T>
 */
interface IsolatedTask<T> {
    public T execute() throws Throwable;
}