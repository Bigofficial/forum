package com.rt.forum.util;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    //默认状态登入凭证超时时间 12小时
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    //记住我的凭证时间 100天
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    //实体类型:帖子
    int  ENTITY_TYPE_POST = 1;

    //实体类型：评论
    int ENTITY_TYPE_COMMENT = 2;

    //实体类型：人
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";
    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     *
     */
    int SYSTEM_USER_ID = 1;
}
