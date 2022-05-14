package com.wei.reggie.controller;

import com.wei.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info("上传文件:{}",file.toString());
        //转存临时文件
        //使用随机文件名
        String originFileName=file.getOriginalFilename();
        //截取原来图片的后缀名
        String suffix=originFileName.substring(originFileName.lastIndexOf("."));
        String randomFileName= UUID.randomUUID().toString()+suffix;
        //判断配置文件声明的目录在电脑里是否存在
        File dir=new File(basePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        log.info("生成这个文件:{}",basePath+randomFileName);
        file.transferTo(new File(basePath+randomFileName));

        return R.success(randomFileName);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        //通过输入流读取文件内容
        FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
        //通过输出流将文件写回浏览器
        ServletOutputStream outputStream = response.getOutputStream();
        int len=0;
        byte[] bytes=new byte[1024];
        while ((len=fileInputStream.read(bytes))!=-1){      //把读到的东西放在数组里，同时统计长度
            outputStream.write(bytes,0,len);
            outputStream.flush();    //刷新
        }
        outputStream.close();
        fileInputStream.close();

    }


}
