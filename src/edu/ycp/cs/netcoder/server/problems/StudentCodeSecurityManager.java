package edu.ycp.cs.netcoder.server.problems;

import java.security.Permission;

public class StudentCodeSecurityManager extends SecurityManager
{
    private ThreadGroup WORKER_THREAD_GROUP=KillableTaskManager.WORKER_THREAD_GROUP;
    
    @Override
    public void checkAccess(Thread t) {
        if (isWorkerThread()) {
            throw new SecurityException("Cannot access Thread");
        }
    }
    
    @Override
    public void checkAccess(ThreadGroup g) {
        if (isWorkerThread()) {
            throw new SecurityException("Cannot access ThreadGroup");
        }
    }

    @Override
    public void checkPermission(Permission perm) {
        check(perm);
        
    }
    
    @Override
    public void checkPermission(Permission perm, Object context) {
        check(perm);
    }
    
    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkCreateClassLoader()
     */
    @Override
    public void checkCreateClassLoader() {
        if (isWorkerThread()) {
            throw new SecurityException("Cannot create classloader");
        }
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#getThreadGroup()
     */
    @Override
    public ThreadGroup getThreadGroup() {
        //if (isStudentCode()) {
          //  throw new SecurityException("Student code should not get the ThreadGroup");
        //}
        return super.getThreadGroup();
    }
    /*
    private boolean isStudentCode() {
        if (true) return isWorkerThread();
        Class<?>[] contextArr=getClassContext();
        for (Class<?> c : contextArr) {
            //System.out.println("class for context: "+c+" classloader: "+c.getClassLoader());
            System.out.println("class for context: "+c);
            //System.out.println("class for context: "+c+" classloader: "+c.getClassLoader()+
                    //" "+(cl instanceof OnTheFlyCompiler));
            //System.out.println("code source: "+c.getProtectionDomain().getCodeSource());
//            ClassLoader cl=c.getClassLoader();
//            if (cl instanceof OnTheFlyCompiler) {
//                return true;
//            }
        }
        return false;
    }
    */
    
    private boolean isWorkerThread() {
        ThreadGroup group=getThreadGroup();
        if (group==WORKER_THREAD_GROUP) {
            return true;
        }
        return false;
    }
    
    private void check(Permission perm) {
        // allow reading the line separator
        if (perm.getName().equals("line.separator") && perm.getActions().contains("read")) {
            return;
        }
        if (perm.getName().equals("accessDeclaredMembers")) {
            return;
        }
        if (isWorkerThread()) {
            throw new SecurityException("Student code doing " +perm.getName());
        }
        
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkExit(int)
     */
    @Override
    public void checkExit(int status) {
        System.out.println("Trying to call system.exit");
        if (isWorkerThread()) {
            throw new SecurityException("Student code should not call System.exit(int i)");
        }
        System.out.println("system.exit is sadly OK");
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkExec(java.lang.String)
     */
    @Override
    public void checkExec(String cmd) {
        if (isWorkerThread()) {
            throw new SecurityException("Student code cannot execute commands");
        }
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkPackageDefinition(java.lang.String)
     */
    @Override
    public void checkPackageDefinition(String pkg) {
        System.out.println("checkPackageDefinition");
        if (pkg.equals("edu.ycp.cs.netcoder.server.compilers")) {
            return;
        }
    }
    
}
