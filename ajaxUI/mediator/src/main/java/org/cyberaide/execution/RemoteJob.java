package org.cyberaide.execution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
//import org.cyberaide.core.CoGObject;
//import org.cyberaide.info.InfoService;

public class RemoteJob extends Executable {
	// array to hold all the service info
	Logger log = Logger.getLogger(this.getClass());
	//private CoGObject[] wsgramNodes;
	private final String LOGINSERVICETYPE = "prews-gram";// ws-gram, gridftp, gsi-openssh

	public RemoteJob() {
		super(WfType.SINGLEJOB);
		//InfoService info = new InfoService();
		//wsgramNodes = info.getServices(LOGINSERVICETYPE);
	}

	//public CoGObject[] getNodesList() {
	//	return wsgramNodes;
	//}
	
	/**
	 * set command attribute
	 */
	public void setcmd(String strCmd) {
		this.addAttributes("cmd", strCmd);
	}
	
	/**
	 * set remote host
	 */
	public void sethost(String strHost) {
		this.addAttributes("host", strHost);
	}
	
	
	/**
	 * submit a job and get back a jobId
	 */
	public String submit(String wsgramNode, String strCmd) {
		String wsgram = wsgramNode;
		String cmd = strCmd;
		String jobId = null;

		// ProcessBuilder pb = new ProcessBuilder("globusrun-ws", "-submit",
		// "-F", wsgram, "-c", cmd);
		ProcessBuilder pb =
				new ProcessBuilder("globus-job-submit", wsgram, cmd);

		// log.info("submitting the job to remote machine \"" + wsgram + "\"");
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
			// System.out.println("Submitting job ...\n")
			while ((s = stdInput.readLine()) != null) {
				System.out
						.print("JobId used to check status or get output is: \t");
				System.out.println(s);
				jobId = s;
			}

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				log.error(s);
			}
		} catch (IOException e) {
		}
		return jobId;
	}

	/**
	 * status query
	 */
	public String getStatus(String jobId) {
		ProcessBuilder pb = new ProcessBuilder("globus-job-status", jobId);
		String status = null;

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
			while ((s = stdInput.readLine()) != null) {
				status = s;
			}

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				log.error(s);
			}
		} catch (IOException e) {
		}
		return status;
	}

	/**
	 * job execution output retrieval
	 */
	public String getOutput(String jobId) {
		ProcessBuilder pb = new ProcessBuilder("globus-job-get-output", jobId);
		String output = null;

		try {
			Process p = pb.start();
			BufferedReader stdInput =
					new BufferedReader(
							new InputStreamReader(p.getInputStream()));
			BufferedReader stdError =
					new BufferedReader(
							new InputStreamReader(p.getErrorStream()));

			// read the output from the command
			StringBuffer result = new StringBuffer();
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				result.append(s);
				result.append("\n");
			}
			output = new String(result);

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				log.error(s);
			}
		} catch (IOException e) {
		}
		return output;
	}

	/**
	 * run a remote job synchronously
	 */
	public void run(String wsgramNode, String strCmd) {
		final String wsgram = wsgramNode;
		final String cmd = strCmd;
		Thread aThread = new Thread(new Runnable() {
			public void run() {
				// ProcessBuilder pb = new ProcessBuilder("globusrun-ws",
				// "-submit", "-F", wsgram, "-c", cmd);
				// ProcessBuilder pb = new ProcessBuilder("globusrun", "-r",
				// wsgram, "-o", "&(executable=" + cmd + ")");
				ProcessBuilder pb =
						new ProcessBuilder("globus-job-run", wsgram, cmd);

				// log.info("executing the job at remote machine \"" + wsgram +
				// "\"");
				try {
					Process p = pb.start();
					BufferedReader stdInput =
							new BufferedReader(new InputStreamReader(p
									.getInputStream()));
					BufferedReader stdError =
							new BufferedReader(new InputStreamReader(p
									.getErrorStream()));

					// read the output from the command
					String s = null;
					System.out.println("Job execution output ---> ");
					while ((s = stdInput.readLine()) != null) {
						System.out.println(s);
					}
					System.out.println("<--- finished execution");

					// read any errors from the attempted command
					while ((s = stdError.readLine()) != null) {
						log.error(s);
					}
				} catch (IOException e) {
					log.error("error occured during job execution");
				}
			}
		});
		try {
			aThread.start();
			aThread.join();
		} catch (InterruptedException e) {
		}
		return;
	}
}
