package com.stock.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.stock.mapper.EMoneyMapper;
import com.stock.mapper.UserMapper;
import com.stock.service.EMoneyService;
import com.stock.service.PaymentService;
import com.stock.util.Const;
import com.stock.vo.UserVO;
import com.stock.vo.emoney.EMoneyMainDataVO;
import com.stock.vo.emoney.EMoneyResultVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class EMoneyServiceImpl implements EMoneyService {

    @Value("${emoney.api.url}")
    private String eMoneyApiUrl;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EMoneyMapper emoneyMapper;

    @Autowired
    private UserMapper userMapper;


    final ObjectMapper mapper = new ObjectMapper();
    final HttpHeaders httpHeaders = new HttpHeaders();
    final HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);

    /**
     * 메인페이지용 이머니 주식정보 API 호출
     * @return
     */
    @Override
    public EMoneyMainDataVO landMainEMoney() throws JSONException {
        final String requestUrl = String.format(this.eMoneyApiUrl, "landing");

        ResponseEntity<String> result = new RestTemplate().postForEntity(requestUrl, requestEntity, String.class);
        JSONObject json = new JSONObject(result.getBody());
        EMoneyMainDataVO eMoneyMainDataVO = new Gson().fromJson(json.toString(), EMoneyMainDataVO.class);
        eMoneyMainDataVO.todayStock.setExpectProfit();
        eMoneyMainDataVO.setDate(new Date());

        return eMoneyMainDataVO;

    }


    /**
     * 이머니 가입 RequestData
     * @param userVO
     * @return
     */
    @Override
    public void joinEMoney(UserVO userVO) throws Exception {
        final String type = "signup";

        final Calendar calendar = Calendar.getInstance();
        final String registerDate = Const.yyyy_MM_dd_FORMAT.format(calendar.getTime());
        calendar.add(Calendar.DATE, Const.FREE_CHARGE_PERIOD);
        final String paidConDate = Const.yyyy_MM_dd_FORMAT.format(calendar.getTime());

        final Map<String, Object> params = new HashMap<>();
        params.put("registerDate", registerDate);
        params.put("paidConDate", paidConDate);
        params.put("mediaKey", userVO.getMediaKey());
        params.put("mediaUserKey", userVO.getMediaUserKey());
        params.put("userCid", userVO.getUserCid());
        params.put("mpn", userVO.getPhoneNum());
        params.put("payment", 1);

        final boolean isSuccess = this.requestEMoneyApi(mapper.writeValueAsString(params), userVO, type);

        if(!isSuccess){
            log.error("---[EMONEY 가입 실패]-------------------------------------------------------");
            log.error("userCid = {}", userVO.getUserCid());
            log.error("--------------------------------------------------------------------------");

            // 결제 취소
            this.paymentService.cancelPayment(userVO.getUserCid(), Const.JoinStatus.WAIT);
            log.error("[EMONEY 가입 실패 ---> 결제 취소 완료] UserCid={}", userVO.getUserCid());
            throw new Exception();
        }
    }


    /**
     * 이머니 가입해지 RequestData
     * @param userVO
     * @return
     */
    @Override
    public void cancelEMoney(UserVO userVO) throws Exception {
        final String type = "signout";
        final String mediaKey = this.userMapper.selectMediaKey(userVO.getUserCid());
        userVO.setMediaKey(mediaKey);

        final Map<String, Object> params = new HashMap<>();
        params.put("secessionDate", Const.yyyy_MM_dd_FORMAT.format(new Date()));
        params.put("mediaKey", mediaKey);
        params.put("userCid", userVO.getUserCid());
        params.put("mpn", userVO.getPhoneNum());

        final boolean isSuccess = this.requestEMoneyApi(mapper.writeValueAsString(params), userVO, type);

        if(!isSuccess){
            log.error("---[EMONEY 해지 실패]----------------------------------------------------");
            log.error("UserCid = {}", userVO.getUserCid());
            log.error("-----------------------------------------------------------------------");
            throw new Exception();
        }
    }


    /**
     * 이머니 회원정보 업데이트 RequestData
     * @param userVO
     * @return
     */
    @Override
    public void modifyEMoney(UserVO userVO) throws Exception {
        final String type = "modify";

        final Map<String, Object> params = new HashMap<>();
        params.put("userCid", userVO.getUserCid());
        params.put("mpn", userVO.getPhoneNum());
        params.put("payment", 1);

        final boolean isSuccess = this.requestEMoneyApi(mapper.writeValueAsString(params), userVO, type);

        if(!isSuccess){
            log.error("---[EMONEY 가입 정보 업데이트 실패]----------------------------------------------------");
            log.error("UserCid = {}", userVO.getUserCid());
            log.error("-----------------------------------------------------------------------");
            throw new Exception();
        }
    }


    /**
     * 이머니 가입/해지/업데이트용 API 연동
     * @param jsonString
     * @param userVO
     * @param type
     * @return
     */
    public boolean requestEMoneyApi(String jsonString, UserVO userVO, String type) throws Exception {
        try{
            // API 호출
            final String requestUrl = String.format(this.eMoneyApiUrl, type);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            final HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, httpHeaders);
            final ResponseEntity<Result> result = (new RestTemplate()).postForEntity(requestUrl, requestEntity, Result.class);

            // 결과 DB 저장
            EMoneyResultVO eMoneyResultVO = new EMoneyResultVO();
            eMoneyResultVO.setType(type.toUpperCase());
            eMoneyResultVO.setUserCid(userVO.getUserCid());
            eMoneyResultVO.setResultCode(Objects.requireNonNull(result.getBody()).getResultCode());
            eMoneyResultVO.setResultMsg(result.getBody().getResultMessage());
            eMoneyResultVO.setParam(jsonString);
            this.emoneyMapper.insertEMoneyApiResult(eMoneyResultVO);

            return result.getBody().isSuccess();
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    @Getter
    @NoArgsConstructor
    static class Result {
        private String resultCode;
        private String resultMessage;

        public String getResultMessage(){
            switch (this.resultCode){
                case "0000" : this.resultMessage = "성공"; break;
                case "0001" : this.resultMessage = "파라미터 오류"; break;
                case "0002" : this.resultMessage = "이미 저장중인 USER CID"; break;
                case "0009" : this.resultMessage = "기타 오류"; break;
            }
            return this.resultMessage;
        }

        public boolean isSuccess(){
            return this.resultCode.equals("0000");
        }
    }

}
