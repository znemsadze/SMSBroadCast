package com.magti.SMSbroad;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import model.SmsQueue;
import model.SmsState;

import org.smpp.ServerPDUEvent;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.PDU;
import org.smpp.pdu.Request;
import org.smpp.pdu.Response;
import org.smpp.pdu.SubmitSMResp;

import com.magti.SMSbroad.smpp.SMSLogica;
import com.magti.SMSbroad.smpp.SMSReceiver;
import com.magti.SMSbroad.utils.Lm;

public class Listener implements Runnable {

	private Boolean stopLoop = false;
	private SMSReceiver iReceiver;
	private boolean iWasConnected;
	private SMSLogica smsLogica;

	public Listener(SMSLogica inSmsLogica) {
		smsLogica = inSmsLogica;
	}

	@Override
	public void run() {
		iReceiver = smsLogica.iReceiver;
		while (!stopLoop) {

			try {
				System.out.println("Im listening"+Thread.currentThread().getId());
				smsLogica.sendEnquire();
				handleIncomingMessages();
				Thread.sleep(7000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		synchronized (ThreadManager.runThreads) {
			ThreadManager.runThreads--;
		}
	}

	public void stop() {
		stopLoop = true;
	}

	synchronized public void handleIncomingMessages() throws Exception {

		if (iReceiver == null) {

			if (iWasConnected)
				try {
					Thread.sleep(90000);
				} catch (InterruptedException intex) {

				}
			smsLogica.reBind();
		}

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("orastore2");
		EntityManager em = emf.createEntityManager();
		try {
			int c = iReceiver.getWaitingCount();
			System.out.println("waitcount========================= " + c);
			if (c > 0)
				Lm.log().finer("Polling for received messages... " + c + " found.");
			em.getTransaction().begin();
			for (int i = 0; i < c; i++) {
				ServerPDUEvent e = iReceiver.getRequestEvent(100);
				if (e != null) {
					PDU pdu = e.getPDU();
					if (pdu != null) {
						if (pdu.isRequest()) {
							// Handle request
							Lm.log().finest("[" + (i + 1) + "] Received request " + pdu.debugString());
							Response response = ((Request) pdu).getResponse();

							// respond with default response
							smsLogica.iSession.respond(response);
							Lm.log().finest("[" + (i + 1) + "]  Responded with " + response.debugString());

							// Decode request
							if (pdu instanceof DeliverSM) {
								DeliverSM req = ((DeliverSM) pdu);

								if (req.getMessageState() == 2) {
									SmsQueue smsQueue = getSmsQueueByMessageId(req.getReceiptedMessageId(), em);
									if (smsQueue != null) {
										SmsState smsState = em.find(SmsState.class, new Long(
											ThreadManager.STATUS_DELIVERED));
										smsQueue.setSmsState(smsState);
										smsQueue.setDeliveryDate(Calendar.getInstance().getTime());
										em.merge(smsQueue);
									}
									System.out.println("delevered " +req.getReceiptedMessageId()+"THREADID========="+Thread.currentThread().getId());
									Lm.log().finest("[" + (i + 1) + "]  DELIVERED for " + req.getReceiptedMessageId());
								} else if (req.getMessageState() > 2) {
									SmsQueue smsQueue = getSmsQueueByMessageId(req.getReceiptedMessageId(), em);
									if (smsQueue != null) {
										SmsState smsState = em.find(SmsState.class, new Long(
											ThreadManager.STATUS_FAILED));
										smsQueue.setSmsState(smsState);
										em.merge(smsQueue);
									}
									Lm.log().finest("[" + (i + 1) + "]  FAILED for " + req.getReceiptedMessageId());
								}
							} else {
								Lm.log().warning("[" + (i + 1) + "] Unrecognized request!");
							}

						} else if (pdu.isResponse() && pdu instanceof SubmitSMResp) {
							SubmitSMResp res = (SubmitSMResp) pdu;
							Lm.log().finest("[" + (i + 1) + "] Received submit SM response " + res.debugString());
							SmsQueue smsQueue = getSmsQueueById(res.getSequenceNumber(), em);
							SmsState smsState = em.find(SmsState.class, new Long(ThreadManager.STATUS_SEND));
							if (smsQueue.getMessageid() == null) {
								smsQueue.setSmsState(smsState);
								smsQueue.setMessageid(res.getMessageId());
								smsQueue.setSendDate(Calendar.getInstance().getTime());
								em.merge(smsQueue);
							}

							Lm.log().severe("Message send queueId=" + res.getSequenceNumber());
						} else if (pdu.isResponse() && pdu instanceof EnquireLinkResp) {
							// EnquireLinkResp res = (EnquireLinkResp) pdu;
							smsLogica.iEnquireSent = false;
							Lm.log().finest("[" + (i + 1) + "] EnquireLink response");
						} else {
							Lm.log().warning("[" + (i + 1) + "] Uprocessed message ");
						}
					}
				}
				Lm.log().finest("[" + (i + 1) + "] Done...");

			}
			em.getTransaction().commit();
			if (c > 0) {
				smsLogica.iLastCommReceived = System.currentTimeMillis();
			}
		} finally {
			em.close();
			emf.close();
		}
	}

	public SmsQueue getSmsQueueById(Integer queueId, EntityManager em) {
		SmsQueue smsQueue = em.find(SmsQueue.class, new Long(queueId));
		return smsQueue;
	}

	@SuppressWarnings("unchecked")
	public SmsQueue getSmsQueueByMessageId(String messageId, EntityManager em) {
		Query query = em.createNamedQuery("SmsQueue.getByMessageId");
		query.setParameter("msgId", messageId);
		List<SmsQueue> smsQueues = query.getResultList();
		if (smsQueues != null && smsQueues.size() > 0) {
			return smsQueues.get(0);
		} else {
			return null;
		}
	}
}
