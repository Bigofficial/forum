package com.rt.nowcoder.dao;


import com.rt.nowcoder.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //userId不是0的时候，动态sql
    //分页
    //offset起始行号，limit最多多少页
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    /**
     *
     * @param userId
     * param给参数取别名
     * 动态拼接，只有一个参数，要带param。
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    //查询帖子详情
    DiscussPost selectDiscussPostById(int id);

    //增加帖子评论数量
    int updateCommentCount(int id, int commentCount);
}
