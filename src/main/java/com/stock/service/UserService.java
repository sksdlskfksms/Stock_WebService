package com.stock.service;

import com.stock.vo.UserVO;

import java.text.ParseException;

public interface UserService {

    boolean isJoinUser(String mediaUserKey, String mediaKey);

    Long insertUser(UserVO userVO) throws ParseException;

    UserVO selectUserByUserCid(String userCid, String status);

    void updatePhoneOfUser(String userCid, String phoneNum);

    void updateStatusToWait(String userCid);

    boolean isOkRejoin(UserVO userVO) throws ParseException;

}
