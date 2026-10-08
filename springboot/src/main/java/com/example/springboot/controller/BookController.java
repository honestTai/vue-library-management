package com.example.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.Result;
import com.example.springboot.controller.request.BookPageRequest;
import com.example.springboot.entity.Admin;
import com.example.springboot.entity.Book;
import com.example.springboot.service.BookService;
import com.example.springboot.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@CrossOrigin //解决跨域问题
@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    BookService bookService;
    private static final String BASE_FILE_PATH = System.getProperty("user.dir");

    @GetMapping("/list")
    public Result list() {
        List<Book> books = bookService.list();
        return Result.success(books);
    }

    @PostMapping("/file/upload")
    public Result uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            long flag = System.currentTimeMillis();
            String filePath = BASE_FILE_PATH + flag + "-" + originalFilename;
            FileUtil.mkParentDirs(filePath);
            file.transferTo(FileUtil.file(filePath));

//            String token = TokenUtils.genToken(String.valueOf(currentAdmin.getId()), currentAdmin.getPassword(),15);
            return Result.success("http://localhost:9090/api/book/file/download/" + flag + "-" + originalFilename);
        } catch (Exception e) {
            log.info("文佳上传失败", e);
        }
        return Result.error("文件上传失败");
    }

    @GetMapping("/file/download/{flag}")
    public void downloadFile(@PathVariable String flag, HttpServletResponse response) {
        OutputStream os;
        try {
            if (StrUtil.isNotEmpty(flag)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(flag, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(BASE_FILE_PATH + flag);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }

    @GetMapping("/page")
    public Object page(BookPageRequest request) {
        Object page = bookService.page(request);
        return page;
    }

    @PostMapping("/save")
    public Result save(@RequestBody Book book) {
        bookService.save(book);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Book book) {
        bookService.update(book);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Book book = bookService.getById(id);
        return Result.success(book);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        bookService.deleteById(id);
        return Result.success();
    }

}
