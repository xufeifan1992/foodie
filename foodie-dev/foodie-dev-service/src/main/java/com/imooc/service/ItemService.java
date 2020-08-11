package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface ItemService {

    /**
     * 根据商品ID查询商品详情
     *
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);

    /**
     * 根据商品ID查询图片列表
     *
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品ID查询商品规格
     *
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpectList(String itemId);

    /**
     * 根据商品ID查询商品参数
     *
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 查询商品各个评价等级数量
     *
     * @param itemId
     * @return
     */
    public CommentLevelCountsVO queryCommentCounts(String itemId);


    /**
     * 根据商品id，查询商品评价，分页
     *
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 根据商品id，查询商品评价，分页
     *
     * @param keywords
     * @param sort
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 根据分类ID查询商品列表
     *
     * @param catId
     * @param sort
     * @return
     */
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize);

    /**
     * 根据规格ID查询最新购物车中的商品数据
     *
     * @param specIds
     * @return
     */
    public List<ShopcartVO>queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格ID获取规格对象的具体信息
     * @param specId
     * @return
     */
    public ItemsSpec queryItemSpecById(String specId);

    /**
     * 根据商品ID获取商品图片URL
     *
     * @param itemId
     * @return
     */
    public String queryItemMainImgById(String itemId);


    public void decreaseItemSpecStock(String specid,Integer buyCounts);


}
