package org.cyberaide.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.globus.myproxy.MyProxy;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.ietf.jgss.GSSCredential;

public class Myproxy {

	final static String DEFAULTSERVER = "myproxy.teragrid.org";
	final static int PORT = 7512;
	final static int DEFAULTLIFETIME = 4 * 60 * 60; // default 4 hours
	String server;
	int port;
	String myPath;

	static Logger log = Logger.getLogger(Myproxy.class);
	final static String PROXY_FILE_PREFIX = "x509up_u";

	public Myproxy() {
		this.server = DEFAULTSERVER;
		this.port = PORT;
	}

	public Myproxy(String server) {
		this.server = server;
		this.port = PORT;
	}

	public Myproxy(String server, int port) {
		if (server != null) {
			this.server = server;
		} else {
			this.server = DEFAULTSERVER;
		}
		if (port != 0) {
			this.port = port;
		} else {
			this.port = PORT;
		}
	}

	public boolean retrieveCertificate(String user, String password) {
		return retrieveCertificate(server, port, user, password, 0);
	}

	public boolean retrieveCertificate(String user, String password,
			int lifetime) {
		return retrieveCertificate(server, port, user, password, lifetime);
	}

	/**
	 * retrieve a proxy credential from a myproxy server
	 * 
	 * @param host
	 *            myproxy server host
	 * @param port
	 *            myproxy server's port
	 * @param user
	 *            username
	 * @param password
	 *            passphrase to login to the myproxy server
	 * @param lifetime
	 *            the lifetime of the proxy credential (in seconds)
	 * 
	 * @return true if retrieve successfully, false otherwise
	 */
	public boolean retrieveCertificate(String host, int port, String user,
			String password, int lifetime) {

		if (host == null) {
			host = DEFAULTSERVER;
		}

		if (port == 0) {
			port = PORT;
		}

		if (lifetime == 0) {
			lifetime = DEFAULTLIFETIME; // default lifetime is 2 hours
		}

		try {
			MyProxy proxy = new MyProxy(host, port);
			GSSCredential cred = proxy.get(user, password, lifetime);
			int remainingtime = cred.getRemainingLifetime();

			// location to store the proxy certificate
			String tmpdir = System.getProperty("java.io.tmpdir");

			ProcessBuilder pb = new ProcessBuilder("id", "-u");
			Process p = pb.start();
			BufferedReader input = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String uid = input.readLine();
			String outputFile = tmpdir + "/" + PROXY_FILE_PREFIX + uid;

			// create file
			File f = new File(outputFile);
			if (f.exists()) {
				f.delete();
			}
			// write content of the credential into the file
			String path = f.getPath();
			OutputStream out = null;
			try {
				out = new FileOutputStream(f);
				// write the contents
				byte[] data = ((ExtendedGSSCredential) cred)
						.export(ExtendedGSSCredential.IMPEXP_OPAQUE);
				out.write(data);
			} catch (Exception e) {
				// e.printStackTrace(System.err);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
					}
				}
			}
			// set appropriate permissions
			pb = new ProcessBuilder("chmod", "600", outputFile);
			pb.start();
			cred.dispose();
			log.info("A proxy certificate has been received for user " + user
					+ " in " + path);
			log.info("valid remaining time in second(s): " + remainingtime);
			return true;
		} catch (Exception e) {
			log.info("Failed to retrieve proxy certificate!!\n" + e);
			return false;
		}
	}
}
