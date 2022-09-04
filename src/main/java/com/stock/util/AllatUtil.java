package com.stock.util;


import javax.net.ssl.*;
import javax.net.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;



public class AllatUtil {

  ///Fields

  private static final String util_lang = "JSP";
  private static final String util_ver  = "1.0.7.1";

  private static final String approval_uri          = "/servlet/AllatPayUtf8/pay/approval.jsp";
  private static final String sanction_uri          = "/servlet/AllatPayUtf8/pay/sanction.jsp";
  private static final String cancel_uri            = "/servlet/AllatPayUtf8/pay/cancel.jsp";
  private static final String cashreg_uri           = "/servlet/AllatPayUtf8/pay/cash_registry.jsp";
  private static final String cashapp_uri           = "/servlet/AllatPayUtf8/pay/cash_approval.jsp";
  private static final String cashcan_uri           = "/servlet/AllatPayUtf8/pay/cash_cancel.jsp";
  private static final String escrowchk_uri         = "/servlet/AllatPayUtf8/pay/escrow_check.jsp";
  private static final String escrowret_uri         = "/servlet/AllatPayUtf8/pay/escrow_return.jsp";
  private static final String escrowconfirm_uri     = "/servlet/AllatPayUtf8/pay/escrow_confirm.jsp";
  private static final String certreg_uri           = "/servlet/AllatPayUtf8/pay/fix.jsp";
  private static final String certcancel_uri        = "/servlet/AllatPayUtf8/pay/fix_cancel.jsp";

  private static final String c2c_approval_uri      = "/servlet/AllatPayUtf8/pay/c2c_approval.jsp";
  private static final String c2c_cancel_uri        = "/servlet/AllatPayUtf8/pay/c2c_cancel.jsp";
  private static final String c2c_sellerreg_uri     = "/servlet/AllatPayUtf8/pay/seller_registry.jsp";
  private static final String c2c_productreg_uri    = "/servlet/AllatPayUtf8/pay/product_registry.jsp";
  private static final String c2c_buyerchg_uri      = "/servlet/AllatPayUtf8/pay/buyer_change.jsp";
  private static final String c2c_escrowchk_uri     = "/servlet/AllatPayUtf8/pay/c2c_escrow_check.jsp";
  private static final String c2c_escrowconfirm_uri = "/servlet/AllatPayUtf8/pay/c2c_escrow_confirm.jsp";
  private static final String c2c_esrejectcheck_uri = "/servlet/AllatPayUtf8/pay/c2c_reject_check.jsp";
  private static final String c2c_expressreg_uri    = "/servlet/AllatPayUtf8/pay/c2c_express_reg.jsp";

  private static final String allat_host      = "tx.allatpay.com";

  private static final String utilVer         = "&opt_lang=" +util_lang+ "&opt_lang_ver=" + util_ver;

  ///Constructor

  ///Method
  public HashMap approvalReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=true;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, approval_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, approval_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap sanctionReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, sanction_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, sanction_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    } finally {
      return retHm;
    }
  }

  public HashMap cancelReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, cancel_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, cancel_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap cashregReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, cashreg_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, cashreg_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap cashappReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, cashapp_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, cashapp_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap cashcanReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, cashcan_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, cashcan_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap escrowchkReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host,escrowchk_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, escrowchk_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap escrowRetReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, escrowret_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, escrowret_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap escrowConfirmReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host,escrowconfirm_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, escrowconfirm_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  //////////////////////
  public HashMap CertRegReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, certreg_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, certreg_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }
  public HashMap CertCancelReq(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, certcancel_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, certcancel_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap approvalReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, c2c_approval_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_approval_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap cancellReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, c2c_cancel_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_cancel_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap sellerRegReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, c2c_sellerreg_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_sellerreg_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap productRegReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, c2c_productreg_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_productreg_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap buyerChkReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, c2c_buyerchg_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_buyerchg_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap escrowChkReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host, c2c_escrowchk_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_escrowchk_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap escrowConfirmReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host,c2c_escrowconfirm_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_escrowconfirm_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap esRejectCheckReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host,c2c_esrejectcheck_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_esrejectcheck_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  public HashMap expressregReqC2C(String strReq, String sslFlag){
    HashMap retHm=null;
    boolean isEnc=false;
    try {
      if( sslFlag.equals("SSL") ){
        retHm=SendRepo( strReq, allat_host,c2c_expressreg_uri, 443 );
      }else{
        isEnc=checkEnc( strReq );
        if ( isEnc ){
          retHm=SendRepo( strReq, allat_host, c2c_expressreg_uri, 80 );
        }else{
          return retHm=getValue("reply_cd=0230\nreply_msg=암호화 오류\n");
        }
      }
    } catch (Exception e){
      retHm=getValue("reply_cd=0221\nreply_msg=Exception : "+e.getMessage()+"\n");
    }
    return retHm;
  }

  ////////////////////////
  private HashMap SendRepo( String srpReq, String srpHost, String srpUri, int srpPort){
    HashMap  retHm=null;
    String retTxt=sendReq(srpReq, srpHost, srpUri, srpPort);
    retHm=getValue(retTxt);
    return retHm;
  }

  ////--------------------------Connect And Client Data Send----------------------------------------
  private String sendReq(String sendMsg, String host,  String uri, int port){
    String sendstr = "";
    String headerStr = "";
    String result= "";
    String getstr = "";

    // 443 일 때
    SSLSocketFactory sslFactory = null;
    SSLSocket sslSoc = null;

    // 80 일 떄
    SocketFactory sFactory = null;
    Socket sSoc = null;

    // 공통
    PrintWriter sOut = null;
    BufferedReader sIn = null;

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String sApplyTime="&allat_apply_ymdhms="+formatter.format(cal.getTime());
    String sUtilVer="&allat_opt_lang="+util_lang+"&allat_opt_ver="+util_ver;
    sendMsg=sendMsg+sUtilVer+sApplyTime;
    try {
      if ( port == 80 ) {       // 80 Port 일 때
        sFactory = SocketFactory.getDefault();
        sSoc = (Socket)sFactory.createSocket( InetAddress.getByName(host) , port);
        sOut = new PrintWriter(sSoc.getOutputStream(), true);
        sIn = new BufferedReader(new InputStreamReader(sSoc.getInputStream(), "euc-kr"));
      } else {      // 80 이외에는 443 Port 로 통신
        sslFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        sslSoc = (SSLSocket)sslFactory.createSocket(host, port);
        sOut = new PrintWriter(sslSoc.getOutputStream(), true);
        sIn  = new BufferedReader(new InputStreamReader(sslSoc.getInputStream(), "euc-kr"));
      }

    } catch (UnknownHostException uhe) {
      result = "reply_cd=0221\nreply_msg=Exception: "+uhe.getMessage()+"\n";
      return result;
    } catch (IOException ioe) {
      result = "reply_cd=0221\nreply_msg=Exception:"+ioe.getMessage()+"\n";
      return result;
    } catch (Exception e) {
      result = "reply_cd=0221\nreply_msg=Exception:"+e.getMessage()+"\n";
      return result;
    }

    try {
      byte[] reqbyte = sendMsg.getBytes();
      int reqlen = reqbyte.length;

      sendstr = "POST " + uri + " HTTP/1.0\r\n";
      sendstr += "Host: "+ host + ":" + port + "\r\n";
      sendstr += "Content-type: application/x-www-form-urlencoded\r\n";
      sendstr += "Content-length: " + reqlen + "\r\n";
      sendstr += "Accept: */*\r\n";
      sendstr += "\r\n";
      sendstr += sendMsg+"\r\n";
      sendstr += "\r\n";
      sOut.println(sendstr);

      while( (getstr = sIn.readLine()).length() != 0 ) {
        headerStr += getstr+"\n";
      }

      while((getstr = sIn.readLine()) != null ) {
        result += getstr+"\n";
      }
    } catch (Exception e) {
      result = "reply_cd=0212\n"+"reply_msg=Socket Connect Error:"+e.getMessage()+"\n";
      return result;
    } finally {
      try {
        if(sOut != null) sOut.close();
        if(sIn != null) sIn.close();
        if(sslSoc != null) sslSoc.close();  // 443
        if(sSoc != null ) sSoc.close();
      } catch (Exception ef) {}
    }
    return result;
  }

  ////-----------------------Result Text Convert HashMap---------------------------------------
  private HashMap getValue(String sText){
    HashMap retHm=new HashMap();
    String sArg1=null;
    String sArg2=null;

    StringTokenizer fstTk=new StringTokenizer(sText,"\n");
    while(fstTk!=null&&fstTk.hasMoreTokens()){
      String tmpTk=fstTk.nextToken();
      StringTokenizer secTk=new StringTokenizer(tmpTk,"=");
      for (int i=0; i<2; i++) {
        if (i==0) {
          if (secTk.hasMoreTokens()) sArg1 = secTk.nextToken().trim();
          else sArg1 = "";
        } else {
          if (secTk.hasMoreTokens()) sArg2 = secTk.nextToken().trim();
          else sArg2 = "";
        }
      }
      retHm.put(sArg1,sArg2);
    }
    if (retHm.get("reply_cd")==null) {
      retHm.put("reply_cd","0299");
      retHm.put("reply_msg",sText);
    }
    return retHm;
  }

  private boolean checkEnc(String srcStr){
    int ckIdx;

    ckIdx=srcStr.indexOf("allat_enc_data=");

    if( ckIdx == -1){
      return false;
    } else {
      ckIdx += "allat_enc_data=".length()+5;
    }
    if( (srcStr.substring(ckIdx,ckIdx+1)).equals("1") ){
      return true;
    }else{
      return false;
    }
  }

  public String setValue(HashMap hm){
    String formData="";
    int i=0;
    boolean bFirst=false;
    if(hm==null){
      formData=null;
      return formData;
    }
    Iterator ir  = hm.keySet().iterator();
    while(ir.hasNext()){
      String sKey=(String)ir.next();
      String sValue=(String)hm.get(sKey);
      if(bFirst){
        formData+=sKey+""+sValue+"";
      }else{
        formData+="00000010"+sKey+""+sValue+"";
        bFirst=true;
      }
    }
    return formData;
  }
}