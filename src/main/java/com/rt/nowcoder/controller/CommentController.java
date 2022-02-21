package com.rt.nowcoder.controller;

import com.rt.nowcoder.entity.Comment;
import com.rt.nowcoder.entity.DiscussPost;
import com.rt.nowcoder.entity.Event;
import com.rt.nowcoder.event.EventProducer;
import com.rt.nowcoder.service.CommentService;
import com.rt.nowcoder.service.DiscussPostService;
import com.rt.nowcoder.util.CommunityConstant;
import com.rt.nowcoder.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;
    //路径上把帖子id传过滤
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        //内容为空不让回复
        if(comment.getContent() == null || StringUtils.isBlank(comment.getContent())){
            return "redirect:/discuss/detail/" + discussPostId;
        }
        //有可能为空但是后面做统一异常处理
        comment.setUserId(hostHolder.getUser().getId());
        //有效
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId) ; //帖子id存在data 我们没有专门设置属性
          if(comment.getEntityType() == ENTITY_TYPE_POST){
              //给帖子评论
              //获得评论的那个帖子，获得发这个这个帖子的人的id
              DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
              event.setEntityUserId(target.getUserId());
          }else  if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
              //给评论评论
              //获得评论的那条评论，获得发这个评论的人的id
              Comment target = commentService.findCommentById(comment.getEntityId());
              event.setEntityUserId(target.getUserId());
          }
          eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
