package com.magti.SMSbroad.utils;

import java.util.logging.*;

public class Lm
{
    private static Logger iLog = null;

    public static Logger log()
    {
        if ( iLog == null)
        {
            iLog = Logger.getLogger("smssec.log");
            iLog.setLevel(Level.ALL);
            try
            {
                FileHandler fh=new FileHandler("/opt/logs/smssec.log", 10000000, 10);
                fh.setLevel(Level.ALL);
                iLog.addHandler(fh);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            for (int i=0; i<iLog.getHandlers().length; i++)
            {
                iLog.getHandlers()[i].setFormatter(new ClassicFormatter());
                iLog.getHandlers()[i].setLevel(Level.ALL);
            }
        }
        return iLog;
    }
}
