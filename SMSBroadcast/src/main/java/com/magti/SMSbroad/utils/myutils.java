package com.magti.SMSbroad.utils;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.bind.DatatypeConverter;

 


 
 
public class myutils {

	public static boolean strisemty(String s) {
		try {
			return ((s == null) || (s.trim().equals("")));
		} catch (Exception e) {
			return true;
		}

	}

	public static boolean checknumber(String s) {
		if (strisemty(s))
			return false;
		if (s.matches("[0-9]{1,9}"))
			return true;
		else
			return false;

	}

	public static Integer getOnesCount(Integer value1, Integer value2) {
		Integer k = (value1 == null ? 0 : value1) & (value2 == null ? 0 : value2);
		Integer s = 0;
		while (k > 0) {
			s = s + (k & 1);
			k = k >> 1;
		}
		return s;
	}

	public static String nillStr(String s) {
		if (s == null)
			return "";
		else
			return s;
	}

	public static boolean strIsValid(String s) {
		try {
			return s.matches("[0-9ა-ჰ.,-?!A-Z '$+#%/]{1,100}");
		} catch (Exception e) {
			return false;
		}

	}

	public static boolean strIsValidLong(String s) {
		try {
			return s.matches("[0-9ა-ჰ.,-? !A-Z'$+#%/]{1,1000}");
		} catch (Exception e) {
			return false;
		}

	}

	public static SimpleDateFormat myformat() {
		return new SimpleDateFormat("dd.MM.yyyy");
	}

	public static boolean checkPersonNo(String s) {
		if (s.matches("[0-9]{11}"))
			return true;
		else
			return false;

	}

	public static boolean checkindcode(String s) {
		if (s.matches("[0-9]{9}"))
			return true;
		else
			return false;

	}

	public static boolean checkArticle(String s) {
		if (s.matches("[0-9]{3}"))
			return true;
		else
			return false;

	}

	public static boolean checkPart(String s) {
		if (strisemty(s))
			return true;
		if (s.matches("[0-9]{0,2}'{0,4}"))
			return true;
		else
			return false;

	}

	public static boolean checkPunct(String s) {
		if (s.matches("[ა-ჰ0-9]{0,3}"))
			return true;
		else
			return false;

	}

	public static boolean checkPrima(String s) {
		if (s.matches("[0-9]{0,1}"))
			return true;
		else
			return false;

	}

	public static boolean checkSkcode(String s) {
		if (s.matches("[ა-ჰ]{0,3}[0-9]{1,11}"))
			return true;
		else
			return false;
	}

	public static boolean checklawcode(String s) {
		if (s.matches("[ა-ჰ]{0,3}[0-9]{0,11}"))
			return true;
		else
			return false;
	}

	public static boolean checkOrgCode(String s) {
		if (s.matches("[ა-ჰ]{0,2}[0-9]{1,9}"))
			return true;
		else
			return false;
	}

	public static boolean checkCardNum(String s) {
		if (s.matches("[ა-ჰ]{0,2}[0-9]{1,9}"))
			return true;
		else
			return false;
	}

	public static boolean checkOnlyFloat(String s) {
		if (s.matches("[0-9.]{1,9}"))
			return true;
		else
			return false;
	}

	public static boolean checkOnlyFloatu(String s) {
		if (s.matches("[-]{0,1}[0-9.]{1,9}"))
			return true;
		else
			return false;
	}

	public static Date strtodate(String s) {
		DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;
		try {
			date = (Date) formater.parse(s);

		} catch (Exception e) {
			try {
				date = (Date) formater.parse("01.01.1900");
			} catch (ParseException e1) {
			}
		}

		return date;
	}
	
	public static Date strToDateTm(String s) {
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = null;
		try {
			date = (Date) formater.parse(s);

		} catch (Exception e) {
			try {
				date = (Date) formater.parse("01.01.1900");
			} catch (ParseException e1) {
			}
		}

		return date;
	}

	public static String datetostr(Date date) {
		if (date == null)
			return "";
		DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
		return formater.format(date);
	}

	public static String datetostrOrd(Date date) {
		if (date == null)
			return "";
		DateFormat formater = new SimpleDateFormat("yyyy.MM.dd");
		return formater.format(date);
	}

	public static String dateToStrBar(Date date) {
		if (date == null)
			return "";
		DateFormat formater = new SimpleDateFormat("ddmmyy");
		return formater.format(date);
	}

	public static boolean dataIsReal(Date date) {
		DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
		try {
			if (date.before(formater.parse("02.01.1901")))
				return false;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static String dateToStrTm(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String sdt = "";
		try {
			sdt = sdf.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sdt;
	}

	public static String dateToStrTmOld(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String sdt = "";
		try {
			sdt = sdf.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sdt;
	}
	
	public static boolean dateisvalid(String s) {
		if (!strisemty(s)) {
			DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
			Date date = null;
			Calendar date1 = Calendar.getInstance();
			date1.add(Calendar.DATE, 1);
			try {
				date = (Date) formater.parse(s);
				if ((date.after(date1.getTime())) || (date.before(formater.parse("02.01.1900"))))
					return false;
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static boolean dateisvalidoremty(String s) {
		if (!strisemty(s)) {
			DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");

			try {
				@SuppressWarnings("unused")
				Date date = (Date) formater.parse(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public static String myMD5(String s) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(s.getBytes());
		byte byteData[] = md.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	public static String inttobarcode(Long value, Integer docType) {
		StringBuilder stb = new StringBuilder("0000000000000000");
		stb.replace(11, 16, dateToStrBar(Calendar.getInstance().getTime()));
		stb.replace(10 - Long.toString(value).length(), 10, Long.toString(value));
		stb.replace(2 - Integer.toString(docType).length(), 2, Integer.toString(docType));
		return stb.toString();
	}

 

	public static List<Integer> bitwiseToList(Integer k) {
		List<Integer> ls = new ArrayList<Integer>();
		for (int i = 0; i < 32; i++) {
			if ((k & (1 << i)) != 0)
				ls.add(i);
		}
		return ls;
	}

	public static Integer listToBitwise(List<Integer> l) {
		Integer i = 0;
		Integer s = 0;
		if (l.size() > 2) {
			i = 1;
			for (Integer k : l) {
				if (k < 100)
					s = s + (i << k);

			}
		}
		return s;
	}

	public static Integer moveFile(String dir1, String dir2) {
		File afile = new File(dir1);
		if (afile.exists()) {
			if (afile.renameTo(new File(dir2 + afile.getName())))
				return 1;
			else
				return -1;
		} else {
			return 0;
		}
	}

	public static String myMD5(byte[] s) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(s);
		byte byteData[] = md.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	public static Date getdate() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}

	public static String trans(String s, Integer v) {
		if (strisemty(s))
			return "";
		StringBuilder sb = new StringBuilder(s);
		StringBuilder sh1 = new StringBuilder();
		StringBuilder sh2 = new StringBuilder();
		switch (v) {
		case 12:
			sh1.insert(0, "ÀÁÂÃÄÅÆÈÉÊËÌÍÏÐÑÒÓÔÖ×ØÙÚÛÜÝÞßàáãä");
			sh2.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh");
			break;
		case 21:
			sh2.insert(0, "ÀÁÂÃÄÅÆÈÉÊËÌÍÏÐÑÒÓÔÖ×ØÙÚÛÜÝÞßàáãä");
			sh1.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh");
			break;
		case 13:
			sh1.insert(0, "ÀÁÂÃÄÅÆÈÉÊËÌÍÏÐÑÒÓÔÖ×ØÙÚÛÜÝÞßàáãä");
			sh2.insert(0, "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ");
			break;
		case 31:
			sh2.insert(0, "ÀÁÂÃÄÅÆÈÉÊËÌÍÏÐÑÒÓÔÖ×ØÙÚÛÜÝÞßàáãä");
			sh1.insert(0, "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ");
			break;
		case 23:
			sh1.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh");
			sh2.insert(0, "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ");
			break;
		case 32:
			sh2.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh");
			sh1.insert(0, "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ");
			break;
		case 44:
			sh1.insert(0, "აობვგღდჯძეჟზიკქყლმნპფრსთტუხცწჩჭშჰ");
			sh2.insert(0, "ააგდეეზზზკლმნოოორსტუუქღყყჩცძძჭჭჯჰ");
			break;
		default:
			break;
		}

		for (int i = 0; i < s.length(); i++) {
			if (sh1.indexOf(s.substring(i, i + 1)) > -1) {
				sb.replace(
						i,
						i + 1,
						sh2.substring(sh1.indexOf(sb.substring(i, i + 1)),
								sh1.indexOf(sb.substring(i, i + 1)) + 1));
			}

		}

		return sb.toString();

	}

 
 
	

	public static String getBase64String(String value){
		return DatatypeConverter.printBase64Binary(value.getBytes());
	}
	
	
	public static void main(String[] argv) throws Exception {
//     System.out.println( getBase64String("Op3SOAP:"+myMD5("p5HzF{N")));
		 
		System.out.println(dateToStrTm(Calendar.getInstance().getTime()));
		
		System.out.println( strToDateTm(dateToStrTm(Calendar.getInstance().getTime())));	}

	public static String nextDocNo(EntityManager em) {
		String result = "";
		String no = "0000000";
		Query query = em.createNativeQuery("select nextval('loan_no_seq') ");
		BigInteger seq = (BigInteger) query.getResultList().get(0);
		String seqstr = seq.toString();
		no = no.subSequence(0, no.length() - seqstr.length()) + seqstr;
		result += no;
		return result;
	}
 
 
	
}
