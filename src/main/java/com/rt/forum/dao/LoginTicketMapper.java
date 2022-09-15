package com.rt.forum.dao;

import com.rt.forum.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated //我们改用redis了
public interface LoginTicketMapper {
    //使用注解写sql
    //自动生成id
    @Insert({
            "insert into login_ticket (user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired}) "
    })
    @Options(useGeneratedKeys = true,keyProperty = "id") //自动生成主键
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket ",
            "where ticket = #{ticket} "
    })
    LoginTicket selectByTicket(String ticket);

    //动态sql演示
    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket = #{ticket}",
            "<if test=\"ticket!=null\">",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);
}
