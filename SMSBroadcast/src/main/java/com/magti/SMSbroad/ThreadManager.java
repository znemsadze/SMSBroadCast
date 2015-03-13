package com.magti.SMSbroad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.magti.SMSbroad.smpp.SMSLogica;

public class ThreadManager {

	public static AtomicInteger nextSid;
	public final static int STATUS_WAITING = 1;
	public final static int STATUS_SUBMITTED = 2;
	public final static int STATUS_SEND = 3;
	public final static int STATUS_DELIVERED = 4;
	public final static int STATUS_FAILED = 5;
	public static Integer runThreads;
	public static Properties props;
	public static String actioncCmd;
	public static Integer SendersCnt;
	public static List<SMSLogica> smsLogicas;
	static {
		props = new Properties();
		try {
			runThreads = 0;
			props.load(Sender.class.getClassLoader().getResourceAsStream("config.properties"));
			// props.load(new FileInputStream("config.properties"));
			actioncCmd = props.getProperty("Action");
			SendersCnt = Integer.parseInt(props.getProperty("SenderCount"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static ExecutorService pool = Executors.newFixedThreadPool(10, new ThreadFactory() {
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable);
			thread.setName(String.format("SMSBroadcast %s", thread.getId()));
			return thread;
		}
	});

	public static void main(String[] args) throws Exception {
		List<Sender> senders = new ArrayList<Sender>();
		List<Listener> listeners = new ArrayList<Listener>();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		smsLogicas=new ArrayList<SMSLogica>();
		String cmd = actioncCmd;
		runThreads = 1;Integer chanelId=0;

		while (runThreads > 0) {
			if ("StartSending".equals(cmd)) {
				for (int i = 0; i < SendersCnt; i++) {
					chanelId++;
					SMSLogica smsLogica = new SMSLogica(chanelId);
					smsLogicas.add(smsLogica);
					smsLogica.reBind();
					Sender sender = new Sender(smsLogica);
					senders.add(sender);
					pool.submit(sender);
					
					Listener listener = new Listener(smsLogica);
					listeners.add(listener);
					pool.submit(listener);
					synchronized (runThreads) {
						runThreads++;
					}
				}
				
				cmd = in.readLine();
			} else if ("exit".equals(cmd)) {
				for (Sender sender : senders) {
					sender.stop();
				}
				for (Listener listener : listeners) {
					listener.stop();
				}
				synchronized (runThreads) {
					runThreads--;
				}
				System.out.println(runThreads);
			}

		}
		System.out.println("pool Shutdown");
		pool.shutdown();

	}

 

}
