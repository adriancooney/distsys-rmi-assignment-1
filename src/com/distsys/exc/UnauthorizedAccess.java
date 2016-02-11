package com.distsys.exc;

public class UnauthorizedAccess extends Exception {

	public UnauthorizedAccess(String reason) {
		super(reason);
	}
}

