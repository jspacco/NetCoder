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

package edu.ycp.cs.netcoder.server;

import java.util.Enumeration;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LoginService;
import edu.ycp.cs.netcoder.server.problems.HashPassword;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.problems.User;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {
	private static final long serialVersionUID = 1L;

	@Override
	public User login(String userName, String password) {
		EntityManager eman = HibernateUtil.getManager();

		List<User> result = eman.createQuery("select u from User u where u.userName = :userName", User.class)
				.setParameter("userName", userName)
				.getResultList();
		
		if (result.size() != 1) {
			// no such user
			return null;
		}
		
		User user = result.get(0);
		
		String salt = user.getSalt();
		String hashedPassword = HashPassword.computeHash(password, salt);
		
		if (hashedPassword.equals(user.getPasswordMD5())) {
			// Successful authentication
			
			// Set User object in server HttpSession so that other
			// servlets will know that the client is logged in
			HttpSession session = getThreadLocalRequest().getSession();
			session.setAttribute("user", user);
			
			// Clear salt and password md5 hash: that way, they can't be
			// compromised by malicious javascript on the client side
			user.setSalt("");
			user.setPasswordMD5("");
			
			return user;
		} else {
			// Failed authentication
			return null;
		}
	}
	
	@Override
	public void logout() {
		HttpSession session = getThreadLocalRequest().getSession();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> attributeNames = (Enumeration<String>) session.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attr = attributeNames.nextElement();
			session.removeAttribute(attr);
		}
	}
}
