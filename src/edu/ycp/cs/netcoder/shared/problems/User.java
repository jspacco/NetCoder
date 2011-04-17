package edu.ycp.cs.netcoder.shared.problems;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable {
	private int id;
	private String userName;
	private String password;

	public User() {
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
}
