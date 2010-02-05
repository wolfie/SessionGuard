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

package com.github.wolfie.sessionguard.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VNotification;

public class VSessionGuard extends Widget implements Paintable {
  
  public static final String A_TIMEOUT_SECS_INT = "timeout";
  public static final String A_WARNING_PERIOD_MINS_INT = "mins";
  public static final String A_TIMEOUT_MSG_XHTML_STRING = "xhtml";
  public static final String A_KEEPALIVE_BOOL = "keepalive";
  
  public static final String V_PING_BOOL = "ping";
  
  /** A minute in milliseconds */
  private static final int MINUTE = 60000; // legibility ftw
  private static final int MINUTES = MINUTE; // legibility ftw
  
  /** The client side widget identifier */
  protected String paintableId;
  
  /** Reference to the server connection object. */
  protected ApplicationConnection client;
  private int timeoutMins;
  private int warningPeriod;
  private String xhtmlMessage;
  private boolean keepalive;
  
  private final Timer timer = new Timer() {
    @Override
    public void run() {
      
      if (!keepalive) {
        final VNotification notification = new VNotification(-1);
        updateMinutes(notification, warningPeriod);
        
        new Timer() {
          int minutesLeft = warningPeriod;
          
          @Override
          public void run() {
            if (notification.isShowing()) {
              if (minutesLeft > 0) {
                // refresh the message with an updated minute-count
                minutesLeft--;
                updateMinutes(notification, minutesLeft);
              } else {
                // the session should have ended by now, so Vaadin should show a
                // session timeout message when trying to update the component.
                ping(true);
                cancel();
                notification.hide();
              }
            } else {
              cancel();
            }
          }
        }.scheduleRepeating(MINUTE);
      } else {
        ping(true);
      }
    }
    
    private void updateMinutes(final VNotification notification,
        final int minutesLeft) {
      notification.show(getXhtmlMessage(xhtmlMessage, minutesLeft),
          VNotification.CENTERED_TOP, "warning");
    }
    
    private String getXhtmlMessage(final String xhtmlMessage, final int minutes) {
      return xhtmlMessage.replace("_", String.valueOf(minutes));
    }
  };
  
  public VSessionGuard() {
    setElement(Document.get().createDivElement());
    if (BrowserInfo.get().isIE6()) {
      getElement().getStyle().setProperty("overflow", "hidden");
      getElement().getStyle().setProperty("height", "0");
    }
  }
  
  public void updateFromUIDL(final UIDL uidl, final ApplicationConnection client) {
    this.client = client;
    paintableId = uidl.getId();
    
    if (client.updateComponent(this, uidl, true)) {
      return;
    }
    
    if (uidl.hasAttribute(A_TIMEOUT_SECS_INT)) {
      timeoutMins = uidl.getIntAttribute(A_TIMEOUT_SECS_INT) / 60;
      updateTimer();
    }
    
    if (uidl.hasAttribute(A_WARNING_PERIOD_MINS_INT)) {
      warningPeriod = uidl.getIntAttribute(A_WARNING_PERIOD_MINS_INT);
      updateTimer();
    }
    
    if (uidl.hasAttribute(A_TIMEOUT_MSG_XHTML_STRING)) {
      xhtmlMessage = uidl.getStringAttribute(A_TIMEOUT_MSG_XHTML_STRING);
    }
    
    if (uidl.hasAttribute(A_KEEPALIVE_BOOL)) {
      keepalive = uidl.getBooleanAttribute(A_KEEPALIVE_BOOL);
      updateTimer();
    }
    
    if (uidl.hasVariable(V_PING_BOOL)) {
      // we don't actually need to do anything with this. It doesn't matter
      // whether this value comes through or not.
    }
    
    /*
     * Never ping here immediately - when another component sends something to
     * the server, this will also be sent. Once the server-side gets this value,
     * it will refresh this, and we'll know that the session isn't about to end.
     */
    ping(false);
  }
  
  private void ping(final boolean now) {
    client.updateVariable(paintableId, "ping", true, now);
  }
  
  private void updateTimer() {
    if (!keepalive && 0 < timeoutMins && timeoutMins > warningPeriod) {
      // re-schedule the timeout
      timer.schedule((timeoutMins - warningPeriod) * MINUTES);
    } else if (keepalive) {
      /*
       * always launch keepalive only half a minute before the session is to
       * end. Half a minute, since one minute is the shortest session, and we
       * don't to update all the time (i.e. 0 delay)
       */
      timer.schedule(Long.valueOf(Math.round((timeoutMins - 0.5) * MINUTES))
          .intValue());
    } else {
      // timeout was <= 0, which doesn't make sense, or timeout <=
      // warningPeriod, which is just plain stupid. Abort countdown.
      timer.cancel();
    }
  }
}
