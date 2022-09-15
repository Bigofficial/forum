package com.rt.forum.service;

import com.rt.forum.entity.User;
import com.rt.forum.util.CommunityConstant;
import com.rt.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    //关注              发起follow的user
    public void follow(int userId, int entityType, int entityId){
        //关注目标 粉丝
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();
                operations.opsForZSet().add(followeeKey, entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId,System.currentTimeMillis());

                return operations.exec();
            }
        });
    }
    //取关
    public void unfollow(int userId, int entityType, int entityId){
        //关注目标 粉丝
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    //查询关注目标实体的数量
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询实体粉丝数量
    public long findFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //当前用户有没有关注这个目标
    public boolean hasFollowed(int userId, int entityType, int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }


    //查询某个用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit){
        //查人
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        //时间
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        //判断空？
        if(targetIds == null){
            return null;
        }else {
            //id转换为更详细数据
            List<Map<String, Object>> list = new ArrayList<>();
            for(Integer targetId : targetIds){
                Map<String, Object> map = new HashMap<>();
                User user = userService.findUserById(targetId);
                map.put("user", user);
                //关注时间
                Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
                //毫秒数还原为时间
                map.put("followTime", new Date(score.longValue()));
                list.add(map);
            }
            return list;
        }

    }
    //查询某个用户关注粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit){
        //拼key
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);

        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(targetIds == null){
            return null;
        }else {
            List<Map<String, Object>> list = new ArrayList<>();
            for(Integer targetId : targetIds){
                Map<String, Object> map = new HashMap<>();
                User user = userService.findUserById(targetId);
                map.put("user",user);
                //时间
                Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
                map.put("followTime", new Date(score.longValue()));
                list.add(map);
            }
            return list;
        }

    }
}
