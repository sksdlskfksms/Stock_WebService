package com.stock.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**-----------------------------------------------------------------------
 * 페이코 자동결제 연동 유틸리티
 *------------------------------------------------------------------------
 * @Class  AutoPayment_Util.java
 * @author PAYCO기술지원<dl_payco_ts@nhnent.com>
 * @since
 * @version
 * @Description
 */

public class PaycoUtil {

    private static final String FILE_PATH   = "C:/logs";

    private String RESERVE_URI    			= "";
    private String INFO_URI					= "";
    private String PAYMENT_URI				= "";
    private String DELETE_URI      			= "";
    private String CANCEL_URI      			= "";

    //생성자
    public PaycoUtil(String serverType){

        if(serverType.equals("DEV")){
            RESERVE_URI 		= "https://alpha-api-bill.payco.com/outseller/autoPayment/reserve";
            INFO_URI			= "https://alpha-api-bill.payco.com/outseller/autoPayment/info";
            PAYMENT_URI 		= "https://alpha-api-bill.payco.com/outseller/autoPayment/payment";
            DELETE_URI 			= "https://alpha-api-bill.payco.com/outseller/autoPayment/delete";
            CANCEL_URI 			= "https://alpha-api-bill.payco.com/outseller/order/cancel";
        } else {
            RESERVE_URI 		= "https://api-bill.payco.com/outseller/autoPayment/reserve";
            INFO_URI			= "https://api-bill.payco.com/outseller/autoPayment/info";
            PAYMENT_URI 		= "https://api-bill.payco.com/outseller/autoPayment/payment";
            DELETE_URI 			= "https://api-bill.payco.com/outseller/autoPayment/delete";
            CANCEL_URI 			= "https://api-bill.payco.com/outseller/order/cancel";
        }
    }

    ObjectMapper mapper = new ObjectMapper();
    java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss");

    /**
     * 자동결제 등록요청
     * @param map
     * @param logYn : Y/N
     * @return
     */
    public String autoPayment_reserve(Map<String, Object> map, String logYn){

        String returnStr = "";

        try {

            returnStr = getSSLConnection( RESERVE_URI, mapper.writeValueAsString(map));

            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][자동결제 등록요청] " +"[callUrl :" + RESERVE_URI +" ] " + mapper.writeValueAsString(map), logYn);
            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][자동결제 등록요청 결과] " + returnStr, logYn);

        } catch (Exception e){
            e.printStackTrace();
            returnStr = "{\"code\":\"9999\",\"message\":\""+e.getMessage()+"\"}";
        }
        return returnStr;
    }

    /**
     * 자동결제 정보조회
     * @param map
     * @param logYn : Y/N
     * @return
     */
    public String autoPayment_info(Map<String, Object> map, String logYn){

        String returnStr = "";

        try {
            returnStr = getSSLConnection( INFO_URI, mapper.writeValueAsString(map));

            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][자동결제 정보조회] " +"[callUrl :" + INFO_URI +" ] " + mapper.writeValueAsString(map), logYn);
            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][자동결제 정보조회 결과] " + returnStr, logYn);
        } catch (Exception e){
            e.printStackTrace();
            returnStr = "{\"code\":\"9999\",\"message\":\""+e.getMessage()+"\"}";
        }
        return returnStr;
    }

    /**
     * 자동결제 결제요청
     * @param map
     * @param logYn : Y/N
     * @return
     */
    public String autoPayment_payment(Map<String, Object> map, String logYn){
        String returnStr = "";

        try {
            returnStr = getSSLConnection( PAYMENT_URI, mapper.writeValueAsString(map));

            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][자동결제 결제요청] " +"[callUrl :" + PAYMENT_URI +" ] " + mapper.writeValueAsString(map), logYn);
            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][자동결제 결제요청 결과] " + returnStr, logYn);
        } catch (Exception e){
            e.printStackTrace();
            returnStr = "{\"code\":\"9999\",\"message\":\""+e.getMessage()+"\"}";
        }

        return returnStr;
    }

    /**
     * 자동결제 삭제요청
     * @param map
     * @param logYn : Y/N
     * @return
     */
    public String autoPayment_cancel(Map<String, Object> map, String logYn){
        String returnStr = "";

        try {
            returnStr = getSSLConnection( DELETE_URI, mapper.writeValueAsString(map));

            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][자동결제 삭제요청] " +"[callUrl :" + DELETE_URI +" ] " + mapper.writeValueAsString(map), logYn);
            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][자동결제 삭제요청 결과] " + returnStr, logYn);
        } catch (Exception e){
            e.printStackTrace();
            returnStr = "{\"code\":\"9999\",\"message\":\""+e.getMessage()+"\"}";
        }

        return returnStr;
    }

    /**
     * 주문취소
     * @param map
     * @param logYn : Y/N
     * @return
     */
    public String payco_cancel(Map<String, Object> map, String logYn){
        String returnStr = "";

        try {
            returnStr = getSSLConnection( CANCEL_URI, mapper.writeValueAsString(map));

            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][주문취소요청] " +"[callUrl :" + CANCEL_URI +" ] " + mapper.writeValueAsString(map), logYn);
            makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][주문취소결과] " + returnStr, logYn);
        } catch (Exception e){
            e.printStackTrace();
            returnStr = "{\"code\":\"9999\",\"message\":\""+e.getMessage()+"\"}";
        }

        return returnStr;
    }


    public String getConnection(String apiUrl, String arrayObj) throws Exception{

        URL url 			  = new URL(apiUrl); 	// 요청을 보낸 URL
        String sendData 	  = arrayObj;
        HttpURLConnection con = null;
        StringBuffer buf 	  = new StringBuffer();
        String returnStr 	  = "";

        try {
            con = (HttpURLConnection)url.openConnection();

            con.setConnectTimeout(30000);		//서버통신 timeout 설정. 페이코 권장 30초
            con.setReadTimeout(30000);			//스트림읽기 timeout 설정. 페이코 권장 30초

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.connect();

            // 송신할 데이터 전송.
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.write(sendData.getBytes("UTF-8"));
            dos.flush();
            dos.close();

            int resCode = con.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                int c;

                while ((c = br.read()) != -1) {
                    buf.append((char)c);
                }

                returnStr = buf.toString();
                br.close();

            } else {
                returnStr = "{ \"code\" : 9999, \"message\" : \"Connection Error\" }";
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return returnStr;
    }

    public String getSSLConnection(String apiUrl, String arrayObj) throws Exception{

        URL url 			   = new URL(apiUrl); 	// 요청을 보낸 URL
        String sendData 	   = arrayObj;
        HttpsURLConnection con = null;
        StringBuffer buf 	   = new StringBuffer();
        String returnStr 	   = "";

        try {
            con = (HttpsURLConnection)url.openConnection();

            con.setConnectTimeout(30000);		//서버통신 timeout 설정. 페이코 권장 30초
            con.setReadTimeout(30000);			//스트림읽기 timeout 설정. 페이코 권장 30초

            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.connect();
            // 송신할 데이터 전송.
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.write(sendData.getBytes("UTF-8"));
            dos.flush();
            dos.close();
            int resCode = con.getResponseCode();
            if (resCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                int c;

                while ((c = br.read()) != -1) {
                    buf.append((char)c);
                }
                returnStr = buf.toString();
                br.close();
            } else {
                returnStr = "{ \"code\" : 9999, \"message\" : \"Connection Error\" }";
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return returnStr;
    }

    public static void makeServiceCheckApiLogFile(String logText, String logYn) {

        if(logYn.equals("Y")){
            String filePath   = FILE_PATH;
            java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss");
            String nowTotDate = dateformat.format(new java.util.Date());
            Integer nowdate = Integer.parseInt( nowTotDate.substring(0, 8) );

            String fileName = "service_check_log_" + nowdate + ".txt"; //생성할 파일명
            String logPath = filePath + File.separator + fileName;

            File folder = new File(filePath); //로그저장폴더
            File f 		= new File(logPath);  //파일을 생성할 전체경로

            try{

                if(folder.exists() == false) {
                    folder.mkdirs();
                }

                if (f.exists() == false){
                    f.createNewFile(); //파일생성
                }

                // 파일쓰기
                FileWriter fw = null;

                try {

                    fw = new FileWriter(logPath, true); //파일쓰기객체생성
                    fw.write(logText +"\n"); //파일에다 작성

                } catch(IOException e) {
                    throw e;
                } finally {
                    if(fw != null) fw.close(); //파일핸들 닫기
                }

            }catch (IOException e) {
                System.out.println(e.toString()); //에러 발생시 메시지 출력
                e.printStackTrace();
            }
        }else{
            return;
        }
    }

}