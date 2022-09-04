package com.stock.mapper;

import com.stock.core.anotation.Mapper;
import com.stock.vo.UserVO;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    Long insertUser(UserVO userVO);

    UserVO selectUserByUserCid(String userCid);

    UserVO selectUserByUserCidAndStatus(String userCid, String status);

    UserVO selectUserByMediaUserKeyAndStatus(String mediaUserKey, String mediaKey, String status);

    void updateJoinInfoOfUser(UserVO userVO);

    void updateCancelInfoOfUser(UserVO userVO);

    void updateStatusToWait(String userCid, String status);

    void updatePhoneOfUser(String userCid, String phoneNum);

    void saveReserveIdOfUser(String userCid, Long reserveId);

    String selectMediaKey(String userCid);

}
