package com.plane.player.domain;

import java.math.BigDecimal;

import android.os.Parcel;
import android.os.Parcelable;

public class OnLineAudio implements Parcelable{
	private String singername;
	private String songname;
	private String filename;
	private String hash;
	private String extname;
	private int bitrate;
	private double filesize;
	private String playlistId;
	
	public String getSingername() {
		return singername;
	}
	public void setSingername(String singername) {
		this.singername = singername;
	}
	public String getSongname() {
		return songname;
	}
	public void setSongname(String songname) {
		this.songname = songname;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getExtname() {
		return extname;
	}
	public void setExtname(String extname) {
		this.extname = extname;
	}
	public int getBitrate() {
		return bitrate;
	}
	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}
	public double getFilesize() {
		BigDecimal bg = new BigDecimal(filesize);
        filesize = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return filesize;
	}
	public void setFilesize(double filesize) {
		this.filesize = filesize;
	}
	public double getTimelength() {
		return timelength/1000;
	}
	public void setTimelength(double timelength) {
		this.timelength = timelength;
	}
	public double getOwnercount() {
		return ownercount;
	}
	public void setOwnercount(double ownercount) {
		this.ownercount = ownercount;
	}
	
	public String getPlaylistId() {
		return this.hash;
	}
	
	private double timelength;
	private double ownercount;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
