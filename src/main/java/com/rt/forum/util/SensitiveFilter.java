package com.rt.forum.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    //定义日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换敏感词符号
    private static final String REPLACEMENT = "***";
    //根节点
    private TrieNode rootNode = new TrieNode();
    //初始化这个类时，初始化就可以。
    //该注解为调用构造方法后这个方法就会执行
    @PostConstruct
    public void init(){
        //读文件中的敏感词
        //从classes下路径   maven插件清除，再编译
        try (
                //获得字节流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //转换成字符流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        ){
            //存每次读的变量
            String keyWord;
            while((keyWord = reader.readLine() )!= null){
                //添加到前缀树
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败"+e.getMessage());
        }
    }
    //定义前缀树
    private class TrieNode{
        //关键词结束标识
        private boolean isKeyWordEnd = false;

        //描述结点孩子 key:字符，value:孩子结点
        private Map<Character,TrieNode> subnodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subnodes.put(c,node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c){
            return subnodes.get(c);
        }
    }
    //将敏感词添加到前缀树
    private void addKeyWord(String keyWord){
        TrieNode tempNode = rootNode;
        for(int i=0; i<keyWord.length(); i++){
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //指针指向子节点，进入下一轮循环
            tempNode = subNode;

            //最后字符打标记
            if(i == keyWord.length() - 1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 返回过滤完的字符串
     * @param text 待过滤文本
     * @return  过滤后的文本
     */
    public String filter(String text){
        //判空
        if (StringUtils.isBlank(text)){
            return null;
        }
        //声明三个指针
        //指向树 1
        TrieNode tempNode = rootNode;
        //指向字符串首位 2
        int begin = 0;
        //指向当前字符 3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        //指针3 先结束
        while (begin < text.length()){
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                // 指针1处于根结点,计入结果，让指针2想下走一步
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或者中间，指针3都向下走一步
                position++;
                continue;
            }
            //不是符号 检查下级结点
            tempNode = tempNode.getSubNode(c);
            //下级没有结点
            if(tempNode == null){
              //以begin未开头字符串不是敏感词
              sb.append(text.charAt(begin));
              //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeyWordEnd()){
                //发现敏感词，将begin到position这段字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            }else { //没有检测完
                //检查下一个字符
                if(position < text.length() - 1){
                    position++;
                }
            }
        }

        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        //0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
