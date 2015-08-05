package com.pyxx.entity;

public class Items extends AdType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Integer c_id;
	public String icon = "";
	public String des = "";
	public String title = "";
	public String n_mark = "";
	public String u_date = "";
	public String show_type = "0";// 0表示普通列表,1表示其它图片列表，2..可以自己定义类型显示类型
	public String other = "";//价格
	public String other1 = "";//订单状态
	public String other2 = "";//总金额
	public String other3 = "";
	public String fuwu = "";// 服务介绍内容
	public String shangjia = "";// 商家介绍内容
	public String sellerName = "";//商家名称
	public Integer sellerId;
	public String img_list_1 = "";// 服务图片集
	public String img_list_2 = "";// 介绍图片集
	public String vip_id = "";// 商家等级
	public String preferential = "";// 优惠等级
	public String phone = "";// 电话
	public String level = "";// 评价等级
	public String address = "";// 地址
	public String longitude = "";// 经度
	public String latitude = "";// 纬度

	public void getMark() {
		n_mark = nid + "_" + sa;
	}
}
