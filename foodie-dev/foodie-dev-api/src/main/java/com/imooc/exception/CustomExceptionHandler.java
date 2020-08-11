package com.imooc.exception;

import com.imooc.utils.IMOOCJSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class CustomExceptionHandler {
    //MaxUploadSizeExceededException

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public IMOOCJSONResult handleMaxUploadFile(MaxUploadSizeExceededException ex){
        return IMOOCJSONResult.errorMsg("文件上传大小不能超过500K,请压缩图片或者降低文件质量再上传");
    }
}
