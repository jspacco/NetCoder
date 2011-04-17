// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco
// Copyright (C) 2011, David H. Hovemeyer
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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LoginService;
import edu.ycp.cs.netcoder.shared.problems.User;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {
	private static final long serialVersionUID = 1L;

	@Override
	public User login(String userName, String password) {
		// TODO: implement by searching for matching user record in database
		
		if (userName.equals("user") && password.equals("abc")) {
			User user = new User();
			user.setId(0);
			user.setUserName(userName);
			// Don't return the password in the User object
			return user;
		} else {
			return null;
		}
	}
}
