package com.imooc.mapper;

import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemVO;
import com.imooc.pojo.vo.ShopcartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {

    List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String,Object> map);

    List<SearchItemVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    List<SearchItemVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> map);

    List<ShopcartVO> queryItemsBySpecIds(@Param("paramsList") List specIdsList);

    int decreaseItemSpecStock(@Param("specId") String specId, @Param("pendingCounts") int pendingCounts);
}
