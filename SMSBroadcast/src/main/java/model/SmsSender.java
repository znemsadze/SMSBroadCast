package model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the SMS_SENDERS database table.
 * 
 */
@Entity
@Table(name="SMS_SENDERS")
@NamedQuery(name="SmsSender.findAll", query="SELECT s FROM SmsSender s")
public class SmsSender implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="CONTRACT_ID")
	private BigDecimal contractId;

	private String sender;

	public SmsSender() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getContractId() {
		return this.contractId;
	}

	public void setContractId(BigDecimal contractId) {
		this.contractId = contractId;
	}

	public String getSender() {
		return this.sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

}