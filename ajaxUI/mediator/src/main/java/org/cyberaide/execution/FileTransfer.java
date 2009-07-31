package org.cyberaide.execution;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Map;

import org.apache.log4j.Logger;

public class FileTransfer {
	// array to hold all the service info
	static Logger log = Logger.getLogger(FileTransfer.class);
	//private CoGObject[] wsgramNodes;
	private final String LOGINSERVICETYPE = "gridftp";
	public FileTransfer() {
	}

	/**
	 * third party file transfer in Globus
	 */
	public static String transfer(String user, String from, String to) {
		String userhome = "./gridusers/" + user;
		String path = "/./gridusers/" + user + "/home";
		String r = null;
		File proxyDir = new File(userhome);
		File curDir = new File(".");
		String strCurDir = null;
		try{
			strCurDir = curDir.getCanonicalPath();
		} catch (IOException e){
		}
		if(from.indexOf("file:///~") != -1){
			from = from.replaceFirst("file:///~", "file:///" + strCurDir + path);
		}
		if(to.indexOf("file:///~") != -1){
			to = to.replaceFirst("file:///~", "file:///" + strCurDir + path);
		}
		
		ProcessBuilder pb =
				new ProcessBuilder("globus-url-copy", from, to);
		
		Map<String, String> envs = pb.environment();
		String proxyPath = null;
		try{
			proxyPath = proxyDir.getCanonicalPath() + "/x509proxy";
		} catch (Exception e){
			log.error(e.getMessage());
		}
		envs.put("X509_USER_PROXY", proxyPath);
		
		log.info("trying to transfer from \"" + from + "\" to \"" + to + "\"");
		
		try {
			Process p = pb.start();
			BufferedReader stdInput =
					new BufferedReader(
							new InputStreamReader(p.getInputStream()));
			BufferedReader stdError =
					new BufferedReader(
							new InputStreamReader(p.getErrorStream()));

			// read the output from the command
			String s = null;
			StringBuffer ret = new StringBuffer();
			// System.out.println("Submitting job ...\n")
			while ((s = stdInput.readLine()) != null) {
				log.info(s);
				ret.append(s);
			}

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				log.error(s);
				ret.append(s);
			}
			r = new String(ret);
		} catch (IOException e) {
		}
		return r;
	}

}
