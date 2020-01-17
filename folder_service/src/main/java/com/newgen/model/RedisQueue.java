package com.newgen.model;

import java.util.Deque;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class RedisQueue {
	@Resource(name = "queue") 
    private Deque<String> queue; 
 
    public void add(String value) { 
        queue.add(value); 
    } 
 
    public String getFirst() { 
        return queue.getFirst(); 
    } 
 
    public String getLast() { 
        return queue.getLast(); 
    } 
    
    public String pollFirst() { 
        return queue.pollFirst(); 
    } 
 
    public String pollLast() { 
        return queue.pollLast(); 
    } 
    
    public int getSize(){
    	return queue.size();
    }
}
