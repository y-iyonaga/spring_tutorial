package com.example.its.web.issue;

import com.example.its.domain.issue.IssueEntity;
import com.example.its.domain.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/issues") // これのおかげで@GetMappingのパスを省略
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    // GET /issues ←このリクエストが来た時用
    @GetMapping
    public String showList(Model model) {
        model.addAttribute("issueList", issueService.findAll()); // IssueService.javaからfindAllを受け取る
        return "issues/List"; // テンプレートフォルダから見てissuesのディレクトリ内にあるList.htmlをさしている
    }

    // GET /issues/creationForm  ←このリクエストが来た時用
    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute IssueForm form) { // @ModelAttributedeでIssueFormがmodelの中に登録される。(newは自動で差し込んでくれる)
//        model.addAttribute("issueForm", new IssueForm());　　　　　　　↑これのおかげで左はいらない
        return "issues/creationForm";
    }

    // POST /issues  ←これが来た時用
    @PostMapping
    public String create(IssueForm form,Model model) {
        issueService.create(form.getSummary(),form.getDescription());
        return "redirect:/issues"; // ここでリロードした際にもう一度postすることを防ぐ(2重サブミット対策)
    }
}

