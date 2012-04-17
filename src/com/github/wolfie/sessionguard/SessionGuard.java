// Copyright 2010 Henrik Paul
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.github.wolfie.sessionguard;

import com.github.wolfie.sessionguard.client.SessionGuardConnector.SessionGuardClientRpc;
import com.github.wolfie.sessionguard.client.SessionGuardServerRpc;
import com.github.wolfie.sessionguard.client.SessionGuardState;
import com.github.wolfie.sessionguard.exception.NonPositiveTimeSpanException;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractComponent;

/**
 * Server side component for the VSessionGuard widget.
 */
public class SessionGuard extends AbstractComponent {
	
	private static final long serialVersionUID = -8232615940183467323L;
	
	private final SessionGuardClientRpc clientRpc = getRpcProxy(SessionGuardClientRpc.class);
	
	private final SessionGuardServerRpc rpc = new SessionGuardServerRpc() {
		private static final long serialVersionUID = -1471851254915253281L;
		
		@Override
		public void ping() {
			clientRpc.pong();
		}
	};
	
	public SessionGuard() {
		registerRpc(rpc);
		setTimeoutWarningXHTML("It seems like you have been inactive for a while.<br/>"
				+ "Please note that your session will end in _ minutes, unless you do something.");
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws ClassCastException
	 *           if <code>getApplication().getContext()</code> doesn't return an
	 *           object instance of {@link WebApplicationContext}.
	 */
	@Override
	public void attach() {
		super.attach();
		
		final Application application = getApplication();
		if (application != null) {
			
			final ApplicationContext context = application.getContext();
			if (context instanceof WebApplicationContext) {
				final WebApplicationContext webContext = (WebApplicationContext) context;
				final int sessionTimeout = webContext.getHttpSession()
						.getMaxInactiveInterval();
				getState().setSessionTimeoutSeconds(sessionTimeout);
			} else {
				throw new ClassCastException(getClass() + " must be used in a "
						+ WebApplicationContext.class.getName()
						+ ", currently trying to be used in a "
						+ context.getClass().getName() + ".");
			}
		}
	}
	
	public boolean isKeptAlive() {
		return getState().isKeepAlive();
	}
	
	/**
	 * <p>
	 * Should the session be artificially kept alive by the {@link SessionGuard}.
	 * If keep alive is enabled, no warning message will be presented.
	 * </p>
	 * <p>
	 * By default, the session is not being kept alive.
	 * </p>
	 * <p>
	 * <em>Note:</em> If you need to set this permanently for your application,
	 * consider setting the <tt>session-timeout</tt> setting in your
	 * <tt>web.xml</tt> instead.
	 * 
	 * @param keepAlive
	 *          <tt>true</tt> if you want the {@link SessionGuard} to prevent the
	 *          session to time out. <tt>false</tt> if you want to disable this
	 *          feature.
	 */
	public void setKeepalive(final boolean keepAlive) {
		getState().setKeepAlive(keepAlive);
		requestRepaint();
	}
	
	/**
	 * <p>
	 * The amount of time left in the session lifetime, until the warning message
	 * will be presented.
	 * </p>
	 * <p>
	 * If {@link #isKeptAlive()} returns <tt>true</tt>, or given warning period is
	 * longer than the context's current session lifetime, no message will be
	 * presented.
	 * </p>
	 * 
	 * @param minutes
	 *          The amount of time in minutes.
	 * @throws NonPositiveTimeSpanException
	 *           if <tt>minutes</tt> is zero or less.
	 */
	public void setTimeoutWarningPeriod(final int minutes) {
		if (minutes > 0) {
			getState().setWarningPeriodMinutes(minutes);
			requestRepaint();
		} else {
			throw new NonPositiveTimeSpanException(
					"'minutes' must be greater than zero");
		}
	}
	
	/**
	 * @return The amount of minutes before session timeout that the warning is
	 *         displayed.
	 */
	public int getTimeoutWarningPeriod() {
		return getState().getWarningPeriodMinutes();
	}
	
	/**
	 * <p>
	 * Set a custom session timeout warning message
	 * </p>
	 * <p>
	 * If {@link #isKeptAlive()} returns <tt>true</tt>, no message will be
	 * presented.
	 * </p>
	 * 
	 * @param timeoutWarningXHTML
	 *          The warning message in XHTML-format. Use the underscore character
	 *          '_' as a placeholder for remaining minutes in the session.
	 * @throws NullPointerException
	 *           if <tt>timeoutWarningHTML</tt> is <tt>null</tt>
	 */
	public void setTimeoutWarningXHTML(final String timeoutWarningXHTML) {
		if (timeoutWarningXHTML != null) {
			getState().setTimeoutWarningXhtml(timeoutWarningXHTML);
			requestRepaint();
		} else {
			throw new NullPointerException("timeoutWarningXHTML was null");
		}
	}
	
	/**
	 * Get the currently used warning message
	 * 
	 * @return The current warning message in XHTML-format.
	 */
	public String getTimeoutWarningXHTML() {
		return getState().getTimeoutWarningXhtml();
	}
	
	@Override
	public SessionGuardState getState() {
		return (SessionGuardState) super.getState();
	}
}
