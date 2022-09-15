package com.rt.forum.dao;

import com.rt.forum.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户会话列表，针对每个会话返回最新私信
    List<Message> selectConversations(int userId, int offset, int limit);
    //查询当前用户会话数量
    int selectConversationsCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);
    //查询某个会话所包含私信数量
    int selectLettersCount(String conversationId);
    //未读消息数量 里面和外面
    int selectLettersUnreadCount(int userId, String conversationId);

    //增加一条消息
    int insertMessage(Message message);

    //状态设为已读
    int updateStatus(List<Integer> ids, int status);

    //查某一主题最新通知
    Message selectLatestNotice(int userId, String topic);
    //查询某个主题包含通知数量  评论多少
    int selectNoticeCount(int userId, String topic);
    //查询未读通知数量
    int selectNoticeUnreadCount(int userId, String topic);

    //查询某个主题包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
