package com.github.wolfie.sessionguard.client;

import com.vaadin.terminal.gwt.client.ComponentState;

public class SessionGuardState extends ComponentState {
	private static final long serialVersionUID = 2022586112957879875L;
	
	private int sessionTimeoutSeconds = -2;
	private int warningPeriodMinutes;
	private String timeoutWarningXhtml;
	private boolean keepAlive;
	
	public int getWarningPeriodMinutes() {
		return warningPeriodMinutes;
	}
	
	public void setWarningPeriodMinutes(final int timeoutMinutes) {
		this.warningPeriodMinutes = timeoutMinutes;
	}
	
	public String getTimeoutWarningXhtml() {
		return timeoutWarningXhtml;
	}
	
	public void setTimeoutWarningXhtml(final String timeoutWarningXhtml) {
		this.timeoutWarningXhtml = timeoutWarningXhtml;
	}
	
	public boolean isKeepAlive() {
		return keepAlive;
	}
	
	public void setKeepAlive(final boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
	
	public int getSessionTimeoutSeconds() {
		return sessionTimeoutSeconds;
	}
	
	public void setSessionTimeoutSeconds(final int sessionTimeoutSeconds) {
		this.sessionTimeoutSeconds = sessionTimeoutSeconds;
	}
	
}
