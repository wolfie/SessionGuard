package com.github.wolfie.sessionguard.client;

import com.github.wolfie.sessionguard.SessionGuard;
import com.github.wolfie.sessionguard.client.ui.VSessionGuard;
import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;

@Connect(SessionGuard.class)
public class SessionGuardConnector extends AbstractComponentConnector {
	private static final long serialVersionUID = -5179614917438796015L;
	
	public interface SessionGuardClientRpc extends ClientRpc {
		void pong();
	}
	
	private boolean isBeingPingedAlready;
	
	@Override
	protected void init() {
		super.init();
		
		registerRpc(SessionGuardClientRpc.class, new SessionGuardClientRpc() {
			private static final long serialVersionUID = -8010122317884306241L;
			
			@Override
			public void pong() {
				ping(false);
				getWidget().updateTimer();
			}
		});
	}
	
	@Override
	protected VSessionGuard createWidget() {
		final VSessionGuard widget = GWT.create(VSessionGuard.class);
		widget.setPinger(new VSessionGuard.Pinger() {
			@Override
			public void ping(final boolean immediately) {
				SessionGuardConnector.this.ping(immediately);
			}
		});
		return widget;
	}
	
	private void ping(final boolean immediately) {
		if (!isBeingPingedAlready) {
			/*
			 * if it's immediately, we don't want to ignore the second request. If
			 * it's not, the second request will trigger the send.
			 */
			isBeingPingedAlready = !immediately;
			
			final MethodInvocation invocation = new MethodInvocation(
					getConnectorId(),
					"com.github.wolfie.sessionguard.client.SessionGuardServerRpc",
					"ping", new Object[] {});
			getConnection().addMethodInvocationToQueue(invocation, immediately);
		} else if (immediately) {
			getConnection().sendPendingVariableChanges();
			isBeingPingedAlready = false;
		}
	}
	
	@Override
	public VSessionGuard getWidget() {
		return (VSessionGuard) super.getWidget();
	}
	
	@Override
	public SessionGuardState getState() {
		return (SessionGuardState) super.getState();
	}
	
	@Override
	public void onStateChanged(final StateChangeEvent stateChangeEvent) {
		getWidget().setSessionTimeoutSeconds(getState().getSessionTimeoutSeconds());
		getWidget().setWarningPeriodMinutes(getState().getWarningPeriodMinutes());
		getWidget().setTimeoutWarningXhtml(getState().getTimeoutWarningXhtml());
		getWidget().setKeepAlive(getState().isKeepAlive());
	}
}
