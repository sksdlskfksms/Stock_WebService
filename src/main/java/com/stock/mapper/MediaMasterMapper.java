package com.stock.mapper;

import com.stock.core.anotation.Mapper;
import com.stock.vo.MediaVO;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MediaMasterMapper {
    MediaVO selectMediaMasterByMediaKey(String mediaKey);

    MediaVO selectMediaMasterByUserCid(String userCid);

}
