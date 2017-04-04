package com.github.cypher.sdk;


import java.util.HashMap;
import java.util.Map;

public class PermissionTable {
	private final int sendEvents;
	private final int invite;
	private final int setState;
	private final int redact;
	private final int ban;
	private final int defaultPower;
	private final int kick;
	private final Map<String, Integer> specialEvents;

	public PermissionTable(int sendEvents, int invite, int setState, int redact, int ban, int defaultPower, int kick, Map<String, Integer> specialEvents) {
		this.sendEvents = sendEvents;
		this.invite = invite;
		this.setState = setState;
		this.redact = redact;
		this.ban = ban;
		this.defaultPower = defaultPower;
		this.kick = kick;
		this.specialEvents = new HashMap<String, Integer>(specialEvents);
	}


	public int getSendEvents() {
		return sendEvents;
	}

	public int getInvite() {
		return invite;
	}


	public int getSetState() {
		return setState;
	}


	public int getRedact() {
		return redact;
	}


	public int getBan() {
		return ban;
	}


	public int getDefaultPower() {
		return defaultPower;
	}


	public int getKick() {
		return kick;
	}


	public Map<String, Integer> getSpecialEvents() {

		return new HashMap<String, Integer>(specialEvents);
	}


}
