package com.stock.service.impl;

import com.stock.exception.ProcessException;
import com.stock.mapper.CancelMapper;
import com.stock.mapper.PaymentMapper;
import com.stock.mapper.ReserveMapper;
import com.stock.mapper.UserMapper;
import com.stock.service.*;
import com.stock.util.Const;
import com.stock.util.OrderUtil;
import com.stock.vo.PaymentCancelVO;
import com.stock.vo.PaymentVO;
import com.stock.vo.ReserveVO;
import com.stock.vo.UserVO;
import com.stock.vo.allat.AllatApproveReqVO;
import com.stock.vo.allat.AllatVO;
import com.stock.vo.payco.PaycoReserveInfoReturnVO;
import com.stock.vo.payco.PaycoReserveInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ReserveMapper reserveMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CancelMapper cancelMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaycoService paycoService;

    @Autowired
    private UserService userService;

    @Autowired
    private AllatService allatService;

    @Autowired
    private EMoneyService eMoneyService;



    /**
     * 모든 PG 공통
     * 결제창 생성시 필요한 정보 생성
     * @param userVO
     * @param pg
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public ResponseEntity<?> reserveAutoPayment(UserVO userVO, Const.PG pg) throws Exception {
        // User 테이블 데이터 저장
        Long userId = this.userService.insertUser(userVO);

        // Reserve 테이블 데이터 저장
        ReserveVO reserveVO = new ReserveVO();
        reserveVO.setUserId(userId);
        reserveVO.setUserCid(userVO.getUserCid());
        reserveVO.setPgName(pg.name());
        this.reserveMapper.insertReserve(reserveVO);

        userVO.setReserveId(reserveVO.getId());
        userVO.setEncodeData(java.net.URLEncoder.encode(userVO.getEncodeData(), "UTF-8"));

        if(pg.equals(Const.PG.PAYCO)){
            // 결제창 URL
            String orderSheetUrl = this.paycoService.paycoReserve(userVO, reserveVO);
            return new ResponseEntity<>(orderSheetUrl, HttpStatus.OK);
        }else if(pg.equals(Const.PG.ALLAT)){
            // 결제창 호출시 request data
            Map<String, String> resultMap = this.allatService.allatPaymentRequestParam(userVO);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }else{
            throw new ProcessException("올바른 경로로 접근해주세요.");
        }

    }


    /**
     * 가입 요금 결제 및 이머니 가입 처리 공통 메소드
     * @param userVO
     * @param paycoReserveInfoVO
     * @param allatVO
     * @throws Exception
     */
    @Override
    public void payment(Const.PG pg, UserVO userVO, PaycoReserveInfoVO paycoReserveInfoVO, AllatVO allatVO) throws Exception {
        // 자동결제 등록 및 가입 요금 결제
        if(pg.equals(Const.PG.payco)){
            this.paycoJoinPayment(userVO, paycoReserveInfoVO);
        }else if(pg.equals(Const.PG.allat)){
            this.allatJoinPayment(userVO, allatVO);
        }else{
            throw new Exception("확인된 PG사 없음");
        }

        // 이머니 가입
        this.eMoneyService.joinEMoney(userVO);

        // 가입완료 정보 DB 저장
        userVO.setStatus(Const.JoinStatus.JOIN);
        userVO.setJoinDate(Const.yyyyMMddHHmm_FORMAT.format(new Date()));
        userMapper.updateJoinInfoOfUser(userVO);

    }


    /**
     * 페이코 가입시 요금 일할 결제
     * @param userVO
     * @param paycoReserveInfoVO
     * @throws Exception
     */
    @Transactional
    public void paycoJoinPayment(UserVO userVO, PaycoReserveInfoVO paycoReserveInfoVO) throws Exception {
        // 자동결제 예약 정보 조회 API 호출
        PaycoReserveInfoReturnVO paycoReserveInfoReturnVO = this.paycoService.paycoFindReserveInfo(paycoReserveInfoVO);
        paycoReserveInfoReturnVO.setReserveId(userVO.getReserveId());

        // 예약 정보 DB 저장
        this.reserveMapper.savePaycoReserveInfo(paycoReserveInfoReturnVO);
        this.userMapper.saveReserveIdOfUser(userVO.getUserCid(), userVO.getReserveId());

        // 결제 처리
        ReserveVO reserveVO = new ReserveVO();
        reserveVO.setId(paycoReserveInfoReturnVO.getReserveId());
        reserveVO.setPaycoAutopaymentCertifyKey(paycoReserveInfoReturnVO.getAutoPaymentCertifyKey());
        reserveVO.setPaycoAutopaymentNo(paycoReserveInfoReturnVO.getAutoPaymentNo());

        String oderCode = paycoReserveInfoReturnVO.getSellerAutoPaymentReferenceKey();
        // 가입 요금에 대한 결제 API 호출
        PaymentVO paymentVO = this.paycoService.paycoPayment(reserveVO, oderCode, this.calculateAmount(Const.JoinStatus.JOIN, null));

        // 결제 정보 DB 저장
        this.paymentMapper.saveAutoPaymentInfo(paymentVO);

        if(!paymentVO.isResultSuccess()){
            log.error("[페이코 결제 처리 API 결과 오류] -> {}", paymentVO.getResultMessage());
            throw new Exception(paymentVO.getResultMessage());
        }

    }


    /**
     * 올앳 가입시 요금 일할 결제
     * @param userVO
     * @throws Exception
     */
    @Transactional
    public void allatJoinPayment(UserVO userVO, AllatVO allatVO) throws Exception {
        // 인증정보 승인 API 호출
        AllatApproveReqVO allatApproveReqVO = this.allatService.allatPaymentRequest(allatVO.getAllat_enc_data());
        allatApproveReqVO.setReserveId(userVO.getReserveId());

        // 예약 정보 DB 저장
        this.reserveMapper.saveAllatReserveInfo(allatApproveReqVO);
        this.userMapper.saveReserveIdOfUser(userVO.getUserCid(), userVO.getReserveId());

        // 결제 API 호출
        PaymentVO paymentVO = this.allatService.allatPaymentApprove(userVO, OrderUtil.getOrderCode(), allatApproveReqVO.getSFixKey());

        // 결제 정보 DB 저장
        this.paymentMapper.saveAutoPaymentInfo(paymentVO);

        if(!paymentVO.isResultSuccess()){
            throw new Exception("올앳 결제 API 결과 실패");
        }

    }


    /**
     * 정액 자동 결제
     * (배치 스케줄러)
     * @throws Exception
     */
    @Override
    @Transactional
    public void fixAmountAutoPayment() throws Exception {
        List<ReserveVO> reserveVOList = reserveMapper.selectReserveInfoList(Const.JoinStatus.JOIN.name());

        if(reserveVOList != null){
            for(ReserveVO reserveVO : reserveVOList){
                PaymentVO paymentVO = new PaymentVO();
                String pg = reserveVO.getPgName();

                // 주문코드 생성
                String orderCode = OrderUtil.getOrderCode();

                // 결제 처리
                if(pg.equals(Const.PG.PAYCO.name())){
                    paymentVO = this.paycoService.paycoPayment(reserveVO, orderCode, Const.FIX_AMOUNT);
                }else if(pg.equals(Const.PG.ALLAT.name())){
                    UserVO userVO = this.userMapper.selectUserByUserCidAndStatus(reserveVO.getUserCid(), Const.JoinStatus.JOIN.name());
                    paymentVO = this.allatService.allatPaymentApprove(userVO, orderCode, reserveVO.getAllatFixKey());
                }else{
                    log.error("---[자동결제 배치처리 오류]----------------------------------------------------");
                    log.error("reserveId = {} -------------> PG사 정보 확인되지 않음", reserveVO.getId());
                    log.error("--------------------------------------------------------------------------");
                }

                // 결제 처리 API 결과 정보 DB 저장
                paymentMapper.saveAutoPaymentInfo(paymentVO);

            }
        }

    }


    /**
     * 결제 취소 공통 메소드
     * @param userCid
     * @throws Exception
     */
    @Override
    public void cancelPayment(String userCid, Const.JoinStatus status) throws Exception {
        final UserVO userVO = userService.selectUserByUserCid(userCid, Const.JoinStatus.all.name());

        // 해지 가능 여부 체크
        if (userVO == null) {
            throw new ProcessException("가입 이력이 없습니다.");
        }
        else if (userVO.getStatus().equals(Const.JoinStatus.CANCEL)) {
            throw new ProcessException("이미 해지된 상태입니다.");
        }

        // Reserve 테이블 결제 정보 조회
        final ReserveVO reserveVO = reserveMapper.selectReserveById(userVO.getReserveId());
        if(reserveVO == null){
            throw new ProcessException("취소가능한 자동결제 등록 정보가 없습니다.");
        }

        final int refundAmount = this.calculateAmount(Const.JoinStatus.CANCEL, userVO.getJoinDateToDateFormat());
        PaymentVO paymentVO = paymentMapper.selectPayment(userVO.getReserveId());
        PaymentCancelVO paymentCancelVO = new PaymentCancelVO();
        paymentCancelVO.setReserveId(paymentVO.getReserveId());
        paymentCancelVO.setOrderCode(paymentVO.getOrderCode());
        paymentCancelVO.setRefundAmount(refundAmount);

        // 파트너 PG사 확인
        String pg = this.reserveMapper.selectReserveByUserCid(userVO.getUserCid()).getPgName();

        if(pg != null){
            // 환불 처리
            if(pg.equals(Const.PG.PAYCO.name())){
                paymentCancelVO.setPaycoOrderNo(paymentVO.getPaycoOrderNo());
                this.paycoCancelPayment(userVO, reserveVO, paymentCancelVO, paymentVO.getPaycoOrderCertifyKey());
            }else if(pg.equals(Const.PG.ALLAT.name())){
                this.allatCancelPayment(userVO, paymentCancelVO, reserveVO.getAllatFixKey());
            }else{
                throw new ProcessException("잘못된 접근입니다. 정상적인 경로로 재접속해주세요.");
            }
        }else{
            throw new ProcessException("잘못된 접근입니다. 정상적인 경로로 재접속해주세요.");
        }

        if(status.equals(Const.JoinStatus.CANCEL)){
            // 이머니 해지
            this.eMoneyService.cancelEMoney(userVO);

            // 해지완료 정보 DB 저장
            userVO.setStatus(status);
            userVO.setCancelDate(Const.yyyyMMddHHmm_FORMAT.format(new Date()));
            this.userMapper.updateCancelInfoOfUser(userVO);
        }

    }


    /**
     * 페이코 자동결제 정보 삭제 및 환불
     * 1. status = CANCEL : 해지로 인한 결제 취소
     * 2. status = WAIT : 이머니 가입 중 오류 발생으로 결제취소
     * @throws Exception
     */
    @Transactional
    public void paycoCancelPayment(UserVO userVO, ReserveVO reserveVO,
                                   PaymentCancelVO paymentCancelVO, String certifyKey) throws Exception {

        boolean isRefundSuccess = true;
        boolean isDeleteSuccess = false;

        // 환불 처리 API 호출
        if(paymentCancelVO.getRefundAmount() > 0) {
            paymentCancelVO = this.paycoService.paycoRefund(paymentCancelVO, certifyKey);
            isRefundSuccess = paymentCancelVO.isRefundSuccess();
        }

        // 페이코 자동결제 정보 삭제 API 호출
        if(isRefundSuccess){
            paymentCancelVO = this.paycoService.paycoDeleteInfo(paymentCancelVO, reserveVO.getPaycoAutopaymentCertifyKey());
            isDeleteSuccess = paymentCancelVO.isDeleteSuccess();
        }

        // API 결과 DB 저장
        paymentCancelVO.setPgName(Const.PG.PAYCO.name());
        this.cancelMapper.saveCancelPaymentInfo(paymentCancelVO);

        if(!isRefundSuccess || !isDeleteSuccess){
            log.error("[페이코 자동결제 해지 실패] UserCid={}", userVO.getUserCid());
            throw new ProcessException("오류가 발생했습니다. 고객센터로 문의해주세요.");
        }
    }


    /**
     * 올앳 자동결제 정보 삭제 및 환불
     * 1. status = CANCEL : 해지로 인한 결제 취소
     * 2. status = WAIT : 이머니 가입 중 오류 발생으로 결제취소
     * @throws Exception
     */
    public void allatCancelPayment(UserVO userVO, PaymentCancelVO paymentCancelVO, String allatFixKey) {
        boolean isRefundSuccess = true;
        boolean isDeleteSuccess = false;

        // 환불 처리 API 호출
        if(paymentCancelVO.getRefundAmount() > 0) {
            paymentCancelVO = this.allatService.allatRefund(paymentCancelVO);
            isRefundSuccess = paymentCancelVO.isRefundSuccess();

            // 환불은 완료됐으나, 자동결제 정보 삭제는 실패될 경우 대비 가입유무 상태값 WAIT으로 변경
            this.userService.updateStatusToWait(userVO.getUserCid());
        }

        // 페이코 자동결제 정보 삭제 API 호출
        if(isRefundSuccess){
            paymentCancelVO = this.allatService.allatDeleteInfo(paymentCancelVO, allatFixKey);
            isDeleteSuccess = paymentCancelVO.isDeleteSuccess();
        }

        // API 결과 DB 저장
        paymentCancelVO.setPgName(Const.PG.ALLAT.name());
        this.cancelMapper.saveCancelPaymentInfo(paymentCancelVO);

        if(!isRefundSuccess || !isDeleteSuccess){
            log.error("[올앳 자동결제 해지 실패] UserCid={}", userVO.getUserCid());
            throw new ProcessException("오류가 발생했습니다. 고객센터로 문의해주세요.");
        }
    }


    /**
     * 가입 금액 일할 계산 = 서비스 제공일수 * 500원  = (정산일수 - 요금시작일) * 500원
     * 해지 환불 금액 일할 계산
     * @return amount
     */
    @Override
    public int calculateAmount(Const.JoinStatus typeEnum, Date joinDate){
        // 현재시간 기준
        final Calendar currentCal = Calendar.getInstance();
        final int today = currentCal.get(Calendar.DATE);                                    // 가입일
        final int thisMonthLastDay = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);    // 당월 마지막일

        // 요금 시작일 기준
        final Calendar joinCal = Calendar.getInstance();
        if(typeEnum.equals(Const.JoinStatus.CANCEL)) {                                      // 해지시 가입날짜로 세팅
            if(joinDate != null){
                joinCal.setTime(joinDate);
            }else{
                return 0;
            }
        }

        joinCal.add(Calendar.DATE, Const.FREE_CHARGE_PERIOD);                               // 무료체험 기간 적용(가입일 + 무료체험일수)
        final int chargeStartDay = joinCal.get(Calendar.DATE);                              // 요금시작일 = 가입일 + 무료체험일수
        final int chargeLastDay = joinCal.getActualMaximum(Calendar.DAY_OF_MONTH);          // 정산일수(요금시작 달의 마지막일)
        final int chargeDayCount = chargeLastDay - chargeStartDay;                          // 일할계산 일수 = 정산일수 - 요금시작일

        int amount = Math.min(chargeDayCount * Const.CHARGE_PRICE, Const.FIX_AMOUNT);       // 가입 당월 요금 (정액 요금을 넘을 수 없음)

        // 당월요금 + 익월요금 청구하는 기간
        // 정산일 <= 가입일 <= 요금시작일이 가입일의 마지막
        if(Const.CHARGE_DAY <= today && today <= thisMonthLastDay - Const.FREE_CHARGE_PERIOD){
            amount += Const.FIX_AMOUNT;
        }

        // 해지시 환불 금액
        if(typeEnum.equals(Const.JoinStatus.CANCEL)){
            // 무료체험 기간 이후
            if(currentCal.after(joinCal)){
                // 가입 당월에 해지
                if(Const.yyyy_MM_FORMAT.format(joinCal.getTime()).equals(Const.yyyy_MM_FORMAT.format(currentCal.getTime()))){
                    int usedDay = today - chargeStartDay;                                    // 이용일 수
                    amount = Const.CHARGE_PRICE * (chargeDayCount - usedDay);                // 하루이용금액 * 미이용일수

                    // 정산일 이후(익월요금 청구 이후)
                    if(Const.CHARGE_DAY <= today){
                        return amount + Const.FIX_AMOUNT;                                    // 당월요금 + 익월요금
                    }
                }
                // 가입 당월 이후
                else{
                    amount = Const.CHARGE_PRICE * ((Math.min(thisMonthLastDay, 30)) - today); // 하루이용금액 * 미이용일수
                }
            }
        }

        return amount;
    }

}