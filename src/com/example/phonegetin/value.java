package com.example.phonegetin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class value {
	private static String fbuseid;
	private static String fbuselink;
	private static String fbuselog = "login";
	private static String fbacesstoken;
	private static int openservice = 0;
	private static int Position = 0;
	private static String[][] battery_data;
	private static int level, callcheck, gpscheck;
	private static int closeservice;
	private static int login = 0;
	public static int update;
	public static long totalMem;
	public static double totaldisk, longitude, latitude;
	public static int googlelog = 0, clickfb, clickgoogle;
	public static String googleid, googlemail;
	public static int netmin = 0, netposition = 0, sizeposition = 0,
			sizemin = 0, timeposition = 0;
	public static int show = 0;
	public static ArrayList<HashMap<String, Object>> netdata, sizedata,
			memdata;
	public static String imei, address;
	public static int miss, out, get, gpsstat, gpsopen;

	public static void setmemapp(ArrayList<HashMap<String, Object>> list) {
		memdata = list;
	}

	public static ArrayList<HashMap<String, Object>> showmemapp() {
		return memdata;
	}

	public static void setsizeapp(ArrayList<HashMap<String, Object>> list) {
		sizedata = list;
	}

	public static ArrayList<HashMap<String, Object>> showsizeapp() {
		return sizedata;
	}

	public static void setnetapp(ArrayList<HashMap<String, Object>> list) {
		netdata = list;
	}

	public static ArrayList<HashMap<String, Object>> shownetapp() {
		return netdata;
	}

	public static void setimei(String phoneimei) {
		imei = phoneimei;
	}

	public static String showimei() {
		return imei;
	}

	public static int showtimeposition() {
		return timeposition;
	}

	public static void settimeposition(int position) {
		timeposition = position;
	}

	public static int showsizemin() {
		return sizemin;
	}

	public static void setsizemin(int min) {
		sizemin = min;
	}

	public static int showsizeposition() {
		return sizeposition;
	}

	public static void setsizeposition(int position) {
		sizeposition = position;
	}

	public static int shownetposition() {
		return netposition;
	}

	public static void setnetposition(int position) {
		netposition = position;
	}

	public static int shownetmin() {
		return netmin;
	}

	public static void setnetmin(int min) {
		netmin = min;
	}

	public static String showgooglemail() {
		return googlemail;
	}

	public static void setgooglemail(String mail) {
		googlemail = mail;
	}

	public static String showgooleid() {
		return googleid;
	}

	public static void setgoogleid(String id) {
		googleid = id;
	}

	public static int showgoogleclick() {
		return clickgoogle;
	}

	public static void setgoogleclick(int click) {
		clickgoogle = click;
	}

	public static int showfbclick() {
		return clickfb;
	}

	public static void setfbclick(int click) {
		clickfb = click;
	}

	public static int showgooglelog() {
		return googlelog;
	}

	public static void setgooglelog(int login) {
		googlelog = login;
	}

	public static double showtotaldisk() {
		return totaldisk;
	}

	public static void settotaldisk(double disk) {
		totaldisk = disk;
	}

	public static long showtotlMem() {
		return totalMem;
	}

	public static void settotalMem(long Mem) {
		totalMem = Mem;
	}

	public static void setupdate(int setupdate) {
		update = setupdate;
	}

	public static int showupdate() {
		return update;
	}

	public static void setPosition(int position) {

		Position = position;
	}

	public static int showposition() {
		return Position;
	}

	public static void setopenservice(int open_service) {
		openservice = open_service;
	}

	public static int showopenservice() {
		return openservice;
	}

	public static void setfbid(String fbid) {
		fbuseid = fbid;
	}

	public static String showfbid() {
		return fbuseid;
	}

	public static void setfblink(String fblink) {
		fbuselink = fblink;
	}

	public static String showfblink() {
		return fbuselink;
	}

	public static void setfblog(String fblog) {
		fbuselog = fblog;
	}

	public static String showfblog() {
		return fbuselog;
	}

	public static void setfbacesstoken(String acesstoken) {
		fbacesstoken = acesstoken;

	}

	public static String showacesstoken() {
		return fbacesstoken;
	}

}
