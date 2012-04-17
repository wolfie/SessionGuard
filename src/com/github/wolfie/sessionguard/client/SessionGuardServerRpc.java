package com.github.wolfie.sessionguard.client;

import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public interface SessionGuardServerRpc extends ServerRpc {
	void ping();
}
