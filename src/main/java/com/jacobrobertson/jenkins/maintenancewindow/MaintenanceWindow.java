package com.jacobrobertson.jenkins.maintenancewindow;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.model.Queue.BuildableItem;
import hudson.model.queue.QueueTaskDispatcher;
import hudson.model.queue.CauseOfBlockage;

import java.util.List;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

@Extension
public class MaintenanceWindow extends QueueTaskDispatcher implements Describable<MaintenanceWindow> {

	private static final Logger LOGGER = Logger.getLogger("MaintenanceWindow");
	
	public MaintenanceWindow() {
		LOGGER.info("construct");
	}
	
	@Override
	public CauseOfBlockage canTake(Node node, BuildableItem bitem) {
		long now = System.currentTimeMillis();
		CauseOfBlockage cause;
		boolean block = false;
		String itemString = null;
		for (ScheduleItem item: getDescriptor().getScheduleItems()) {
			block = item.isBlocked(now);
			if (block) {
				itemString = item.toString();
				break;
			}
		}
		if (block) {
			cause = CauseOfBlockage.fromMessage(Messages._MaintenanceWindowMessage(itemString));
		} else {
			cause = null;
		}
		LOGGER.info("canTake." + cause);
		return cause;
	}
	
	@Extension
	public static final class DescriptorImpl extends Descriptor<MaintenanceWindow> {
		private String spec;
		private transient List<ScheduleItem> items;

		@Override
		public String getDisplayName() {
			return "Maintenance Window";
		}
		public String getSpec() {
			return spec;
		}
		public List<ScheduleItem> getScheduleItems() {
			if (items == null) {
				items = ScheduleItem.parseSpec(spec);
			}
			return items;
		}
		@Override
		public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
			boolean value = super.configure(req, json);
			spec = json.getString("spec");
			getScheduleItems();
			save();
			return value;
		}
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(getClass());	
	}
	
}
