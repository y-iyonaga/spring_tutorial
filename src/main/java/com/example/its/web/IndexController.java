package com.example.its.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ResponseBody;

@Controller // Springにコントローラークラスだと伝える
public class IndexController {


    @GetMapping("/") //getのトップページに紐づけてる
//    @ResponseBody // 戻り値がそのままレスポンスになる　←retuneを直書きではなくファイル指定にしたためいらなくなった
    public String index() {
        return "index"; //index.htmlのこと。拡張子は書かなくてよい、テクノロジーに依存しなくて済む。
    }
}
