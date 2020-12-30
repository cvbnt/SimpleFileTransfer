package com.hao123.file.Controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author cvbnt
 */
@Controller
public class controller {
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/pages/singleUpload")
    public String singleUpload() {
        return "pages/singleUpload";
    }

    @RequestMapping("/pages/multiUpload")
    public String multiUpload() {
        return "pages/multiUpload";
    }


    @RequestMapping("/uploadSingleFile")
    public String singleUpload(MultipartFile file) throws IOException {
        File file1 = new File("F:/upload/" + file.getOriginalFilename());
        if (!file1.exists()) {
            file1.createNewFile();
        }
        file.transferTo(file1);
        //将接受的文件存储
        return "/pages/uploadSuccess";
    }

    @RequestMapping("/uploadMultiFile")
    public String multiUpload(MultipartFile[] multiFile) throws IOException {
        for (MultipartFile f : multiFile) {
            if (!f.isEmpty()) {
                File file = new File("F:/upload/" + f.getOriginalFilename());
                file.createNewFile();
                f.transferTo(file);
            }
        }
        return "/pages/uploadSuccess";
    }

    @RequestMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) throws IOException {
        //下载文件的路径(这里绝对路径)
        String path = "F:/upload/" + filename;
        File file = new File(path);
        //创建字节输入流，这里不实用Buffer类
        InputStream in = new FileInputStream(file);
        //available:获取输入流所读取的文件的最大字节数
        byte[] body = new byte[in.available()];
        //把字节读取到数组中
        in.read(body);
        //设置请求头
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        //设置响应状态
        HttpStatus statusCode = HttpStatus.OK;
        in.close();
        return new ResponseEntity<>(body, headers, statusCode);
        //返回
    }

    @RequestMapping("/pdf/{fileName}")
    public void downloadPDFResource(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @PathVariable("fileName") String fileName) throws UnsupportedEncodingException {
        //Check the renderer
        String dataDirectory = "F:/upload";
        Path file = Paths.get(dataDirectory, fileName);
        if (Files.exists(file)) {
            response.setContentType("Save As APPLICATION/OCTET-STREAM");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
