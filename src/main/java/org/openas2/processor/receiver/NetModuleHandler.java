package org.openas2.processor.receiver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public interface NetModuleHandler
{
	void handle(NetModule owner, Socket s);

	void handle(final InetAddress remoteIp, final int remotePort, final InetAddress localIp,
			final int localPort, final InputStream inputStream, final OutputStream outputStream);
}
