package edu.ycp.cs.netcoder.server.problems;

import java.security.AccessControlException;

public class StudentSandboxSecurityManager extends SecurityManager
{

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkExit(int)
     */
    @Override
    public void checkExit(int status) {
        throw new AccessControlException("Please do not call System.exit(int i)");
    }

}
