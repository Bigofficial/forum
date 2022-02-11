package com.rt.newcoder.service;


import com.rt.newcoder.dao.DiscussPostMapper;
import com.rt.newcoder.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {


    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * DiscussPost返回userId我们要的是名字
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostsRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
