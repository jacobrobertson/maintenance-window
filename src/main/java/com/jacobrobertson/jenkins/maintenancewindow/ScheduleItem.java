package com.jacobrobertson.jenkins.maintenancewindow;

import hudson.scheduler.CronTab;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import antlr.ANTLRException;

public class ScheduleItem {

	private CronTab cronTab;
	private long duration;
	public ScheduleItem(CronTab cronTab, long duration) {
		this.cronTab = cronTab;
		this.duration = duration;
	}
	
	public boolean isBlocked(long now) {
		Calendar past = cronTab.floor(now);
		long start = past.getTimeInMillis();
		long end = start + duration;
		if (now >= start && now <= end) {
			return true;
		} else {
			return false;
		}
	}
	public String toString() {
		return cronTab.toString() + "; Duration: " + (duration / 60000) + " minutes";
	}
	public static List<ScheduleItem> parseSpec(String spec) {
		List<ScheduleItem> items = new ArrayList<ScheduleItem>();
		String[] lines = spec.split("\n");
		for (String line: lines) {
			line = line.trim();
			if (line.charAt(0) == '#') {
				continue;
			}
			String[] split = line.split(":");
			if (split.length != 2) {
				throw new IllegalArgumentException("Spec must be in form of cron:duration");
			}
			CronTab tab;
			try {
				tab = new CronTab(split[0].trim());
			} catch (ANTLRException e) {
				throw new IllegalArgumentException("Bad cron spec", e);
			}
			long duration = Long.parseLong(split[1].trim()) * 60000;
			ScheduleItem item = new ScheduleItem(tab, duration);
			items.add(item);
		}
		return items;
	}
	
}
