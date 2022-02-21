package com.rt.nowcoder.util;

import com.rt.nowcoder.entity.User;
import org.springframework.stereotype.Component;

/*
容器作用，持有用户信息，代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();
    //主要是set，get方法。它实现线程隔离是获取当前线程，获取map对象，存到map里，每个线程map不同。

    public void setUsers(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
