package com.github.wolfie.sessionguard;

import java.util.Map;

import com.github.wolfie.sessionguard.client.ui.VSessionGuard;
import com.github.wolfie.sessionguard.exception.NonPositiveTimeSpanException;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractComponent;

/**
 * Server side component for the VSessionGuard widget.
 */
@com.vaadin.ui.ClientWidget(com.github.wolfie.sessionguard.client.ui.VSessionGuard.class)
public class SessionGuard extends AbstractComponent {
  
  private static final long serialVersionUID = -8232615940183467323L;
  
  private int sessionTimeout = -2;
  private int timeoutWarningPeriodMinutes = 0;
  private String timeoutWarningHTML = "It seems like you have been inactive for a while.<br/>"
      + "Please note that your session will end in _ minutes, unless you do something.";
  private boolean keepalive = false;
  
  @Override
  public void paintContent(final PaintTarget target) throws PaintException {
    target.addAttribute(VSessionGuard.A_TIMEOUT_SECS_INT, sessionTimeout);
    target.addAttribute(VSessionGuard.A_WARNING_PERIOD_MINS_INT,
        timeoutWarningPeriodMinutes);
    target.addAttribute(VSessionGuard.A_TIMEOUT_MSG_XHTML_STRING,
        timeoutWarningHTML);
    target.addAttribute(VSessionGuard.A_KEEPALIVE_BOOL, keepalive);
    target.addVariable(this, VSessionGuard.V_PING_BOOL, false);
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
        sessionTimeout = webContext.getHttpSession().getMaxInactiveInterval();
      } else {
        throw new ClassCastException(getClass() + " must be used in a "
            + WebApplicationContext.class.getName()
            + ", currently trying to be used in a "
            + context.getClass().getName() + ".");
      }
    }
  }
  
  public boolean isKeptAlive() {
    return keepalive;
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
    keepalive = keepAlive;
    requestRepaint();
  }
  
  /**
   * <p>
   * Set the time at which a session timeout warning will be presented.
   * </p>
   * <p>
   * If {@link #isKeptAlive()} returns <tt>true</tt>, no message will be
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
      timeoutWarningPeriodMinutes = minutes;
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
    return timeoutWarningPeriodMinutes;
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
   * @param timeoutWarningHTML
   *          The warning message in XHTML-format. Use the underscore character
   *          '_' as a placeholder for remaining minutes in the session.
   * @throws NullPointerException
   *           if <tt>timeoutWarningHTML</tt> is <tt>null</tt>
   */
  public void setTimeoutWarningHTML(final String timeoutWarningHTML) {
    if (timeoutWarningHTML == null) {
      this.timeoutWarningHTML = timeoutWarningHTML;
      requestRepaint();
    } else {
      throw new NullPointerException("timeoutWarningHTML was null");
    }
  }
  
  /**
   * Get the currently used warning message
   * 
   * @return The current warning message in XHTML-format.
   */
  public String getTimeoutWarningString() {
    return timeoutWarningHTML;
  }
  
  @Override
  public void changeVariables(final Object source,
      final Map<String, Object> variables) {
    /*
     * Always ping back when something happens on the client side, to let
     * VSessionGuard know that something has happened, and so that it resets its
     * timer.
     */
    requestRepaint();
  }
}
