package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the SMS_STATES database table.
 * 
 */
@Entity
@Table(name="SMS_STATES")
@NamedQuery(name="SmsState.findAll", query="SELECT s FROM SmsState s")
public class SmsState implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String name;

//	//bi-directional many-to-one association to SmsInfo
//	@OneToMany(mappedBy="smsState")
//	private List<SmsInfo> smsInfos;

	//bi-directional many-to-one association to SmsQueue
	@OneToMany(mappedBy="smsState")
	private List<SmsQueue> smsQueues;

	public SmsState() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public List<SmsInfo> getSmsInfos() {
//		return this.smsInfos;
//	}
//
//	public void setSmsInfos(List<SmsInfo> smsInfos) {
//		this.smsInfos = smsInfos;
//	}
//
//	public SmsInfo addSmsInfo(SmsInfo smsInfo) {
//		getSmsInfos().add(smsInfo);
//		smsInfo.setSmsState(this);
//
//		return smsInfo;
//	}
//
//	public SmsInfo removeSmsInfo(SmsInfo smsInfo) {
//		getSmsInfos().remove(smsInfo);
//		smsInfo.setSmsState(null);
//
//		return smsInfo;
//	}

	public List<SmsQueue> getSmsQueues() {
		return this.smsQueues;
	}

	public void setSmsQueues(List<SmsQueue> smsQueues) {
		this.smsQueues = smsQueues;
	}

	public SmsQueue addSmsQueue(SmsQueue smsQueue) {
		getSmsQueues().add(smsQueue);
		smsQueue.setSmsState(this);

		return smsQueue;
	}

	public SmsQueue removeSmsQueue(SmsQueue smsQueue) {
		getSmsQueues().remove(smsQueue);
		smsQueue.setSmsState(null);

		return smsQueue;
	}

}