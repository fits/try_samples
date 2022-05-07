package com.example.demo;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.*;

@RestController
public class SampleController {
    // CharacterEncodingFilter は不要
    @GetMapping("/a")
    public String a() {
        return "サンプル1";
    }

    // HttpServletResponse.getWriter() はデフォルトで ISO-8859-1 のため、
    // CharacterEncodingFilter の適用か setCharacterEncoding が必要
    @GetMapping("/b")
    public void b(HttpServletResponse res) throws java.io.IOException {
        System.out.println(res.getCharacterEncoding());
        //res.setCharacterEncoding("UTF-8");

        try (var writer = res.getWriter()) {
            writer.print("サンプル2");
        }
    }
}