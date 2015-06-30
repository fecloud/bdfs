package com.yuncore.bdfs.task;

import java.util.Stack;

public class TaskContainer {

	private Stack<Task> stacks;

	protected ContainerListener listener;

	public TaskContainer() {
		stacks = new Stack<Task>();
	}

	public synchronized Task getTask() {
		if (stacks.isEmpty()) {
			return null;
		}
		return stacks.pop();
	}

	public Task addTask(Task dir) {
		synchronized (this) {
			stacks.push(dir);
		}
		if (null != listener) {
			listener.onTaskAdd(stacks.peek());
		}
		return dir;
	}

	public synchronized ContainerListener getListener() {
		return listener;
	}

	public synchronized void setListener(ContainerListener listener) {
		this.listener = listener;
	}

	public interface ContainerListener {

		void onTaskAdd(Task dir);

	}

}
