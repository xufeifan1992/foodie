package com.imooc.controller;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemInfoVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.service.CategoryService;
import com.imooc.service.ItemService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品", tags = {"商品相关"})
@RestController
@RequestMapping("/items")
public class ItemController extends BaseController{
    @Autowired
    private ItemService itemService;


    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult sixNewItems(@ApiParam(name = "itemId", value = "商品ID", required = true) @PathVariable("itemId") String itemId) {

        if (itemId == null) {
            return IMOOCJSONResult.errorMsg(null);
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);
        List<ItemsSpec> itemsSpecList = itemService.queryItemSpectList(itemId);


        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemParams(itemsParam);
        itemInfoVO.setItemSpecList(itemsSpecList);

        return IMOOCJSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public IMOOCJSONResult commentLevel(@ApiParam(name = "itemId", value = "商品ID", required = true) @RequestParam String itemId) {

        if (itemId == null) {
            return IMOOCJSONResult.errorMsg(null);
        }

        CommentLevelCountsVO countsVO = itemService.queryCommentCounts(itemId);


        return IMOOCJSONResult.ok(countsVO);
    }

    @ApiOperation(value = "查询商品评价", notes = "查询商品评价", httpMethod = "GET")
    @GetMapping("/comments")
    public IMOOCJSONResult comments(@ApiParam(name = "itemId", value = "商品ID", required = true) @RequestParam String itemId,
                                    @ApiParam(name = "level", value = "评价等级", required = true) @RequestParam Integer  level,
                                    @ApiParam(name = "page", value = "第几页", required = true) @RequestParam Integer page,
                                    @ApiParam(name = "pageSize", value = "每页显示行数", required = true) @RequestParam Integer pageSize) {

        if (itemId == null) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = super.COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = itemService.queryPagedComments(itemId, level, page, pageSize);


        return IMOOCJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public IMOOCJSONResult search(@ApiParam(name = "keywords", value = "关键字", required = true) @RequestParam String keywords,
                                    @ApiParam(name = "sort", value = "排序", required = true) @RequestParam String sort,
                                    @ApiParam(name = "page", value = "第几页", required = true) @RequestParam Integer page,
                                    @ApiParam(name = "pageSize", value = "每页显示行数", required = true) @RequestParam Integer pageSize) {

        if (keywords == null) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = super.PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = itemService.searchItems(keywords, sort, page, pageSize);


        return IMOOCJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "根据分类搜索商品列表", notes = "根据分类搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public IMOOCJSONResult catItems(@ApiParam(name = "catId", value = "三级分类ID", required = true) @RequestParam Integer catId,
                                  @ApiParam(name = "sort", value = "排序", required = true) @RequestParam String sort,
                                  @ApiParam(name = "page", value = "第几页", required = true) @RequestParam Integer page,
                                  @ApiParam(name = "pageSize", value = "每页显示行数", required = true) @RequestParam Integer pageSize) {

        if (catId == null) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = super.PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = itemService.searchItemsByThirdCat(catId, sort, page, pageSize);


        return IMOOCJSONResult.ok(pagedGridResult);
    }

    //由于用户长时间未登录网站，刷新购物车数据
    @ApiOperation(value = "根据商品ids查找最新的商品数据", notes = "根据商品ids查找最新的商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public IMOOCJSONResult catItems(@ApiParam(name = "itemSpecIds", value = "商品分类ID", required = true) @RequestParam String itemSpecIds){

        if(StringUtils.isBlank(itemSpecIds)){
            return IMOOCJSONResult.ok();
        }

        List<ShopcartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);


        return IMOOCJSONResult.ok(list);
    }
}