package com.github.wolfie.sessionguard;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class SessionguardApplication extends Application {
  private static final long serialVersionUID = -1544094538751229767L;
  
  @Override
  public void init() {
    final Window mainWindow = new Window("Sessionguard Application");
    final Label label = new Label(
        "This application has a 3-minute session, with a timeout warning of 2 minutes session time left.");
    mainWindow.addComponent(label);
    setMainWindow(mainWindow);
    final SessionGuard sessionGuard = new SessionGuard();
    sessionGuard.setTimeoutWarningPeriod(2);
    mainWindow.addComponent(sessionGuard);
    
    mainWindow.addComponent(new Button("Switch to keepalive",
        new Button.ClickListener() {
          private static final long serialVersionUID = -4423285632350279761L;
          private boolean keepalive = false;
          
          @Override
          public void buttonClick(final ClickEvent event) {
            keepalive = !keepalive;
            sessionGuard.setKeepalive(keepalive);
            event.getButton()
                .setCaption(
                    keepalive ? "Switch to timeout message"
                        : "Switch to keepalive");
          }
        }));
    
  }
  
}
