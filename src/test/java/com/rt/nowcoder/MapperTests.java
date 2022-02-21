package com.rt.nowcoder;

import com.rt.nowcoder.dao.LoginTicketMapper;
import com.rt.nowcoder.dao.MessageMapper;
import com.rt.nowcoder.entity.LoginTicket;
import com.rt.nowcoder.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void test01(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        System.out.println(loginTicket);
        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);
    }

    @Test
    public void testSelect(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc",1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void test02(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 10);
        System.out.println(messages.size());
        for (Message m:
             messages) {
            System.out.println(m);
        }
        System.out.println();
        System.out.println(messageMapper.selectConversationsCount(111));

        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message m:
             messages1) {
            System.out.println(m);
        }
        System.out.println(messageMapper.selectLettersCount("111_112"));
        System.out.println(messageMapper.selectLettersUnreadCount(111, "111_131"));
    }
}
