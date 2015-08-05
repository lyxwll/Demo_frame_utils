package com.pyxx.entity;

import java.util.Date;

import com.pyxx.entity.AdType;

/**
 * 管理微博用户信息
 * 
 * @author He-Jian
 * 
 */
public class WeiboItemInfo extends AdType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;/* 微博ID */
	public String name;/* 微博昵称 */
	public String userID;/* 用户ID */
	public Date created_at;/* 创建时间 */
	public String created_str;
	public String userImg;
	public String text;/* 微博内容 */
	public String source;/* 微博信息来源 */
	public String thumbnail_pic;/* 缩略图 */
	public String bmiddle_pic;/* 中型图 */
	public String original_pic;/* 原始图 */
	public String profile_image_url;/* 用户自定义头像 */
	public String retweetedID;/* 转发博文ID */
	public String cCount = "...";
	public String rCount = "...";
	public boolean zmark = false;/* 是否有转发 */

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserImg() {
		return userImg;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public String getCreated_str() {
		return created_str;
	}

	public void setCreated_str(String created_str) {
		this.created_str = created_str;
	}

	public boolean getzmark() {
		return zmark;
	}

	public void setZmark(boolean zmark) {
		this.zmark = zmark;
	}

	public String getcCount() {
		return cCount;
	}

	public void setcCount(String cCount) {
		this.cCount = cCount;
	}

	public String getrCount() {
		return rCount;
	}

	public void setrCount(String rCount) {
		this.rCount = rCount;
	}

	public String getRetweetedID() {
		return retweetedID;
	}

	public void setRetweetedID(String retweetedID) {
		this.retweetedID = retweetedID;
	}

	public WeiboItemInfo retweeted;/* 转发内容 */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getThumbnail_pic() {
		return thumbnail_pic;
	}

	public void setThumbnail_pic(String thumbnail_pic) {
		this.thumbnail_pic = thumbnail_pic;
	}

	public String getBmiddle_pic() {
		return bmiddle_pic;
	}

	public void setBmiddle_pic(String bmiddle_pic) {
		this.bmiddle_pic = bmiddle_pic;
	}

	public String getOriginal_pic() {
		return original_pic;
	}

	public void setOriginal_pic(String original_pic) {
		this.original_pic = original_pic;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public WeiboItemInfo getRetweeted() {
		return retweeted;
	}

	public void setRetweeted(WeiboItemInfo retweeted) {
		this.retweeted = retweeted;
	}
}
