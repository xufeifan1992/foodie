package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.center.CenterUserBO;
import com.imooc.resources.FileUpload;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户中心信息相关接口", tags = "用户中心信息相关接口")
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {
    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;


    @ApiOperation(value = "用户头像修改", notes = "用户头像修改", httpMethod = "GET")
    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace(@ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam String userId,
                                      @ApiParam(name = "file", value = "用户头像", required = true)
                                      MultipartFile file,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {

        //定义头像保存地址
        //String fileSpace = IMAGE_USER_FACE_LOCATION;
        String fileSpace = fileUpload.getImageUserFaceLocation();
        //区分不同用户上传的头像,在路径上添加userId
        String uploadPathPrefix = File.separator + userId;

        //开始文件上传
        if (file != null) {
            FileOutputStream fileOutputStream = null;
            try {
                //获取文件原始名称
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件重命名  imooc-face.png -> ["imooc-face","png"]
                    String[] fileNameArray = fileName.split("\\.");
                    //获取文件后缀
                    String suffix = fileNameArray[fileNameArray.length - 1];
                    if (!suffix.equalsIgnoreCase("png") && !suffix.equalsIgnoreCase("jpg") &&
                            !suffix.equalsIgnoreCase("jpeg")) {
                        return IMOOCJSONResult.errorMsg("图片格式不正确");
                    }


                    //文件名称重组
                    String newFileName = "face-" + userId + "." + suffix;

                    //上传头像最终保存位置
                    String finaleFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;

                    //用于提供给web服务的访问地址
                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finaleFacePath);
                    if (outFile.getParentFile() != null) {
                        outFile.getParentFile().mkdirs();
                    }

                    //文件输出，保存到目录
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    //关闭流
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //获取服务器地址
            String imageServerUrl = fileUpload.getImageServerUrl();
            //由于浏览器缓存的情况，加上时间戳来保证更新图片
            String finalUserFaceUrl = imageServerUrl + uploadPathPrefix + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
            //更新用户头像
            Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
            CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);

            //增加令牌token，会整合进redis分布式会话
            convertUsersVO(userResult);

        } else {
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }


        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", httpMethod = "GET")
    @PostMapping("update")
    public IMOOCJSONResult update(@ApiParam(name = "userId", value = "用户ID") @RequestParam String userId,
                                  @RequestBody @Valid CenterUserBO centerUserBO,
                                  BindingResult result,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        //判断BindingResult是否包含错误信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errors = getErrors(result);
            return IMOOCJSONResult.errorMap(errors);
        }

        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);

        //setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);

        //增加令牌token，会整合进redis分布式会话
        convertUsersVO(userResult);

        return IMOOCJSONResult.ok(userResult);
    }

    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            //发生错误的属性
            String errorFild = fieldError.getField();
            //发生错误的信息
            String errorMessage = fieldError.getDefaultMessage();

            map.put(errorFild, errorMessage);

        }
        return map;
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
