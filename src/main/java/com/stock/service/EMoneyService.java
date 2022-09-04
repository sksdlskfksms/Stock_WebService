package com.stock.service;

import com.stock.vo.UserVO;
import com.stock.vo.emoney.EMoneyMainDataVO;
import org.json.JSONException;

public interface EMoneyService {

    EMoneyMainDataVO landMainEMoney() throws JSONException;

    void joinEMoney(UserVO userVO) throws Exception;

    void cancelEMoney(UserVO userVO) throws Exception;

    void modifyEMoney(UserVO userVO) throws Exception;

}
