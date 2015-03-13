package com.magti.SMSbroad.smpp;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.PDU;
import org.smpp.pdu.Request;
import org.smpp.pdu.Response;
import org.smpp.pdu.SubmitSM;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.UnbindResp;
import org.smpp.util.ByteBuffer;

import com.magti.SMSbroad.ThreadManager;
import com.magti.SMSbroad.utils.Lm;

public class SMSLogica extends Thread {

	public static Properties props;
	public static int Transmit = 0;
	public static int Receive = 1;
	public static int Trancieve = 2;

	public Session iSession;
	public SMSReceiver iReceiver;

	public IMessageListener iListener;
	public String iSMSCAddr;
	public int iSMSCPort;
	public String iSMSCUser;
	public String iSMSCPassword;
	public String iSystemType;
	public String iSourcePhoneNumber;
	public boolean iTerminating;

	public long iEnquireInterval;
	public long iEnquireTimeout;
	public boolean iEnquireSent;
	public boolean iWasConnected;
	public long iLastCommReceived;
	public long iSubmitInterval;

	public AtomicInteger nextSid;

	public boolean connected;

	public SMSLogica(Integer chanelId) {

		props = new Properties();

		try {
			props.load(SMSLogica.class.getClassLoader().getResourceAsStream("config.properties"));
			// props.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		connected = false;
		nextSid = new AtomicInteger(1);

		iListener = null;
		iSMSCAddr = (String) props.get("SMSCAddress");
		iSMSCPort = Integer.parseInt((String) props.get("SMSCPort"));
		iSMSCUser = (String) props.get("SMSCUser")+chanelId;
		iSMSCPassword = (String) props.get("SMSCPassword");
		iSystemType = (String) props.get("SystemType");
		iSourcePhoneNumber = (String) props.get("SourcePhoneNumber");
		iEnquireInterval = Long.parseLong((String) props.get("EnquireInterval"));
		iEnquireTimeout = Long.parseLong((String) props.get("EnquireTimeout"));
		iWasConnected = false;
		iSubmitInterval = Long.parseLong((String) props.get("SubmitInterval"));

		iEnquireInterval = 1;
	}

	public synchronized boolean isConnected() {
		return connected;
	}

	protected synchronized void setConnected(boolean aConnected) {
		connected = aConnected;
	}

	public void run() {
		Lm.log().info("SMSLogica started...");

		try {
			reBind();
		} catch (Exception ex2) {
			Lm.log().log(Level.SEVERE, "Unable to rebind() during run.", ex2);

		}

		while (!iTerminating) {

			try {

				if (iEnquireSent && System.currentTimeMillis() - iLastCommReceived > iEnquireTimeout) {
					Lm.log().warning(
						"Enquire response not received for " + (System.currentTimeMillis() - iLastCommReceived)
							+ " msec - rebinding");
					reBind();
				} else if (!iEnquireSent && (System.currentTimeMillis() - iLastCommReceived > iEnquireInterval)) {
					try {
						sendEnquire();
					} catch (Exception ex1) {
						Lm.log().log(Level.SEVERE, "Exception sending enquire, session missed.", ex1);

						reBind();
					}
					iEnquireSent = true;
					Lm.log().fine(
						"No messages received for " + (System.currentTimeMillis() - iLastCommReceived)
							+ " msec, enquiring...");
				}

				handleIncomingMessages();
			} catch (Exception ex) {
				Lm.log().log(Level.SEVERE, "Exception during inbound message handling.", ex);

			}

			try {
				sleep(100);
			} catch (InterruptedException iex) {
				Lm.log().info("Sleep interrupted.");
			}
		}
		Lm.log().info("SMSLogica terminating...");

		try {
			unBind();
		} catch (Exception ex) {
			Lm.log().log(Level.SEVERE, "Exception during unbind.", ex);
		}
	}

	synchronized public void sendEnquire() throws Exception {
		EnquireLink msg = new EnquireLink();

		msg.setSequenceNumber(getNextSequenceID());

		iSession.enquireLink(msg);
		Lm.log().finest("Sent EnquireLink.");
	}

	public void shutdown() {
		iTerminating = true;
		interrupt();

		try {
			this.join(3000);
		} catch (InterruptedException iex) {
		}
	}

	synchronized public void reBind() throws Exception {

		Lm.log().info("(Re)binding...");
		if (iReceiver != null || iSession != null) {
			Lm.log().info("Already bound - unbinding...");
			unBind();
			Lm.log().info("Waiting 90 sec...");
			try {
				sleep(90000);
			} catch (InterruptedException intex) {

			}
			Lm.log().info("Waiting comeplete...");
		}

		BindRequest request = null;
		BindResponse response = null;

		request = new BindTransciever();

		Lm.log().fine("Establishing TCP connection to " + iSMSCAddr + ":" + iSMSCPort);
		TCPIPConnection connection = new TCPIPConnection(iSMSCAddr, iSMSCPort);
		connection.setReceiveTimeout(20 * 1000);
		Lm.log().fine("Connected to SMSC.");

		iSession = new Session(connection);

		System.out.println(iSMSCUser + "  " + iSMSCPassword + "  " + iSystemType + "   " + iSourcePhoneNumber);

		// set values
		request.setSystemId(iSMSCUser);
		request.setPassword(iSMSCPassword);
		request.setSystemType(iSystemType);
		request.setInterfaceVersion((byte) 0x34);

		request.setAddressRange((byte) 1, (byte) 1, iSourcePhoneNumber);
		// Lm.log().finer("Bind request being sent: " + request.debugString());

		// send the request
		iReceiver = new SMSReceiver(iSession);
		response = iSession.bind(request, iReceiver);

		Lm.log().fine("Bind response: " + response.debugString());

		if (response.getCommandStatus() != Data.ESME_ROK) {
			Lm.log().severe("Bind failed!");
			iReceiver = null;
		} else {
			iLastCommReceived = System.currentTimeMillis();
			iEnquireSent = false;
			iWasConnected = true;
			setConnected(true);
		}

	}

	public void unBind() throws Exception {
		Lm.log().fine("Unbinding... ");
		setConnected(false);

		if (iReceiver == null) {
			Lm.log().warning("Already unbounded - unable to unbound");
			// return;
		}

		if (iSession.getReceiver().isReceiver()) {
			Lm.log().info("Stopping receiver...");
		}

		try {
			UnbindResp response = iSession.unbind();
			Lm.log().fine("Response unbinded " + response.debugString());
		} catch (Exception ex) {
			Lm.log().log(Level.WARNING, "Unbind failed", ex);
		}

		try {
			iSession.getConnection().close();
		}

		catch (Exception ex) {
			Lm.log().log(Level.WARNING, "Close connection failed", ex);
		}

		iReceiver = null;
		Lm.log().info("Unbinding finished!");

	}

	synchronized public void submitMessage(int aSessionID, int aSubscriberID, String aPhone, String aMessage)
		throws Exception {

		sleep(iSubmitInterval);
		SubmitSM msg = new SubmitSM();
		msg.setSequenceNumber(getNextSequenceID());
		msg.setSourceAddr((byte) 0, (byte) 1, iSourcePhoneNumber);
		msg.setDestAddr((byte) 1, (byte) 1, "995" + aPhone);
		msg.setShortMessage(aMessage);
		msg.setValidityPeriod("000001000000000R");
		msg.setRegisteredDelivery((byte) 1);

		try {
			iSession.submit(msg);
			if (iListener != null) {
				Lm.log().finest("Sending message to " + aPhone);
				iListener.registerSequenceID(aSessionID, aSubscriberID, aPhone, msg.getSequenceNumber());
			} else
				Lm.log().severe("Listener missing for SubmitSM!");

		} catch (Exception ex) {
			reBind();
			throw ex;
		}
	}
	
	

	synchronized public void submitMessageSimple(int uniId, String aPhone, String aMessage, String from,Integer smsCount, Integer isGeo)
		throws Exception {
		sleep(iSubmitInterval);
		byte ton = 0x05;
		byte npi = 0x00;
		try {
			int randInt=getNextSequenceID();
			int smsLen=(isGeo==0)?130:50;
			String smsEnc=(isGeo==0)?Data.ENC_ASCII:Data.ENC_UTF16;
			int encLen=(isGeo==0)?1:8;
			if (aMessage.length() <= smsLen) {
				SubmitSM msg = new SubmitSM();
				msg.setSequenceNumber(uniId);
				msg.setSourceAddr(ton, npi, from);
				msg.setDestAddr((byte) 1, (byte) 1, "995" + aPhone);
				msg.setShortMessage(aMessage,smsEnc);
				msg.setValidityPeriod("000001000000000R");
				msg.setDataCoding((byte)encLen);
				msg.setRegisteredDelivery((byte) 1);

				iSession.submit(msg);
			}else{
				String smsparts[]=splitMessage(aMessage, smsLen) ;
				for(int i=0;i<smsparts.length;i++){
				SubmitSM msg = new SubmitSM();
				msg.setEsmClass((byte)Data.SM_UDH_GSM); //Set UDHI Flag Data.SM_UDH_GSM=0x40
				
				ByteBuffer ed = new ByteBuffer();

				ed.appendByte((byte) 5); // UDH Length

				ed.appendByte((byte) 0x00); // IE Identifier

				ed.appendByte((byte) 3); // IE Data Length

				ed.appendByte((byte) uniId) ; //Reference Number

				ed.appendByte((byte) smsparts.length) ; //Number of pieces

				ed.appendByte((byte) (i+1)) ; //Sequence number

				//This encoding comes in Logica Open SMPP. Refer to its docs for more detail

				ed.appendString(smsparts[i], smsEnc);
			
				msg.setShortMessageData(ed);
				msg.setSequenceNumber(uniId);
				msg.setSourceAddr(ton, npi, from);
				msg.setDestAddr((byte) 1, (byte) 1, "995" + aPhone);
				msg.setValidityPeriod("000001000000000R");
				msg.setDataCoding((byte) encLen);
				
				msg.setRegisteredDelivery((byte) 1);
				System.out.println(msg.debugString());
				
				iSession.submit(msg);
				 System.out.println("submited");
				}
				
			}

			 
		} catch (Exception ex) {
			ex.printStackTrace();
			reBind();
			throw ex;
		}
	}

	private static String[] splitMessage(String value,int chunkLen){
		int cnt=(value.length()%chunkLen==0)?(value.length()/chunkLen):(value.length()/chunkLen+1);
		
		String[] a=new String[cnt]; int k=0;
		while(!value.isEmpty()){
			if(chunkLen<value.length()){
			a[k]=value.substring(0,chunkLen);
			value=value.substring(chunkLen);
			System.out.println(a[k]);
			}else{
				a[k]=value;
				value="";
			}
			k++;
		}
		return a;
		
	}
	
	
	
	
	private int getNextSequenceID() {
		return nextSid.incrementAndGet();
	}

	synchronized public void setListener(IMessageListener aListener) {
		iListener = aListener;
	}

	synchronized public void handleIncomingMessages() throws Exception {

		if (iReceiver == null) {

			if (iWasConnected)
				try {
					sleep(90000);
				} catch (InterruptedException intex) {

				}
			reBind();
		}

		int c = iReceiver.getWaitingCount();
		if (c > 0)
			Lm.log().finer("Polling for received messages... " + c + " found.");

		for (int i = 0; i < c; i++) {
			ServerPDUEvent e = iReceiver.getRequestEvent(100);
			PDU pdu = e.getPDU();
			if (pdu != null) {
				if (pdu.isRequest()) {
					// Handle request
					Lm.log().finest("[" + (i + 1) + "] Received request " + pdu.debugString());
					Response response = ((Request) pdu).getResponse();

					// respond with default response
					iSession.respond(response);
					Lm.log().finest("[" + (i + 1) + "]  Responded with " + response.debugString());

					// Decode request
					if (pdu instanceof DeliverSM) {
						DeliverSM req = ((DeliverSM) pdu);

						if (req.getMessageState() == 2) {
							if (iListener != null) {
								Lm.log().finest("[" + (i + 1) + "]  DELIVERED for " + req.getReceiptedMessageId());
								iListener.submitReport(req.getReceiptedMessageId(), ThreadManager.STATUS_DELIVERED);
							} else
								Lm.log().severe("Listener missing for REPORT 1 " + req.getReceiptedMessageId());

						} else if (req.getMessageState() > 2) {
							if (iListener != null) {
								Lm.log().finest("[" + (i + 1) + "]  FAILED for " + req.getReceiptedMessageId());
								iListener.submitReport(req.getReceiptedMessageId(), ThreadManager.STATUS_FAILED);
							} else
								Lm.log().severe("Listener missing for REPORT 0 " + req.getReceiptedMessageId());
						}

					} else {
						Lm.log().warning("[" + (i + 1) + "] Unrecognized request!");
					}

				} else if (pdu.isResponse() && pdu instanceof SubmitSMResp) {
					SubmitSMResp res = (SubmitSMResp) pdu;
					Lm.log().finest("[" + (i + 1) + "] Received submit SM response " + res.debugString());

					if (iListener != null)
						iListener.registerMessageID(res.getSequenceNumber(), res.getMessageId());
					else
						Lm.log().severe("Listener missing for SubmitResponse " + res.getSequenceNumber());
				} else if (pdu.isResponse() && pdu instanceof EnquireLinkResp) {
					// EnquireLinkResp res = (EnquireLinkResp) pdu;
					iEnquireSent = false;
					Lm.log().finest("[" + (i + 1) + "] EnquireLink response");
				} else {
					Lm.log().warning("[" + (i + 1) + "] Uprocessed message ");
				}
			}
			Lm.log().finest("[" + (i + 1) + "] Done...");

		}

		if (c > 0) {
			iLastCommReceived = System.currentTimeMillis();
		}

	}
	
	
 

}
