package com.rt.forum;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {
    public static void main(String[] args) {
        //生产者消费者模式
        BlockingQueue queue = new ArrayBlockingQueue(10);

        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

class Producer implements Runnable{
    //实例化时把队列传进来
    private BlockingQueue<Integer> queue;
    
    public Producer(BlockingQueue blockingQueue){
        this.queue = blockingQueue;
    }
    @Override
    public void run() {
        try{
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生成" + queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable{

    private BlockingQueue<Integer> queue;
    public Consumer(BlockingQueue queue){
        this.queue = queue;
    }
    @Override
    public void run() {
       try{
           while(true){
               Thread.sleep(new Random().nextInt(1000));
               queue.take();
               System.out.println(Thread.currentThread().getName() + "消费" + queue.size());
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }

}