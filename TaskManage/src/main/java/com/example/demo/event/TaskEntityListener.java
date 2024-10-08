package com.example.demo.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Task;

import jakarta.persistence.PreRemove;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskEntityListener {

	private final ApplicationEventPublisher eventPublisher;

    @PreRemove
    public void onPreRemove(Task task) {
    	eventPublisher.publishEvent(new TaskRemovedEvent(task));
    }
    
}
