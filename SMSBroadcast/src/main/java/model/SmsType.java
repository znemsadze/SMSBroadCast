package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the SMS_TYPES database table.
 * 
 */
@Entity
@Table(name="SMS_TYPES")
@NamedQuery(name="SmsType.findAll", query="SELECT s FROM SmsType s")
public class SmsType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String name;

	//bi-directional many-to-one association to SmsInfo
	@OneToMany(mappedBy="smsType")
	private List<SmsInfo> smsInfos;

	public SmsType() {
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

	public List<SmsInfo> getSmsInfos() {
		return this.smsInfos;
	}

	public void setSmsInfos(List<SmsInfo> smsInfos) {
		this.smsInfos = smsInfos;
	}

	public SmsInfo addSmsInfo(SmsInfo smsInfo) {
		getSmsInfos().add(smsInfo);
		smsInfo.setSmsType(this);

		return smsInfo;
	}

	public SmsInfo removeSmsInfo(SmsInfo smsInfo) {
		getSmsInfos().remove(smsInfo);
		smsInfo.setSmsType(null);

		return smsInfo;
	}

}