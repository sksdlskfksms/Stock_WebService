package com.stock.service.impl;

import com.stock.exception.ProcessException;
import com.stock.mapper.UserMapper;
import com.stock.service.UserService;
import com.stock.util.Const;
import com.stock.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean isJoinUser(String mediaUserKey, String mediaKey) {
        return userMapper.selectUserByMediaUserKeyAndStatus(mediaUserKey, mediaKey, Const.JoinStatus.JOIN.name()) != null;
    }

    @Override
    @Transactional
    public Long insertUser(UserVO userVO) throws ParseException {
        // 기가입 여부 체크
        UserVO dbUserVO = this.selectUserByUserCid(userVO.getUserCid(), Const.JoinStatus.all.name());
        if(dbUserVO != null){
            if(dbUserVO.getStatus().equals(Const.JoinStatus.JOIN)){
                throw new ProcessException("같은 번호로 이미 가입되어 있습니다.");
            }

            // 해지상태일 경우
            else if(dbUserVO.getStatus().equals(Const.JoinStatus.CANCEL)) {
                // 해지 이후 2M 경과 여부 체크
                if(!this.isOkRejoin(dbUserVO)){
                    throw new ProcessException("가입 해지월 기준으로 2개월 이후 재가입이 가능합니다.");
                }
            }

            return dbUserVO.getId();
        }
        else{
            userVO.setStatus(Const.JoinStatus.WAIT);
            userMapper.insertUser(userVO);
            return userVO.getId();
        }

    }


    /**
     * 회원 정보 조회
     * @param userCid
     * @param status
     * @return
     */
    @Override
    public UserVO selectUserByUserCid(String userCid, String status) {
        // status 관계없이 userCid 정보로 조회
        if(status.equals(Const.JoinStatus.all.name())){
            return userMapper.selectUserByUserCid(userCid);
        }
        // 특정 status 상태 조건 추가
        else{
            return userMapper.selectUserByUserCidAndStatus(userCid, status.toUpperCase());
        }
    }

    @Override
    @Transactional
    public void updatePhoneOfUser(String userCid, String phoneNum){ userMapper.updatePhoneOfUser(userCid, phoneNum); }

    @Override
    public void updateStatusToWait(String userCid){ userMapper.updateStatusToWait(userCid, Const.JoinStatus.WAIT.name()); }


    /**
     * 재가입 가능 여부 체크
     * 조건 : 해지월 + 2M 이후
     * ex) 1월 1일 해지 => 3월 1일 부터 가입가능
     * @param userVO
     * @return
     */
    @Override
    public boolean isOkRejoin(UserVO userVO) throws ParseException {
        // 현재 날짜
        Calendar today = new GregorianCalendar(Locale.KOREA);

        // 재가입 가능 날짜 = 해지월 + 2M
        Calendar cancelDate = new GregorianCalendar(Locale.KOREA);
        cancelDate.setTime(userVO.getCancelDateToDateFormat());
        cancelDate.add(Calendar.MONTH, 2);

        return today.after(cancelDate) || today.equals(cancelDate);
    }


}
