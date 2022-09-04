package com.stock.service.impl;

import com.stock.vo.MediaVO;
import com.stock.mapper.MediaMasterMapper;
import com.stock.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private MediaMasterMapper mediaMasterMapper;


    /**
     * 매체사 정보
     * by mediaKey
     */
    @Override
    public MediaVO findMediaInfoByMediaKey(String mediaKey){
        return this.mediaMasterMapper.selectMediaMasterByMediaKey(mediaKey);
    }


    /**
     * 매체사 정보
     * by userCid
     */
    @Override
    public MediaVO findMediaInfoByUserCid(String userCid){
        return this.mediaMasterMapper.selectMediaMasterByUserCid(userCid);
    }

}