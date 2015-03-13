package com.magti.SMSbroad;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.magti.SMSbroad.smpp.SMSLogica;
import com.magti.SMSbroad.utils.Lm;

import model.SmsQueue;
import model.SmsState;

public class Sender implements Runnable {

	private Boolean stopLoop = false;
	
	private List<SmsQueue> smsToSend;
	private SMSLogica smsLogica;
	
	public Sender(SMSLogica inSmsLogica){
		smsLogica=inSmsLogica;
	}
	
	@Override
	public void run() {
		while (!stopLoop) {
			System.out.println("fdsfsdfsdf" + this.toString());
	 
			try {
				
				execMessageSend();
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				 
				e.printStackTrace();
			} catch (Exception e) {
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		synchronized (ThreadManager.runThreads) {
			ThreadManager.runThreads--;
		}
	}

	public  void execMessageSend() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("orastore2");
		EntityManager em = emf.createEntityManager();
		try {
     		smsToSend= getNextForSend(em);
			em.getTransaction().begin();
			smsLogica.sendEnquire();
			for (SmsQueue sq : smsToSend) {
				SmsState smsState = new SmsState();
				smsState.setId(ThreadManager.STATUS_SUBMITTED);
				sq.setSmsState(smsState);
				em.merge(sq);
				smsLogica.submitMessageSimple((int)sq.getId(), sq.getPhoneNumber(), 
					sq.getSmsInfo().getSmsText(), sq.getSmsInfo().getSmsSender().getSender(),sq.getSmsInfo().getSmsCount(),sq.getSmsInfo().getIsGeo());
				System.out.println("send " +sq.getId()+"THREADID========="+Thread.currentThread().getId());

				Lm.log().severe(sq.getPhoneNumber() + " " + sq.getSmsInfo().getSmsText() + " "+sq.getSmsInfo().getSmsSender().getSender());
			}
			em.getTransaction().commit();
		} finally {
			em.close();
			emf.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static   synchronized List<SmsQueue> getNextForSend(EntityManager em){
		Query query = em.createQuery("select  t from SmsQueue t where t.smsState.id=700 order by t.id   ");
		query.setMaxResults(2);
		List<SmsQueue> result=query.getResultList();
		if(result!=null&&result.size()>0){
		Query query2= em.createQuery("Update SmsQueue t set "
			+ "t.smsState.id=2 where t.id between :stId and :ndId ");
		query2.setParameter("stId",result.get(0).getId() );
		query2.setParameter("ndId",result.get(result.size()-1).getId() );
		System.out.println("start===="+result.get(0).getId() +" "+Thread.currentThread().getId() );
		System.out.println("end==="+result.get(result.size()-1).getId() +" "+Thread.currentThread().getId() );
		
		em.getTransaction().begin();
		query2.executeUpdate();
		em.getTransaction().commit();
		}
		return result;
	}

	public void stop() {
		stopLoop = true;
	}
}
