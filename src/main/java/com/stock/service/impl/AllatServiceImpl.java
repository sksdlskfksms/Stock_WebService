package com.stock.service.impl;

import com.stock.vo.PaymentCancelVO;
import com.stock.vo.PaymentVO;
import com.stock.vo.ReserveVO;
import com.stock.vo.UserVO;
import com.stock.vo.allat.AllatApproveReqVO;
import com.stock.mapper.ReserveMapper;
import com.stock.service.AllatService;
import com.stock.service.PaymentService;
import com.stock.util.AllatUtil;
import com.stock.util.Const;
import com.stock.util.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AllatServiceImpl implements AllatService {

    @Value("${allatShopId}")
    private String allatShopId;

    @Value("${allatCrossKey}")
    private String allatCrossKey;

    @Value("${allAtTestYn}")
    private String allAtTestYn;

    @Value("${allAtRedirectUrl}")
    private String allAtRedirectUrl;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReserveMapper reserveMapper;

    private AllatUtil util = new AllatUtil();


    /**
     * 올앳 자동결제 등록 정보 생성
     * @param userVO
     */
    @Override
    public Map<String, String> allatPaymentRequestParam(UserVO userVO) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("allat_shop_id", this.allatShopId);
        resultMap.put("allat_order_no", OrderUtil.getOrderCode());
        resultMap.put("allat_amt", Integer.toString(this.paymentService.calculateAmount(Const.JoinStatus.JOIN, null)));
        resultMap.put("shop_receive_url", this.allAtRedirectUrl);  // 로컬 URL은 오류 발생
        resultMap.put("allat_test_yn", this.allAtTestYn);
        resultMap.put("reserveId", Long.toString(userVO.getReserveId()));

        return resultMap;

    }


    /**
     * 올앳 자동결제 삭제 요청 정보 생성
     * @param userCid
     */
    @Override
    public Map<String, String> allatCancelRequestParam(String userCid) {
        ReserveVO reserveVO = this.reserveMapper.selectReserveByUserCid(userCid);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("allat_shop_id", this.allatShopId);
        resultMap.put("allat_fix_key", reserveVO.getAllatFixKey());
        resultMap.put("shop_receive_url", this.allAtRedirectUrl);  // 로컬 URL은 오류 발생
        resultMap.put("allat_test_yn", this.allAtTestYn);

        return resultMap;

    }


    /**
     * 암호화 데이터 승인 전달
     * @param allatEncData
     */
    @Override
    public AllatApproveReqVO allatPaymentRequest(String allatEncData) throws Exception {
        // 요청 데이터 설정
        //----------------------
        String strReq = "";
        strReq = "allat_shop_id=" + this.allatShopId;
        strReq += "&allat_enc_data=" + allatEncData;
        strReq += "&allat_cross_key=" + this.allatCrossKey;


        // 올앳 결제 서버와 통신  : AllatUtil.approvalReq->통신함수, HashMap->결과값
        //-----------------------------------------------------------------------------
        HashMap hm  = util.CertRegReq(strReq, "SSL");

        // 결제 결과 값 확인
        //------------------
        AllatApproveReqVO allatApproveReqVO = new AllatApproveReqVO();
        allatApproveReqVO.setSReplyCd((String)hm.get("reply_cd"));    //결과코드
        allatApproveReqVO.setSReplyMsg((String)hm.get("reply_msg"));  //결과메세지

        /* 결과값 처리
         * --------------------------------------------------------------------------
         * 결과 값(sReplyCd)이 '0000'이면 정상. 단, BeforePage 의 allat_test_yn=Y 일경우 '0001'이 정상.
         * 실제 승인 : allat_test_yn=N 일 경우 sReplyCd=0000 이면 정상
         * 테스트 승인 : allat_test_yn=Y 일 경우 sReplyCd=0001 이면 정상
         * --------------------------------------------------------------------------
         */
        if(allatApproveReqVO.getSReplyCd().equals("0000")){
            // reply_cd "0000" 일때만 성공
            allatApproveReqVO.setSFixKey((String)hm.get("fix_key"));         // 인증키
            allatApproveReqVO.setSApplyYmd((String)hm.get("apply_ymd"));     // 인증일

            log.info("[올앳 인증키 전달 결과] {}", allatApproveReqVO.toString());
            return allatApproveReqVO;
        }else{
            log.error("[올앳 인증키 전달 실패] code = {}, resultMsg = {}", allatApproveReqVO.getSReplyCd(), allatApproveReqVO.getSReplyMsg());
            throw new Exception();
        }

    }


    /**
     * 자동결제 승인 요청
     */
    @Override
    public PaymentVO allatPaymentApprove(UserVO userVO, String orderCode, String cardKey) {
        HashMap reqHm = new HashMap();
        HashMap resHm=null;
        String szReqMsg="";
        String szAllatEncData="";

        // 결제 요청 정보
        //------------------------------------------------------------------------
        String szAmt            = Integer.toString(this.paymentService.calculateAmount(Const.JoinStatus.JOIN, null));    //금액(최대 10자)
        String szCardKey        = cardKey;                         //카드키(최대 24자)
        String szOrderNo        = orderCode;                       //주문번호(최대 80자) : 쇼핑몰 고유 주문번호 -> 관리자페이지 [상점거래번호]
        String szShopMemberId   = userVO.getMediaUserKey();        //회원ID(최대 20자)   -> 관리자페이지 [고객ID]
        String szBuyerNm        = userVO.getName();                //결제자성명(최대 20자) -> 관리자페이지 [고객명]
        String szRecpNm         = userVO.getName();                //수취인성명(최대 20자)

        reqHm.put("allat_card_key"          , szCardKey        );
        reqHm.put("allat_amt"               , szAmt            );
        reqHm.put("allat_shop_member_id"    , szShopMemberId   );
        reqHm.put("allat_order_no"          , szOrderNo        );
        reqHm.put("allat_buyer_nm"          , szBuyerNm        );
        reqHm.put("allat_recp_name"         , szRecpNm         );
        reqHm.put("allat_product_nm"        , "stock"          );  //상품명(최대 1000자) : 여러 상품의 경우 구분자 이용, 구분자('||':파이프 2개)   -> 관리자페이지 [상품코드]
        reqHm.put("allat_product_cd"        , "stock"          );  //상품코드(최대 1000자) : 여러 상품의 경우 구분자 이용, 구분자('||':파이프 2개)  -> 관리자페이지 [상품명]
        reqHm.put("allat_recp_addr"         , ""               );  //수취인주소(최대 120자)
        reqHm.put("allat_sell_mm"           , "00"             );  //할부개월값(최대2자)
        reqHm.put("allat_user_ip"           , "Unknown"        );  //결제자 IP(최대15자) : BuyerIp를 넣을수 없다면 "Unknown"으로
        reqHm.put("allat_cardcert_yn"       , "N"              );  //카드인증여부(최대 1자) : 인증(Y),인증사용않음(N),인증만사용(X)
        reqHm.put("allat_zerofee_yn"        , "N"              );  //일반/무이자 할부 사용 여부(최대 1자) : 일반(N), 무이자 할부(Y)
        reqHm.put("allat_bonus_yn"          , "N"              );  //보너스포인트 사용여부(최대1자) : 사용(Y), 사용않음(N)
        reqHm.put("allat_email_addr"        , ""               );  //(옵션)결제자 이메일 주소(50자)
        reqHm.put("allat_business_type"     , ""               );  //(옵션)결제자 카드종류(최대 1자) : 개인(0),법인(1)
        reqHm.put("allat_registry_no"       , ""               );  //(옵션)주민번호(최대 13자리) : szBusinessType=0 일경우
        reqHm.put("allat_biz_no"            , ""               );  //(옵션)사업자번호(최대 20자리) : szBusinessType=1 일경우
        reqHm.put("allat_gender"            , ""               );  //(옵션)구매자 성별(최대 1자) : 남자(M)/여자(F)
        reqHm.put("allat_birth_ymd"         , ""               );  //(옵션)구매자의 생년월일(최대 8자) : YYYYMMDD형식
        reqHm.put("allat_shop_id"           , this.allatShopId );  //상점ID(최대 20자)
        reqHm.put("allat_test_yn"           , this.allAtTestYn );  //테스트 :Y, 서비스 :N
        reqHm.put("allat_pay_type"          , "FIX"            );  //수정금지(결제방식 정의)
        reqHm.put("allat_opt_pin"           , "NOUSE"          );  //수정금지(올앳 참조 필드)
        reqHm.put("allat_opt_mod"           , "APP"            );  //수정금지(올앳 참조 필드)

        szAllatEncData=util.setValue(reqHm);
        szReqMsg  = "allat_shop_id="   + this.allatShopId
                  + "&allat_amt="      + szAmt
                  + "&allat_enc_data=" + szAllatEncData
                  + "&allat_cross_key="+ this.allatCrossKey;


        resHm = util.approvalReq(szReqMsg, "SSL");
        // 결과 예시
        // ----------------
        // resHm = {order_no=fb0c87f570aa418ebae46f8c6eca028f, escrow_yn=N, approval_no=42770962, mpoint_amt=0, reply_msg=정상,
        // sell_mm=00, amt=1000, card_nm=BC, contract_yn=N, partcancel_yn=Y, cert_yn=N, isp_full_card_cd=, card_pointdc_amt=0,
        // pd_mpoint_seq_no=, card_no=, pd_yn=N, pay_type=FIX, point_amt=, approval_ymdhms=20211028145215, seq_no=1037127721,
        // sf_card_id=61, pd_type=, card_type=C, card_id=61, zerofee_yn=N, pd_amt=, reply_cd=0000, sf_card_nm=BC, currency_cd=410, bc_cert_no=, save_amt=}

        // 결과 값 확인
        //------------------
        // String sPayType        = (String)resHm.get("pay_type");          // 지불수단
        // String sApprovalNo     = (String)resHm.get("approval_no");       // 승인번호
        // String sCardId         = (String)resHm.get("card_id");           // 카드ID
        // String sCardNm         = (String)resHm.get("card_nm");           // 카드명
        // String sSellMm         = (String)resHm.get("sell_mm");           // 할부개월
        // String sZerofeeYn      = (String)resHm.get("zerofee_yn");        // 무이자여부
        // String sCertYn         = (String)resHm.get("cert_yn");           // 인증여부
        // String sContractYn     = (String)resHm.get("contract_yn");       // 직가맹여부
        String sOrderNo        = (String)resHm.get("order_no");         // 주문번호
        String sAmt            = (String)resHm.get("amt");              // 승인금액
        String sReplyCd        = (String)resHm.get("reply_cd");         // 결과코드
        String sReplyMsg       = (String)resHm.get("reply_msg");        // 결과메세지
        String sSeqNo          = (String)resHm.get("seq_no");           // 거래일련번호
        String sApprovalYmdHms = (String)resHm.get("approval_ymdhms");  // 승인일시

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setReserveId(userVO.getReserveId());
        paymentVO.setOrderCode(sOrderNo);
        paymentVO.setPaymentAmount(Integer.parseInt(sAmt));
        paymentVO.setPgName(Const.PG.ALLAT.name());
        paymentVO.setAllatOrderNo(sSeqNo);
        paymentVO.setCompleteYmdt(sApprovalYmdHms);

        // reply_cd 가 "0000" 아닐때는 에러 (자세한 내용은 매뉴얼참조)
        if(sReplyCd.equals("0000")){
            paymentVO.setSuccessResult();
        }else{
            log.error("---[올앳 결제 실패]------------------------------------------------------");
            log.error("ResultCode = {}, ResultMsg = {}, reserveId = {}", sReplyCd, sReplyMsg, userVO.getReserveId());
            log.error("----------------------------------------------------------------------");

            paymentVO.setResultCode(sReplyCd);
            paymentVO.setResultMessage(sReplyMsg);
        }

        return paymentVO;

    }


    /**
     * 올앳 결제 취소
     */
    @Override
    public PaymentCancelVO allatRefund(PaymentCancelVO paymentCancelVO) {
        HashMap reqHm = new HashMap();
        HashMap resHm = null;
        String szReqMsg = "";
        String szAllatEncData = "";

        // 취소 요청 정보
        //------------------------------------------------------------------------
        reqHm.put("allat_shop_id",    this.allatShopId);
        reqHm.put("allat_amt",        paymentCancelVO.getRefundAmount());   //금액(최대 10자)
        reqHm.put("allat_order_no",   paymentCancelVO.getOrderCode());      //주문번호(최대 80자) : 쇼핑몰 고유 주문번호
        reqHm.put("allat_test_yn",    this.allAtTestYn);                    //테스트 :Y, 서비스 :N
        reqHm.put("allat_pay_type",   "CARD");                              //수정금지(결제방식 정의)
        reqHm.put("allat_opt_pin",    "NOUSE");                             //수정금지(올앳 참조 필드)
        reqHm.put("allat_opt_mod",    "APP");                               //수정금지(올앳 참조 필드)

        szAllatEncData = util.setValue(reqHm);
        szReqMsg = "allat_shop_id=" + this.allatShopId
                + "&allat_amt=" + paymentCancelVO.getRefundAmount()
                + "&allat_enc_data=" + szAllatEncData
                + "&allat_cross_key=" + this.allatCrossKey;

        // 올앳 결제 서버와 통신  : AllatUtil.cancelReq->통신함수, HashMap->결과값
        //-----------------------------------------------------------------------------
        resHm = util.cancelReq(szReqMsg, "SSL");

        // 결과 값 확인
        //------------------
        String sReplyCd = (String) resHm.get("reply_cd");
        String sReplyMsg = (String) resHm.get("reply_msg");

        if (sReplyCd.equals("0000")) {
            // reply_cd "0000" 일때만 성공
            paymentCancelVO.setRefundSuccessResult();
            String sCancelYmdHms = (String) resHm.get("cancel_ymdhms");         // 취소일시
            String sPartCancelFlag = (String) resHm.get("part_cancel_flag");    // 취소구분
            String sRemainAmt = (String) resHm.get("remain_amt");               // 잔액
            String sPayType = (String) resHm.get("pay_type");                   // 거래방식구분

            log.info("---[올앳 결제 취소 완료]-----------------------------------------------------");
            log.info("취소일시 = {}, 취소구분 = {}, 잔액 = {}, 거래방식구분 = {}", sCancelYmdHms, sPartCancelFlag, sRemainAmt, sPayType);
            log.info("-------------------------------------------------------------------------");
        }else{
            log.error("---[올앳 결제 취소 실패]-----------------------------------------------------");
            log.error("ResultCode = {}, ResultMsg = {}, reserveId = {}", sReplyCd, sReplyMsg, paymentCancelVO.getReserveId());
            log.error("--------------------------------------------------------------------------");

            paymentCancelVO.setRefundResultCode(sReplyCd);
            paymentCancelVO.setRefundResultMessage(sReplyMsg);
        }

        return paymentCancelVO;

    }



    /**
     * 올앳 결제 정보 삭제
     */
    @Override
    public PaymentCancelVO allatDeleteInfo(PaymentCancelVO paymentCancelVO, String cardKey) {
        HashMap reqHm=new HashMap();
        HashMap resHm=null;
        String strReq="";
        String sEncData="";

        // 요청 정보
        //------------------------------------------------------------------------
        reqHm.put("allat_shop_id"           , this.allatShopId );  //상점ID(최대 20자)
        reqHm.put("allat_fix_key"           , cardKey          );  //해지할 카드인증 Key
        reqHm.put("allat_test_yn"           , "N"              );  //테스트 :Y, 서비스 :N
        reqHm.put("allat_opt_pin"           , "NOVIEW"         );  //수정금지(올앳 참조 필드)
        reqHm.put("allat_opt_mod"           , "WEB"            );  //수정금지(올앳 참조 필드)

        sEncData = util.setValue(reqHm);
        strReq = "allat_shop_id=" + this.allatShopId;
        strReq += "&allat_enc_data=" + sEncData;
        strReq += "&allat_cross_key=" + this.allatCrossKey;

        // 올앳 결제 서버와 통신  : AllatUtil.CertCancelReq->통신함수, HashMap->결과값
        //-----------------------------------------------------------------------------
        resHm = util.CertCancelReq(strReq, "SSL");

        // 결과 값 확인
        //------------------
        String sReplyCd  = (String)resHm.get("reply_cd");       //결과코드
        String sReplyMsg = (String)resHm.get("reply_msg");      //결과 메세지

        // 결과값 처리
        //--------------------------------------------------------------------------
        if( sReplyCd.equals("0000") ){
            // reply_cd "0000" 일때만 성공
            paymentCancelVO.setDeleteSuccessResult();
            paymentCancelVO.setAllatFixKey((String)resHm.get("fix_key"));
            paymentCancelVO.setCompleteYmdt((String)resHm.get("apply_ymd"));

            log.info("---[올앳 자동결제 정보 삭제 완료]-----------------------------------------------------");
            log.info("PaymentCancelVO = {}", paymentCancelVO.toString());
            log.info("--------------------------------------------------------------------------------");
        }else{
            log.error("---[올앳 자동결제 정보 삭제 실패]-----------------------------------------------------");
            log.error("ResultCode = {}, ResultMsg = {}, allat_fix_key = {}", sReplyCd, sReplyMsg, cardKey);
            log.error("---------------------------------------------------------------------------------");

            paymentCancelVO.setDeleteResultCode(sReplyCd);
            paymentCancelVO.setDeleteResultMessage(sReplyMsg);
        }

        return paymentCancelVO;

    }

}