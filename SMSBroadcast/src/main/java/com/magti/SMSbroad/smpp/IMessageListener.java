package com.magti.SMSbroad.smpp;

public interface IMessageListener
{
	public void registerSequenceID(int aSessionID, int aSubscriberID, String aPhone, int aSequenceID);
	public void registerMessageID(int aSequenceID, String aMessageID);
    public void submitReport(String aMessageID, int aStatusID);
}
