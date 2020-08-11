package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.CommentLevel;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.service.ItemService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Transactional
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;

    @Override
    public Items queryItemById(String itemId) {

        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Override
    public ItemsParam queryItemParam(String itemId) {

        Example example = new Example(ItemsParam.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId", itemId);
        ItemsParam itemsParam = itemsParamMapper.selectOneByExample(example);

        return itemsParam;
    }

    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example example = new Example(ItemsImg.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId", itemId);
        List<ItemsImg> itemsImgs = itemsImgMapper.selectByExample(example);

        return itemsImgs;
    }

    @Override
    public List<ItemsSpec> queryItemSpectList(String itemId) {
        Example example = new Example(ItemsSpec.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId", itemId);
        List<ItemsSpec> itemsSpecs = itemsSpecMapper.selectByExample(example);

        return itemsSpecs;
    }

    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        CommentLevelCountsVO commentLevelCountsVO = new CommentLevelCountsVO();


        Integer goodCounts = getCommentCounts(itemId, CommentLevel.good.type);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.normal.type);
        Integer badCounts = getCommentCounts(itemId, CommentLevel.bad.type);

        Integer totalCounts = goodCounts + normalCounts + badCounts;

        commentLevelCountsVO.setGoodCounts(goodCounts);
        commentLevelCountsVO.setNormalCounts(normalCounts);
        commentLevelCountsVO.setBadCounts(badCounts);
        commentLevelCountsVO.setTotalCounts(totalCounts);

        return commentLevelCountsVO;
    }

    private Integer getCommentCounts(String itemId, Integer level) {
        ItemsComments itemsComments = new ItemsComments();
        itemsComments.setItemId(itemId);
        if (level != null) {
            itemsComments.setCommentLevel(level);
        }
        int count = itemsCommentsMapper.selectCount(itemsComments);

        return count;
    }

    @Override
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);
        //mybatis-pageHelper
        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);
        //脱敏
        list.forEach(l -> {
            l.setNickname(DesensitizationUtil.commonDisplay(l.getNickname()));
        });

        PagedGridResult grid = setterPagedGrid(page, list);

        return grid;
    }

    private PagedGridResult setterPagedGrid(Integer page, List<?> list) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        //当前页
        grid.setPage(page);
        //总页数
        grid.setTotal(pageList.getPages());
        //每行显示内容
        grid.setRows(list);
        //总行数
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);

        List<SearchItemVO> list = itemsMapperCustom.searchItems(map);

        return setterPagedGrid(page, list);
    }

    @Override
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);

        List<SearchItemVO> list = itemsMapperCustom.searchItemsByThirdCat(map);

        return setterPagedGrid(page, list);
    }

    @Override
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {

        String[] ids = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);


        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Override
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.yes.type);
        return itemsImgMapper.selectOne(itemsImg) == null ? "" : itemsImgMapper.selectOne(itemsImg).getUrl();
    }

    @Override
    public void decreaseItemSpecStock(String specid, Integer buyCounts) {
        int resultCount = itemsMapperCustom.decreaseItemSpecStock(specid, buyCounts);
        if(resultCount != 1){
            throw new RuntimeException("订单创建，库存不足");
        }

    }
}
