package model;

import java.io.Serializable;

import javax.persistence.*;

import com.magti.SMSbroad.Sender;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the SMS_INFO database table.
 * 
 */
@Entity
@Table(name="SMS_INFO" )
@NamedQuery(name="SmsInfo.findAll", query="SELECT s FROM SmsInfo s")
public class SmsInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Temporal(TemporalType.DATE)
	@Column(name="SEND_DATE")
	private Date sendDate;



	@Column(name="SMS_TEXT")
	private String smsText;

	@Column(name="USER_ID")
	private BigDecimal userId;

//	//bi-directional many-to-one association to SmsState
//	@ManyToOne
//	@JoinColumn(name="STATE_ID")
//	private SmsState smsState;

	//bi-directional many-to-one association to SmsType
	@ManyToOne
	@JoinColumn(name="TYPE_ID")
	private SmsType smsType;

	//bi-directional many-to-one association to SmsQueue
	@OneToMany(mappedBy="smsInfo")
	private List<SmsQueue> smsQueues;

	
	@ManyToOne
	@JoinColumn(name="SMS_SENDER_ID")
	private SmsSender smsSender;
	
	@Column(name="SMS_COUNT")
	private Integer smsCount;
	
	@Column(name="IS_GEO")
	private Integer isGeo;


	public Integer getSmsCount() {
		return smsCount;
	}
	public void setSmsCount(Integer smsCount) {
		this.smsCount = smsCount;
	}
	public Integer getIsGeo() {
		return isGeo;
	}
	public void setIsGeo(Integer isGeo) {
		this.isGeo = isGeo;
	}
	public SmsInfo() {
	}
	public SmsSender getSmsSender() {
		return smsSender;
	}

	public void setSmsSender(SmsSender smsSender) {
		this.smsSender = smsSender;
	}
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getSendDate() {
		return this.sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

 

	public String getSmsText() {
		return this.smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public BigDecimal getUserId() {
		return this.userId;
	}

	public void setUserId(BigDecimal userId) {
		this.userId = userId;
	}

//	public SmsState getSmsState() {
//		return this.smsState;
//	}
//
//	public void setSmsState(SmsState smsState) {
//		this.smsState = smsState;
//	}

	public SmsType getSmsType() {
		return this.smsType;
	}

	public void setSmsType(SmsType smsType) {
		this.smsType = smsType;
	}

	public List<SmsQueue> getSmsQueues() {
		return this.smsQueues;
	}

	public void setSmsQueues(List<SmsQueue> smsQueues) {
		this.smsQueues = smsQueues;
	}

	public SmsQueue addSmsQueue(SmsQueue smsQueue) {
		getSmsQueues().add(smsQueue);
		smsQueue.setSmsInfo(this);

		return smsQueue;
	}

	public SmsQueue removeSmsQueue(SmsQueue smsQueue) {
		getSmsQueues().remove(smsQueue);
		smsQueue.setSmsInfo(null);

		return smsQueue;
	}

}