package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.Stu;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.service.StuService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(value = "首页",tags = {"首页展示相关接口"})
@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取轮播图列表",notes = "获取首页轮播图列表",httpMethod = "GET")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel() {

        List<Carousel> list = carouselService.queryAll(YesOrNo.yes.type);

        return IMOOCJSONResult.ok(list);

    }

    @ApiOperation(value = "获取商品分类(一级)", notes = "获取商品分类(一级)", httpMethod = "GET")
    @GetMapping("/cats")
    public IMOOCJSONResult cats() {

        List<Category> categories = categoryService.queryAllRootLevelCat();

        return IMOOCJSONResult.ok(categories );

    }

    @ApiOperation(value = "根据一级分类获取子分类", notes = "根据一级分类获取子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult cats(@ApiParam(name = "rootCatId",value = "一级分类ID",required = true) @PathVariable("rootCatId") Integer rootCatId) {

        if(rootCatId == null){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        List<CategoryVO> subCategories = categoryService.getSubCatList(rootCatId);

        return IMOOCJSONResult.ok(subCategories );

    }

    @ApiOperation(value = "查询首页最近六个商品", notes = "查询首页最近六个商品", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(@ApiParam(name = "rootCatId", value = "一级分类ID", required = true) @PathVariable("rootCatId") Integer rootCatId) {

        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> sixNewItemsLazy = categoryService.getSixNewItemsLazy(rootCatId);

        return IMOOCJSONResult.ok(sixNewItemsLazy);

    }
}