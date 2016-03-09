package com.youxigu.dynasty2.map.domain;

import java.util.HashMap;
import java.util.Map;

import com.youxigu.dynasty2.util.BaseException;

/**
 * 国家定义
 * 
 * @author Administrator
 * 
 */
public class Country {
	private int countryId;// 国家id
	private String countryName;// 国家名
	private int neutral;// 1 不是中立国家，0是中立国家不能创建角色
	private int stateId;// 创建角色时落地的新手村

	private Map<Integer, CountryCharacter> chars = new HashMap<Integer, CountryCharacter>();
	private State state;

	public void addCountryCharacter(CountryCharacter ch) {
		if (ch.getCountryId() != countryId) {
			// throw new BaseException("角色信息不属于这个国家" + ch.getId() +
			// ",countryId="
			// + countryId);
			return;
		}
		if (chars.containsKey(ch.getId())) {
			throw new BaseException("数据id重复" + ch.getId());
		}
		ch.init(this);
		chars.put(ch.getId(), ch);
	}

	public CountryCharacter getCountryCharacter(int id) {
		return chars.get(id);
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public int getNeutral() {
		return neutral;
	}

	public void setNeutral(int neutral) {
		this.neutral = neutral;
	}
	
	/**
	 * true表示为中立国家
	 * @return
	 */
	public boolean isNeutral() {
		return this.neutral == 0;
	}

	public int getStateId() {
		return stateId;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public Map<Integer, CountryCharacter> getChars() {
		return chars;
	}

	public void setChars(Map<Integer, CountryCharacter> chars) {
		this.chars = chars;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
}
