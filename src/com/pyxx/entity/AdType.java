package com.pyxx.entity;

/**
 * 广告类型
 * 
 * @author wll
 */
public class AdType extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String isad = "false";
	public String ishead = "false";
	public String sa = "";
	public String nid = "";

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isad == null) ? 0 : isad.hashCode());
		result = prime * result + ((nid == null) ? 0 : nid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Listitem other = (Listitem) obj;
		if (isad == null) {
			if (other.isad != null)
				return false;
		} else if (!isad.equals(other.isad))
			return false;
		if (nid == null) {
			if (other.nid != null)
				return false;
		} else if (!nid.equals(other.nid))
			return false;
		return true;
	}
}
