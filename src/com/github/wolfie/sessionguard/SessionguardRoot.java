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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

public class SessionguardRoot extends Root {
	private static final long serialVersionUID = -1544094538751229767L;
	
	private static final int WARNING_PERIOD_MINS = 2;
	
	@Override
	public void init(final WrappedRequest request) {
		setCaption("SessionGuard Demo");
		
		final int sessionTimeout = ((WebApplicationContext) getApplication()
				.getContext()).getHttpSession().getMaxInactiveInterval() / 60;
		
		final Label label = new Label("This application has a " + sessionTimeout
				+ "-minute session, with a timeout warning of " + WARNING_PERIOD_MINS
				+ " minutes session time left.");
		addComponent(label);
		
		final SessionGuard sessionGuard = new SessionGuard();
		sessionGuard.setTimeoutWarningPeriod(WARNING_PERIOD_MINS);
		addComponent(sessionGuard);
		
		addComponent(new Button("Switch to keepalive", new Button.ClickListener() {
			private static final long serialVersionUID = -4423285632350279761L;
			private boolean keepalive = false;
			
			@Override
			public void buttonClick(final ClickEvent event) {
				keepalive = !keepalive;
				sessionGuard.setKeepalive(keepalive);
				event.getButton().setCaption(
						keepalive ? "Switch to timeout message" : "Switch to keepalive");
			}
		}));
		
		final TextField warningXhtmlField = new TextField("Change warning XHTML");
		warningXhtmlField.setWidth("100%");
		warningXhtmlField.setValue(sessionGuard.getTimeoutWarningXHTML());
		warningXhtmlField.setImmediate(true);
		warningXhtmlField.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -5236596836421389890L;
			
			@Override
			public void valueChange(final ValueChangeEvent event) {
				sessionGuard.setTimeoutWarningXHTML(warningXhtmlField.getValue());
				showNotification("Warning string changed");
			}
		});
		addComponent(warningXhtmlField);
		
	}
}
