package com.wu.reggie.controller;


import com.wu.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;


/**
 * 文件的上传和下载
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")//从配置文件中读取路径
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {//参数名字一定要和前端传过来的名字一致
        //file是一个临时文件，需要转存到其他位置

        log.info("文件上传");

        //原始文件名
        String originalFilename = file.getOriginalFilename();

        //获取原始文件名的图片格式类型
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID生成新的文件名,再加上源文件的文件后缀
        String fileName = UUID.randomUUID() + suffix;

        //创建一个目录
        File dir = new File(basePath);

        //目录不存在就创建一个
        if(!dir.exists()) {
            dir.mkdir();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        FileInputStream fileInputStream = null;

        ServletOutputStream outputStream = null;
        try {
            //创建一个输入流，从服务器读取文件
            fileInputStream = new FileInputStream(new File(basePath + name));

            //从浏览器中获取一个文件输出流，把服务器中读取到文件展示在浏览器中
            outputStream = response.getOutputStream();

            //设置文件的格式
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] b = new byte[1024];

            while ((len = fileInputStream.read(b)) != -1) {
                outputStream.write(b,0,len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fileInputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
