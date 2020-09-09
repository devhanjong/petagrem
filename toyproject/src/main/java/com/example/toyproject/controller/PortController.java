package com.example.toyproject.controller;

import com.example.toyproject.model.Comment;
import com.example.toyproject.model.YoutubeVideoInfo;
import com.example.toyproject.repository.YoutubeVideoInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PortController {

    @Autowired
    YoutubeVideoInfoRepository youtubeVideoInfoRepository;

    @GetMapping("/port")
    public String port(@ModelAttribute("thumbnail") YoutubeVideoInfo youtubeVideoInfo, Model model
                     , @RequestParam(name = "page", defaultValue = "1") int page   ) {

        int startPage = (page - 1) / 10 * 10 + 1;
        int endPage = startPage + 9;

        PageRequest req = PageRequest.of(page , 9, Sort.by(Sort.Direction.DESC, "publishedDate"));

        Page<YoutubeVideoInfo> result = youtubeVideoInfoRepository.findAll(req);
        int totalPage = result.getTotalPages();
        System.out.println(result);
        if (endPage > totalPage) {
            endPage = totalPage;
        }
        List<YoutubeVideoInfo> list = result.getContent();

        System.out.println(list);
        model.addAttribute("list", list);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("page", page);

        return "portfolio";
    }


}
