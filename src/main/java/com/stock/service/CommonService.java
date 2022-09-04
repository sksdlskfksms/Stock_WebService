package com.stock.service;

import com.stock.vo.MediaVO;

public interface CommonService {

    MediaVO findMediaInfoByMediaKey(String mediaKey);

    MediaVO findMediaInfoByUserCid(String userCid);

}
