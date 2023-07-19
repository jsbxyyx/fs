package com.github.jsbxyyx.fs.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author jsbxyyx
 */
@Controller
public class FileController {
    private static final String USER_DIR = System.getProperty("user.dir") + "/static";

    static {
        File file = new File(USER_DIR);
        if (!file.exists() || !file.isDirectory())
            file.mkdirs();
    }

    @GetMapping({"/"})
    public String index(HttpServletRequest request) {
        File file = new File(USER_DIR);
        File[] files = file.listFiles();
        List<Fd> fs = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                Fd fd = new Fd();
                fd.setName(f.getName());
                fs.add(fd);
            }
        }
        request.setAttribute("fs", fs);
        return "index";
    }

    @PostMapping({"/upload"})
    public String upload(@RequestParam("file") MultipartFile[] files, RedirectAttributes attributes) {
        StringBuilder sb = new StringBuilder();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String name = file.getOriginalFilename();
                try {
                    file.transferTo(new File(USER_DIR + "/" + name));
                    sb.append(name + "上传成功\n");
                    System.out.println(name + "上传成功");
                } catch (IOException e) {
                    sb.append(name + "上传失败\n");
                    System.err.println(name + "上传失败");
                }
            }
        }
        attributes.addFlashAttribute("message", sb.toString());
        return "redirect:/";
    }

    @GetMapping({"/download"})
    public void download(HttpServletResponse response, String f) throws IOException {
        File file = new File(USER_DIR + "/" + f);
        String outFilename = f.replace("|", "")
                .replace("{", "")
                .replace("}", "")
                .replace("[", "")
                .replace("]", "");
        response.addHeader("Content-Disposition", "attachment;filename=" +
                new String(outFilename.getBytes("UTF-8"), "ISO-8859-1"));
        response.addHeader("Content-Length", "" + file.length());
        response.setContentType("application/octet-stream");
        try (InputStream in = new BufferedInputStream(new FileInputStream(file));
             OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                out.flush();
            }
        }
    }
}
