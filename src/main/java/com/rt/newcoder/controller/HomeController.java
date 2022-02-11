package com.rt.newcoder.controller;

import com.rt.newcoder.entity.DiscussPost;
import com.rt.newcoder.entity.Page;
import com.rt.newcoder.entity.User;
import com.rt.newcoder.service.DiscussPostService;
import com.rt.newcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path="/index",method = RequestMethod.GET)
    public String getIndexPage(Model model
                               , Page page
                               ){
        page.setPath("/index");
        page.setRows(discussPostService.findDiscussPostsRows(0));
        //查到数据不完整没有名字
        List<DiscussPost> list= discussPostService.findDiscussPosts(0, page.getCurrent(), page.getLimit());
        //DiscussPosts和User的结合体的数组
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post:list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }
}
