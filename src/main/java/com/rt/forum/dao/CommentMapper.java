package com.rt.forum.dao;

import com.rt.forum.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //查帖子，课程，还是用户?

    /**
     *
     * @param entityType
     * @param entityId
     * @param offset  分页
     * @param limit    分页限制
     * @return
     */
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
