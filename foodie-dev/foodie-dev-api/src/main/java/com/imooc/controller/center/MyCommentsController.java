package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.center.OrderItemsCommentBO;
import com.imooc.service.center.MyCommentService;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "用户中心评价模块", tags = "用户中心评价模块")
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyCommentService myCommentService;


    @ApiOperation(value = "查询订单列表",notes = "查询订单列表",httpMethod = "POST")
    @PostMapping("pending")
    public IMOOCJSONResult comments(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId

    ){

        //判断订单和用户是否关联
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        //判断订单是否已经评价
        Orders myOrder = (Orders) checkResult.getData();
        if(myOrder.getIsComment() == YesOrNo.yes.type){
            return IMOOCJSONResult.errorMsg("该订单已经评价");
        }

        List<OrderItems> orderItems = myCommentService.queryPendingComment(orderId);

        return IMOOCJSONResult.ok(orderItems);


    }

    @ApiOperation(value = "保存评论列表", notes = "保存评论列表", httpMethod = "POST")
    @PostMapping("saveList")
    public IMOOCJSONResult saveList(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentList

    ) {

        //判断订单和用户是否关联
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        //评论内容不能为空
        if(commentList == null || commentList.isEmpty() || commentList.size() == 0){
            return IMOOCJSONResult.errorMsg("评论内容不能为空");
        }

        myCommentService.saveComments(orderId,userId,commentList);

        return IMOOCJSONResult.ok();


    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myCommentService.queryMyComments(userId,
                page,
                pageSize);

        return IMOOCJSONResult.ok(grid);
    }



}
