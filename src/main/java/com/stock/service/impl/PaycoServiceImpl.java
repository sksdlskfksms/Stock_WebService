package com.stock.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.vo.PaymentCancelVO;
import com.stock.vo.PaymentVO;
import com.stock.vo.ReserveVO;
import com.stock.vo.UserVO;
import com.stock.vo.payco.PaycoReserveInfoReturnVO;
import com.stock.vo.payco.PaycoReserveInfoVO;
import com.stock.service.PaycoService;
import com.stock.util.Const;
import com.stock.util.OrderUtil;
import com.stock.util.PaycoUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class PaycoServiceImpl implements PaycoService {
    @Value("${domain}")
    private String domain;

    @Value("${paycoSellerKey}")
    private String sellerKey;

    @Value("${paycoCpId}")
    private String cpId;

    @Value("${paycoProductId}")
    private String productId;

    @Value("${paycologYn}")
    private String logYn;

    @Value("${payco.server.type}")
    private String serverType;

    @Value("${payco.return.url}")
    private String returnUrl;

    ObjectMapper mapper = new ObjectMapper();  //jackson json object


    /**-----------------------------------------------------------------------
     * 자동결제 예약처리
     *------------------------------------------------------------------------
     * @Class  autoPayment_reserve.jsp
     * @author PAYCO기술지원<dl_payco_ts@nhnent.com>
     * @since
     * @version
     * @Description
     *
     */
    @Transactional
    public String paycoReserve(UserVO userVO, ReserveVO reserveVO) throws Exception {
        String autoPaymentNo = "";
        String autoPaymentCertifyKey = "";

        if(reserveVO.getPaycoAutopaymentNo() != null){
            autoPaymentNo = reserveVO.getPaycoAutopaymentNo();
            autoPaymentCertifyKey = reserveVO.getPaycoAutopaymentCertifyKey();
        }

        final PaycoUtil util = new PaycoUtil(this.serverType);

        /* 외부가맹점의 자동결제 관리번호 */
        String sellerAutoPaymentReferenceKey = OrderUtil.getOrderCode();
        String returnUrlParam = "{\"reserveId\": \"" + userVO.getReserveId() + "\", \"userCid\": \"" + userVO.getUserCid() +
                        "\", \"name\": \"" + userVO.getName() + "\", \"phoneNum\": \"" + userVO.getPhoneNum() +
                        "\", \"mediaKey\": \"" + userVO.getMediaKey() + "\", \"mediaUserKey\": \"" + userVO.getMediaUserKey() + "\"}";

        //설정한 주문정보로 Json String 을 작성합니다.
        Map<String,Object> orderInfo = new HashMap<>();
        orderInfo.put("sellerKey", this.sellerKey);										//[필수]가맹점 코드
        orderInfo.put("cpId", this.cpId); 												//[필수]상점 ID
        orderInfo.put("sellerAutoPaymentReferenceKey", sellerAutoPaymentReferenceKey);  //[필수]외부가맹점의 자동결제 관리번호
        orderInfo.put("currency", "KRW");												//[선택]통화
        orderInfo.put("totalPaymentAmt", Const.FIX_AMOUNT);								//[선택]총 결제금액(자동결제 등록 페이지에 노출될 금액)
        orderInfo.put("returnUrl", this.returnUrl);				                        //[필수]등록 완료 후 Redirect 되는 Url
        orderInfo.put("returnUrlParam", returnUrlParam);                                //[선택]등록 완료 후 Redirect 되는 Url에 함께 전달되어야 하는 파라미터(Json 형태의 String)
        orderInfo.put("orderMethod", "EASYPAY");										//[필수]주문유형
        orderInfo.put("orderChannel", "PC");											//[필수]주문채널(default. PC)
        orderInfo.put("inAppYn", "N");													//[선택]인앱결제 여부

        // customUrlSchemeUseYn 옵션은 inAppYn=Y  일때 default 값을 변경하고 싶으신경우 사용합니다.
        // "Y"인 경우, 취소 및 결제완료등의 event가 custom url scheme로 전달됨
        orderInfo.put("customUrlSchemeUseYn", "N");										//[선택]custom url scheme 사용여부(inappyn=Y인 경우 default=Y)

        orderInfo.put("autoPaymentNo", autoPaymentNo);									//[선택]자동결제 번호 (자동결제 정보 변경시 전달)
        orderInfo.put("autoPaymentCertifyKey", autoPaymentCertifyKey); 					//[선택]자동결제 인증 키(자동결제 정보 변경시 전달)
        orderInfo.put("orderTitle", ""); 								                //[선택]주문 타이틀

        /* 부가정보(extraData) - Json 형태의 String */
        Map<String,Object> extraData = new HashMap<>();
        extraData.put("cancelMobileUrl", domain + "/auth/join/result?EncodeData=" + userVO.getEncodeData()); //[선택]모바일 결제페이지에서 취소 버튼 클릭시 이동할 URL (결제창 이전 URL 등). 미입력시 메인 URL로 이동

        Map<String,Object> viewOptions = new HashMap<>();
        viewOptions.put("showMobileTopGnbYn", "");										//[선택]모바일 상단 GNB 노출여부
        viewOptions.put("iframeYn", "N");												//[선택]Iframe 호출(모바일에서 접근하는경우 iframe 사용시 이값을 "Y"로 보내주셔야 합니다.)
        // Iframe 사용시 연동가이드 내용중 [Iframe 적용가이드]를 참고하시길 바랍니다.

        extraData.put("viewOptions", viewOptions);										//[선택]화면 UI 옵션

        orderInfo.put("extraData",  mapper.writeValueAsString(extraData).replaceAll("\"", "\\\""));	//[선택]부가정보 - Json 형태의 String

        //주문예약 API 호출 함수
        String result = util.autoPayment_reserve(orderInfo, this.logYn);
        JsonNode node = mapper.readTree(result);

        if(node.path("code").toString().equals("0")) {
            return node.path("result").get("orderSheetUrl").textValue();
        } else {
            log.error("---[페이코 자동결제 등록 실패]--------------------------------------------------");
            log.error("ResultCode = {}, ResultMsg = {}", node.path("code").toString(), node.path("message").toString());
            log.error("--------------------------------------------------------------------------");
            throw new Exception();
        }

    }


    /**-----------------------------------------------------------------------
     * 자동결제 예약 response data 처리 페이지(JSP)
     *------------------------------------------------------------------------
     * @Class  autoPayment_return.jsp
     * @author PAYCO기술지원<dl_payco_ts@nhnent.com>
     * @since
     * @version
     * @Description
     *
     */
    public PaycoReserveInfoReturnVO paycoFindReserveInfo(PaycoReserveInfoVO paycoReturnVO) throws Exception {
        paycoReturnVO.setSellerKey(this.sellerKey);

        /* 자동결제 예약 response data */
        String 	sellerAutoPaymentReferenceKey = paycoReturnVO.getSellerAutoPaymentReferenceKey();	//외부가맹점의 자동결제 관리번호
        String 	autoPaymentCertifyKey 		  = paycoReturnVO.getAutoPaymentCertifyKey();			//자동결제 인증 키(자동결제시 필요)

        /* 자동결제 예약 response data 값을 사용하여 자동결제 정보 API 호출 */
        Map<String, Object> info_param = new HashMap<>();
        info_param.put("sellerKey", sellerKey);
        info_param.put("sellerAutoPaymentReferenceKey", sellerAutoPaymentReferenceKey);
        info_param.put("autoPaymentCertifyKey", autoPaymentCertifyKey);

        final PaycoUtil util = new PaycoUtil(this.serverType);
        String result = util.autoPayment_info(info_param, this.logYn);
        JSONObject json = new JSONObject(result);

        if(json.getInt("code") == 0) {
            PaycoReserveInfoReturnVO paycoOrderRegistVO = new Gson().fromJson(Objects.toString(json.get("result")), PaycoReserveInfoReturnVO.class);
            return paycoOrderRegistVO;
        } else{
            log.error("---[페이코 자동결제 등록정보 조회 실패]------------------------------------------");
            log.error("ResultCode = {}, ResultMsg = {}", json.getInt("code"), json.getString("message"));
            log.error("--------------------------------------------------------------------------");
            throw new Exception();
        }

    }


    /**-----------------------------------------------------------------------
     * 자동결제 신청
     *------------------------------------------------------------------------
     * @author PAYCO기술지원<dl_payco_ts@nhnent.com>
     * 자동결제 예약정보를 이용해 자동결제 신청 페이코API 와 통신한다.
     */
    public PaymentVO paycoPayment(ReserveVO reserveVO, String orderCode, int amount) throws Exception {
        final PaycoUtil util = new PaycoUtil(this.serverType);

        String autoPaymentCertifyKey   = reserveVO.getPaycoAutopaymentCertifyKey();
        String autoPaymentNo   		   = reserveVO.getPaycoAutopaymentNo();

        /* 상품정보 변수선언 */
        String taxationType = "DUTYFREE";
        int productPaymentAmt = amount;

        /* 주문 상품 리스트 정보*/
        List<Map<String,Object>> orderProducts = new ArrayList<>();

        Map<String,Object> productInfo = new HashMap<>();
        productInfo.put("cpId", this.cpId);									        //[필수]상점ID
        productInfo.put("productId", this.productId);						        //[필수]상품ID
        productInfo.put("productAmt", productPaymentAmt);					        //[필수]상품금액(상품단가 * 수량)
        productInfo.put("productPaymentAmt", productPaymentAmt);			        //[필수]상품결제금액(상품결제단가 * 수량)
        productInfo.put("orderQuantity", 1);								        //[필수]주문수량(배송비 상품인 경우 1로 셋팅)
        productInfo.put("sortOrdering", 1);									        //[필수]상품 노출순서
        productInfo.put("productName", "");						                    //[필수]상품명
        productInfo.put("orderConfirmUrl", "");								        //[선택]주문완료 후 주문상품을 확인할 수 있는 URL
        productInfo.put("orderConfirmMobileUrl", ""); 						        //[선택]주문완료 후 주문상품을 확인할 수 있는 모바일 URL
        productInfo.put("productImageUrl", "");								        //[선택]이미지 URL(배송비 상품이 아닌 경우는 필수)
        productInfo.put("sellerOrderProductReferenceKey", "PRODUCT_KEY");	        //[필수]외부가맹점에서 관리하는 주문상품 연동 키
        productInfo.put("taxationType", taxationType);						        //[선택]과세타입(면세상품 : DUTYFREE, 과세상품 : TAXATION (기본), 결합상품 : COMBINE)
        orderProducts.add(productInfo);

        /* 설정한 주문정보로 Json String 을 작성합니다. */
        Map<String,Object> paymentInfo = new HashMap<>();
        paymentInfo.put("sellerKey", this.sellerKey);							    //[필수]가맹점 코드
        paymentInfo.put("autoPaymentNo", autoPaymentNo); 						    //[필수]자동결제 번호
        paymentInfo.put("autoPaymentCertifyKey", autoPaymentCertifyKey);  		    //[필수]자동결제 인증 키
        paymentInfo.put("sellerOrderReferenceKey", orderCode);	                    //[필수]외부가맹점의 주문번호
        paymentInfo.put("sellerOrderReferenceKeyType", "UNIQUE_KEY");	            //[선택]외부가맹점의 주문번호 타입
        paymentInfo.put("currency", "KRW");										    //[선택]통화
        paymentInfo.put("totalPaymentAmt", productPaymentAmt);			            //[필수]총 결제 금액
        paymentInfo.put("totalTaxfreeAmt", 0);					                    //[선택]총 면세금액
        paymentInfo.put("totalTaxableAmt", 0);					                    //[선택]총 과세금액
        paymentInfo.put("totalVatAmt", 0);							                //[선택]총 부가세 금액
        paymentInfo.put("orderTitle", "");						                    //[선택]주문 타이틀
        paymentInfo.put("orderProducts", orderProducts);						    //[선택]주문상품 리스트


        //자동결제 API 호출 함수
        String result = util.autoPayment_payment(paymentInfo, this.logYn);
        JsonNode node = mapper.readTree(result);
        String resultCode = node.path("code").toString();
        String resultMsg = node.path("message").textValue();

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setReserveId(reserveVO.getId());
        paymentVO.setOrderCode(orderCode);
        paymentVO.setPaymentAmount(amount);
        paymentVO.setPgName(Const.PG.PAYCO.name());

        if(node.path("code").toString().equals("0")){
            paymentVO.setSuccessResult();
            paymentVO.setPaycoOrderNo(node.path("result").get("orderNo").textValue());
            paymentVO.setPaycoOrderCertifyKey(node.path("result").get("orderCertifyKey").textValue());
            paymentVO.setCompleteYmdt(node.path("result").get("paymentCompleteYmdt").textValue());
        }else{
            log.error("---[페이코 결제 실패]--------------------------------------------------------");
            log.error("ResultCode = {}, ResultMsg = {}, reserveId = {}", resultCode, resultMsg, paymentVO.getReserveId());
            log.error("--------------------------------------------------------------------------");

            paymentVO.setResultCode(resultCode);
            paymentVO.setResultMessage(resultMsg);
        }
        return paymentVO;

    }


    /**-----------------------------------------------------------------------
     * 자동결제 취소(JSP)
     *------------------------------------------------------------------------
     * @Class  autoPayment_paymentCancel.jsp
     * @author PAYCO기술지원<dl_payco_ts@nhnent.com>
     * @since
     * @version
     * @Description
     *
     */
    @Override
    public PaymentCancelVO paycoRefund(PaymentCancelVO paymentCancelVO, String certifyKey) throws Exception {
        final PaycoUtil util = new PaycoUtil(this.serverType);

        String orderNo 							 = paymentCancelVO.getPaycoOrderNo();
        String orderCertifyKey   		 		 = certifyKey;
        String cancelTotalAmt   		 		 = Integer.toString(paymentCancelVO.getRefundAmount());


        /* 설정한 취소요청 정보로 Json String 을 작성합니다. */
        Map<String,Object> cancelInfo = new HashMap<String,Object>();
        cancelInfo.put("sellerKey", sellerKey);						//[필수]가맹점 코드
        cancelInfo.put("orderNo", orderNo); 						//[필수]주문번호 자동결제 관리번호
        cancelInfo.put("orderCertifyKey", orderCertifyKey);  		//[필수]주문인증 key
        cancelInfo.put("cancelTotalAmt", cancelTotalAmt);  			//[필수]취소할 총 금액
        cancelInfo.put("requestMemo", "");							//[선택]취소처리 요청메모

        //자동결제 취소 API 호출 함수
        String result = util.payco_cancel(cancelInfo,logYn);
        JsonNode node = mapper.readTree(result);

        String resultCode = node.path("code").toString();
        String resultMsg = node.path("message").textValue();


        if(resultCode.equals("0")) {
            paymentCancelVO.setRefundSuccessResult();
            paymentCancelVO.setCompleteYmdt(node.path("result").get("cancelYmdt").textValue());
        }else{
            log.error("---[페이코 결제 취소 실패]----------------------------------------------------");
            log.error("ResultCode = {}, ResultMsg = {}, reserveId = {}", resultCode, resultMsg, paymentCancelVO.getReserveId());
            log.error("--------------------------------------------------------------------------");

            paymentCancelVO.setRefundResultCode(resultCode);
            paymentCancelVO.setRefundResultMessage(resultMsg);
        }
        return paymentCancelVO;

    }


    /**-----------------------------------------------------------------------
     * 자동결제 삭제(JSP)
     *------------------------------------------------------------------------
     * @Class  autoPayment_cancel.jsp
     * @author PAYCO기술지원<dl_payco_ts@nhnent.com>
     * @since
     * @version
     * @Description
     *
     */
    @Override
    public PaymentCancelVO paycoDeleteInfo(PaymentCancelVO paymentCancelVO, String certifyKey) throws Exception {
        PaycoUtil util = new PaycoUtil(serverType);

        String sellerAutoPaymentReferenceKey = paymentCancelVO.getOrderCode();
        String autoPaymentCertifyKey   		 = certifyKey;


        /* 설정한 취소요청 정보로 Json String 을 작성합니다. */
        Map<String,Object> cancelInfo = new HashMap<>();
        cancelInfo.put("sellerKey", sellerKey);											//[필수]판매자Key
        cancelInfo.put("sellerAutoPaymentReferenceKey", sellerAutoPaymentReferenceKey); //[필수]외부가맹점의 자동결제 관리번호
        cancelInfo.put("autoPaymentCertifyKey", autoPaymentCertifyKey);  				//[필수]자동결제 인증 키


        //자동결제 삭제 API 호출 함수
        String result = util.autoPayment_cancel(cancelInfo,logYn);
        JsonNode node = mapper.readTree(result);

        String resultCode = node.path("code").toString();
        String resultMsg = node.path("message").textValue();

        if(resultCode.equals("0")) {
            paymentCancelVO.setDeleteSuccessResult();
        }else{
            log.error("----[페이코 자동결제 정보 삭제 실패]--------------------------------------------");
            log.error("ResultCode = {}, ResultMsg = {}, reserveId = {}", resultCode, resultMsg, paymentCancelVO.getReserveId());
            log.error("--------------------------------------------------------------------------");

            paymentCancelVO.setDeleteResultCode(resultCode);
            paymentCancelVO.setDeleteResultMessage(resultMsg);
        }
        return paymentCancelVO;

    }

}
