package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the SMS_QUEUE database table.
 * 
 */
@Entity
@Table(name="SMS_QUEUE")
@NamedQueries(
{@NamedQuery(name="SmsQueue.findAll", query="SELECT s FROM SmsQueue s"),
@NamedQuery(name="SmsQueue.getByMessageId", query="SELECT s FROM SmsQueue s where s.messageid=:msgId ")
})
public class SmsQueue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;
	
	@Column(name="MESSAGE_ID")
	private String messageid;

	@Column(name="PHONE_NUMBER")
	private String phoneNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SEND_DATE")
	private Date sendDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DELIVERY_DATE")
	private Date deliveryDate;
	
	
	//bi-directional many-to-one association to SmsInfo
	@ManyToOne
	@JoinColumn(name="SMS_INFO_ID")
	private SmsInfo smsInfo;



	//bi-directional many-to-one association to SmsState
	@ManyToOne
	@JoinColumn(name="STATE_ID")
	private SmsState smsState;
	
	
	

	public SmsQueue() {
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessageid() {
		return this.messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getSendDate() {
		return this.sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public SmsInfo getSmsInfo() {
		return this.smsInfo;
	}

	public void setSmsInfo(SmsInfo smsInfo) {
		this.smsInfo = smsInfo;
	}

	public SmsState getSmsState() {
		return this.smsState;
	}

	public void setSmsState(SmsState smsState) {
		this.smsState = smsState;
	}

}