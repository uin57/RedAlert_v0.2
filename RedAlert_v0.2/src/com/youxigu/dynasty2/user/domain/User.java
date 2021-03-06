package com.youxigu.dynasty2.user.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.manu.core.ServiceLocator;
import com.youxigu.dynasty2.common.AppConstants;
import com.youxigu.dynasty2.common.service.ICommonService;
import com.youxigu.dynasty2.map.domain.Country;
import com.youxigu.dynasty2.map.domain.CountryCharacter;
import com.youxigu.dynasty2.map.service.IMapService;
import com.youxigu.dynasty2.user.proto.UserMessage;
import com.youxigu.dynasty2.util.DateUtils;

/**
 * 玩家角色信息
 * 
 * @author Administrator
 * 
 */
public class User implements Serializable {
	private static final long serialVersionUID = -1544610208207346456L;
	public static final int USER_WARSTATUS_NORMAL = 10; // 正常
	public static final int USER_WARSTATUS_NEWBEE = 11; // 新手
	public static final int USER_WARSTATUS_NOWAR = 13; // 免战

	public final static String CASH_FLOW_TYPE_GEN = "1";// 产出途径
	public final static String CASH_FLOW_TYPE_USE = "2";// 消耗途径

	public final static byte RESUMR_ARMY_AUTO_TOWER = 0x1; // 重楼自动恢复兵
	public final static byte RESUMR_ARMY_AUTO_RISK = 0x2; // 冒险自动恢复兵
	public final static byte RESUMR_ARMY_AUTO_CASDEF = 0x4; // 城防自动恢复兵
	public final static byte RESUMR_ARMY_AUTO_ARMYOUT = 0x8; // 出征自动恢复兵
	public final static byte RESUMR_ARMY_AUTO_ALL = RESUMR_ARMY_AUTO_TOWER
			| RESUMR_ARMY_AUTO_RISK | RESUMR_ARMY_AUTO_CASDEF
			| RESUMR_ARMY_AUTO_ARMYOUT; // 全部自动恢复
	public final static int MAX_LV = 200;// 最高级别

	public final static int SEX_MAN = 1;// 男
	public final static int SEX_WOMAN = 0;// 女

	private long userId = -1;// 角色ID
	private long accId;// 帐号ID
	private String userName;// 用户名称
//	private int sex; // 1-男，0-女
//	private String icon;// 头像
	/**@see CountryCharacter*/
	private int charId;//用户创建角色选择的角色id
	private String selfSignature;// 个性签名
	private int usrLv;// 等级
	private int cash;// 元宝
	private int consumeCash;// 累计消费的元宝数，一直累计
	private int giftTotal;// 赠送元宝
	private int cashTotal;// 累计充值总额，不包括赠送
	private int payPoint;// 古迹商店货币,
	private int honor;// 经验值
	private Timestamp lastLoginDttm;
	private int countryId;// 国家编号
	private long mainCastleId;// 主城编号
	private Timestamp createDate; // 创建时间
	private long guildId;// 联盟ID, 如果没加入联盟则为0, 或-1
	private Timestamp upLevelTime;// 当前升级的时间
	private String password;// 二级密码 [密码][删除次数][修改次数][dttm]
	private int vip;// vip等级
	private int title;// 军衔
	private int curActPoint;// 当前行动力
	private long troopId;// 主战军团的id
	private int hpPoint;// 当前体力
	private byte autoResumeArmy;// 自动补满兵力
	private int junGong;// 军功
	private long heroId;// 指挥官武将id
	private int color;// 指挥官品质
	private int titleAwardId;// 领取的勋章id
	
	private transient IMapService mapService =null;
	private transient CountryCharacter countryCharacter = null;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getAccId() {
		return accId;
	}

	public void setAccId(long accId) {
		this.accId = accId;
	}

	public void setAccId(double accId) {
		this.accId = (long) accId;
	}

	public long getMainCastleId() {
		return mainCastleId;
	}

	public void setMainCastleId(long mainCastleId) {
		this.mainCastleId = mainCastleId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getSex() {
		return getCountryCharacter().getSex();
	}
	
	public CountryCharacter getCountryCharacter(){
		Country ct = getMapService().getCountryById(countryId);
		return ct.getCountryCharacter(charId);
		
	}
	
	public IMapService getMapService(){
		if(mapService==null){
			mapService = (IMapService)
					 ServiceLocator.getSpringBean("mapService");
		}
		return mapService;
	}

	public String getIcon() {
		return getCountryCharacter().getIcon();
	}

	public String getSelfSignature() {
		return selfSignature;
	}

	public void setSelfSignature(String selfSignature) {
		this.selfSignature = selfSignature;
	}

	public int getUsrLv() {
		return usrLv;
	}

	public void setUsrLv(int usrLv) {
		this.usrLv = usrLv;
	}

	public int getCash() {

		return cash < 0 ? 0 : cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public int getPayPoint() {
		return payPoint;
	}

	public void setPayPoint(int payPoint) {
		this.payPoint = payPoint;
	}

	public int getHonor() {
		return honor;
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}

	// public int getExpPoint() {
	// return expPoint;
	// }
	//
	// public void setExpPoint(int expPoint) {
	// this.expPoint = expPoint;
	// }

	public Timestamp getLastLoginDttm() {
		return lastLoginDttm;
	}

	public void setLastLoginDttm(Timestamp lastLoginDttm) {
		this.lastLoginDttm = lastLoginDttm;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public long getGuildId() {
		return guildId;
	}

	public void setGuildId(long guildId) {
		// if (this.guildId != guildId) {
		// this.guild = null;
		// }
		this.guildId = guildId;

	}

	public int getConsumeCash() {
		return consumeCash;
	}

	public void setConsumeCash(int consumeCash) {
		this.consumeCash = consumeCash;
	}

	public int getGiftTotal() {
		return giftTotal;
	}

	public void setGiftTotal(int giftTotal) {
		this.giftTotal = giftTotal;
	}

	public int getCashTotal() {
		return cashTotal;
	}

	public void setCashTotal(int cashTotal) {
		this.cashTotal = cashTotal;
	}

	/**
	 * 缓存中经常变化的值，用来事件推送
	 * 
	 * @param user
	 * @return
	 */
	public UserMessage getView() {
		return new UserMessage(this);
	}

	public Timestamp getUpLevelTime() {
		return upLevelTime;
	}

	public void setUpLevelTime(Timestamp upLevelTime) {
		this.upLevelTime = upLevelTime;
	}

	public long getUpLevelTm(long time) {
		if (this.upLevelTime == null) {
			return time;
		}
		return upLevelTime.getTime();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] parsePassword() {
		String[] array = { "", "", "", "" };

		boolean reset = false;
		if (this.password == null || this.password.equals("")) {
			reset = true;
		} else {
			String[] tmp = this.password.split(",");
			array[0] = tmp[0];
			array[1] = tmp[1];
			array[2] = tmp[2];
			array[3] = tmp[3];

			Timestamp lastDttm = new Timestamp(Long.parseLong(tmp[3]));
			if (!DateUtils.isSameDay(lastDttm)) {
				reset = true;
			}
		}

		if (reset) {
			ICommonService commonService = (ICommonService) ServiceLocator
					.getSpringBean("commonService");
			array[1] = commonService.getSysParaValue(
					AppConstants.SYS_PASSWORD_UPDATE_TIMES, "5");
			array[2] = commonService.getSysParaValue(
					AppConstants.SYS_PASSWORD_DEL_TIMES, "5");
			array[3] = System.currentTimeMillis() + "";
		}
		return array;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getCurActPoint() {
		return curActPoint;
	}

	public void setCurActPoint(int curActPoint) {
		this.curActPoint = curActPoint;
	}

	public long getTroopId() {
		return troopId;
	}

	public void setTroopId(long troopId) {
		this.troopId = troopId;
	}

	public int getHpPoint() {
		return hpPoint;
	}

	public void setHpPoint(int hpPoint) {
		this.hpPoint = hpPoint;
	}

	/**
	 * true表示设置了自动补满兵
	 * 
	 * @return
	 */
	public boolean isAutoResumeArmy(byte type) {
		return (this.autoResumeArmy & type) != 0;
	}

	public int getJunGong() {
		return junGong;
	}

	public void setJunGong(int junGong) {
		this.junGong = junGong;
	}

	public long getHeroId() {
		return heroId;
	}

	public void setHeroId(long heroId) {
		this.heroId = heroId;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getTitleAwardId() {
		return titleAwardId;
	}

	public void setTitleAwardId(int titleAwardId) {
		this.titleAwardId = titleAwardId;
	}

	public int getCharId() {
		return charId;
	}

	public void setCharId(int charId) {
		this.charId = charId;
	}
	
	
}
