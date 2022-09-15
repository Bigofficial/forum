package com.rt.forum.controller;

import com.rt.forum.entity.Event;
import com.rt.forum.entity.Page;
import com.rt.forum.entity.User;
import com.rt.forum.event.EventProducer;
import com.rt.forum.service.FollowService;
import com.rt.forum.service.UserService;
import com.rt.forum.util.CommunityConstant;
import com.rt.forum.util.CommunityUtil;
import com.rt.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{
    @Autowired
    private FollowService followSerice;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;
    //关注 异步的哦
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        //拦截器检查，登入才能访问
        followSerice.follow(user.getId(), entityType,entityId);
        //触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
                //连接到某个人
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0,"已关注");
    }

    //取消关注 异步的哦
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        //拦截器检查，登入才能访问
        followSerice.unfollow(user.getId(), entityType,entityId);

        return CommunityUtil.getJSONString(0,"已取消关注");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }else{
            model.addAttribute("user", user);
            //分页设置
            page.setLimit(5);
            page.setPath("/followees"+ userId);
            page.setRows((int) followSerice.findFolloweeCount(userId, ENTITY_TYPE_USER));

            List<Map<String, Object>> userList = followSerice.findFollowees(userId,page.getOffset(), page.getLimit());
            if(userList != null){
                for(Map<String, Object> map : userList){
                    User u = (User) map.get("user");
                    map.put("hasFollowed",hasFollowed(u.getId()));
                }
            }
            model.addAttribute("users", userList);
            return  "/site/followee";
        }
    }
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }else{
            model.addAttribute("user", user);
            //分页设置
            page.setLimit(5);
            page.setPath("/followers" + userId);
            page.setRows((int) followSerice.findFollowerCount(ENTITY_TYPE_USER,userId));

            List<Map<String, Object>> userList = followSerice.findFollowers(userId,page.getOffset(), page.getLimit());
            if(userList != null){
                for(Map<String, Object> map : userList){
                    User u = (User) map.get("user");
                    map.put("hasFollowed",hasFollowed(u.getId()));
                }
            }
            model.addAttribute("users", userList);
            return  "/site/follower";
        }
    }
    private boolean hasFollowed(int userId){
        //没登入
        if(hostHolder.getUser() == null){
            return false;
        }else {
            return followSerice.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
    }
}
