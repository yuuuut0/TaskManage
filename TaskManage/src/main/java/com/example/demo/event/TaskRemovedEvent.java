package com.example.demo.event;

import com.example.demo.entity.Task;

public class TaskRemovedEvent {

	private final Task task;

    public TaskRemovedEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
