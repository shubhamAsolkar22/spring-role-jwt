package com.fkog.security.jwt.model;

import java.util.Date;

public interface LastLogoutUserView {
	public Date getLastLoggedOut();

	public void setLastLoggedOut(Date lastLoggedOut);

}
