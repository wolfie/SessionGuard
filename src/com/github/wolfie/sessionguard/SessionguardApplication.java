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

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class SessionguardApplication extends Application {
  private static final long serialVersionUID = -1544094538751229767L;

  private static final int WARNING_PERIOD_MINS = 2;

  @Override
  public void init() {
    final VerticalLayout mainLayout = new VerticalLayout();
    mainLayout.setSpacing(true);
    final Window mainWindow = new Window("Sessionguard Application", mainLayout);

    final int sessionTimeout = ((WebApplicationContext) getContext())
        .getHttpSession().getMaxInactiveInterval() / 60;

    final Label label = new Label("This application has a " + sessionTimeout
        + "-minute session, with a timeout warning of " + WARNING_PERIOD_MINS
        + " minutes session time left.");
    mainWindow.addComponent(label);
    setMainWindow(mainWindow);
    final SessionGuard sessionGuard = new SessionGuard();
    sessionGuard.setTimeoutWarningPeriod(WARNING_PERIOD_MINS);
    mainWindow.addComponent(sessionGuard);

    mainWindow.addComponent(new Button("Switch to keepalive",
        new Button.ClickListener() {
          private static final long serialVersionUID = -4423285632350279761L;
          private boolean keepalive = false;

          public void buttonClick(final ClickEvent event) {
            keepalive = !keepalive;
            sessionGuard.setKeepalive(keepalive);
            event.getButton()
                .setCaption(
                    keepalive ? "Switch to timeout message"
                        : "Switch to keepalive");
          }
        }));

    final TextField warningXhtmlField = new TextField("Change warning XHTML");
    warningXhtmlField.setWidth("100%");
    warningXhtmlField.setValue(sessionGuard.getTimeoutWarningXHTML());
    warningXhtmlField.setImmediate(true);
    warningXhtmlField.addListener(new Property.ValueChangeListener() {
      private static final long serialVersionUID = -5236596836421389890L;

      public void valueChange(final ValueChangeEvent event) {
        sessionGuard.setTimeoutWarningXHTML((String) warningXhtmlField
            .getValue());
        getMainWindow().showNotification("Warning string changed");
      }
    });
    mainWindow.addComponent(warningXhtmlField);

  }
}
