package com.ac;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.os.Handler;
import android.widget.TextView;

public class Util {

	static Process proc;
	static BufferedReader reader;
	static BufferedReader errreader;
	static PrintWriter writer;
	static Handler mHandler = new Handler();
	static TextView termOut;
	static Thread thread;
	static boolean running;
	
	public static void initTerminal(TextView term) {
		termOut = term;
	}

	public static void exec(String cmd) {
		try {

			// Executes the command.

			if (proc == null) {
				proc = Runtime.getRuntime().exec("/system/bin/sh", null, null);

				reader = new BufferedReader(new InputStreamReader(
						proc.getInputStream()));

				errreader = new BufferedReader(new InputStreamReader(
						proc.getErrorStream()));

				writer = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(proc.getOutputStream())));

				running = true;
				
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						int read;
						final StringBuffer output = new StringBuffer();

						try {
						for (;;) {
							
							if (!running) break;
							
//stdout
								char[] buffer = new char[4096];
								while (running &&(read = reader.read(buffer)) > 0) {
									output.append(buffer, 0, read);
									final String chunk = output.toString();
									output.setLength(0);
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											termOut.append(chunk);
										}
									});
								}
//stderr
								while (running && (read = errreader.read(buffer)) > 0) {
									output.append(buffer, 0, read);
									final String chunk = output.toString();
									output.setLength(0);
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											termOut.append(chunk);
										}
									});
								}

							}
						} catch (Exception ex) {
						}
					}
				});
				thread.start();
			}

			writer.write(cmd + "\n");
			writer.flush();

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getTerminalPrompt() {
		return "$";
	}

	
	public static void terminate(){
		if (proc!=null){
			try{
			running = false;
			thread.interrupt();
			proc.destroy();
			proc = null;
			} catch (Exception ignore){}
		}
	}
}
