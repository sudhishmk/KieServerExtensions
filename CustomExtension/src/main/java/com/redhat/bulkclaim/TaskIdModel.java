package com.redhat.bulkclaim;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskIdModel {
	
	private List<Long> taskIds;

	public List<Long> getTaskIds() {
		return taskIds;
	}

	public void setTaskIds(List<Long> taskIds) {
		this.taskIds = taskIds;
	}
	
	

}
