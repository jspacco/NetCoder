package edu.ycp.cs.netcoder.server.problems;

import java.security.AccessControlException;
import java.security.Permission;

public class StudentCodeSecurityManager extends SecurityManager
{
    // Extra level of indirection:
    // To create a sandbox, first create a container.
    // Then create an instance of StudentCodeSecurityManager using the container
    // Now enable the container
    // There is no way to get a reference to the container from the sanboxed code
    // even if you can get a reference to the security manager.
    // So, only the thread that created the container and still has a reference to it
    // can disable the security manager and then get rid of it.
    private SandboxBooleanContainer container;
    public StudentCodeSecurityManager(SandboxBooleanContainer container) {
        this.container=container;
    }
    
    public static class SandboxBooleanContainer {
        private boolean sandboxed=false;
        public void enableSandbox() {
            sandboxed=true;
        }
        public void disableSandbox() {
            sandboxed=false;
        }
        private boolean isSandboxed() {
            return this.sandboxed;
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
    private void check(Permission perm) {
        //if (true) return;
        if (!container.isSandboxed()) {
            return;
        }
        // allow reading the line separator
        if (perm.getName().equals("line.separator") && perm.getActions().contains("read")) {
            return;
        }
        
        throw new SecurityException("Permission denied: " + perm);
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkExit(int)
     */
    @Override
    public void checkExit(int status) {
        if (!container.isSandboxed()) return;
        throw new AccessControlException("Student code should not call System.exit(int i)");
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkExec(java.lang.String)
     */
    @Override
    public void checkExec(String cmd) {
        if (!container.isSandboxed()) return;
        throw new AccessControlException("Student code should not create a process");
    }
}
