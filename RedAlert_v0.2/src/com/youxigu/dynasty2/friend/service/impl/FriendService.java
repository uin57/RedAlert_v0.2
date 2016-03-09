package com.youxigu.dynasty2.friend.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.engine.cache.memcached.MemcachedManager;
import com.manu.util.Util;
import com.manu.util.UtilDate;
import com.youxigu.dynasty2.chat.ChatChannelManager;
import com.youxigu.dynasty2.chat.EventMessage;
import com.youxigu.dynasty2.chat.client.IChatClientService;
import com.youxigu.dynasty2.common.AppConstants;
import com.youxigu.dynasty2.common.service.ICommonService;
import com.youxigu.dynasty2.common.service.ISensitiveWordService;
import com.youxigu.dynasty2.core.wolf.IWolfClientService;
import com.youxigu.dynasty2.friend.dao.impl.FriendDao;
import com.youxigu.dynasty2.friend.domain.Friend;
import com.youxigu.dynasty2.friend.domain.FriendApp;
import com.youxigu.dynasty2.friend.domain.FriendHp;
import com.youxigu.dynasty2.friend.domain.FriendRecommend;
import com.youxigu.dynasty2.friend.enums.FriendGroupType;
import com.youxigu.dynasty2.friend.enums.FriendStatusType;
import com.youxigu.dynasty2.friend.proto.FriendInfoMsg;
import com.youxigu.dynasty2.friend.proto.FriendMsg.GroupType;
import com.youxigu.dynasty2.friend.proto.SendDelFriend;
import com.youxigu.dynasty2.friend.proto.SendFriendMsg;
import com.youxigu.dynasty2.friend.service.IFriendService;
import com.youxigu.dynasty2.friend.service.IHpFriendService;
import com.youxigu.dynasty2.log.AbsLogLineBuffer;
import com.youxigu.dynasty2.log.ILogService;
import com.youxigu.dynasty2.log.LogTypeConst;
import com.youxigu.dynasty2.log.TlogHeadUtil;
import com.youxigu.dynasty2.log.imp.LogHpAct;
import com.youxigu.dynasty2.log.imp.SnsAct;
import com.youxigu.dynasty2.user.domain.User;
import com.youxigu.dynasty2.user.domain.UserChat.RecentTimeFriend;
import com.youxigu.dynasty2.user.service.IUserService;
import com.youxigu.dynasty2.util.BaseException;
import com.youxigu.dynasty2.util.DateUtils;
import com.youxigu.dynasty2.util.PagerResult;
import com.youxigu.dynasty2.util.StringUtils;
import com.youxigu.dynasty2.vip.service.IVipService;
import com.youxigu.wolf.net.AsyncWolfTask;
import com.youxigu.wolf.net.UserSession;

/**
 * 好友Service实现类
 * 
 * @author Dagangzi
 * 
 */
public class FriendService implements IFriendService, IHpFriendService {
	public static final Logger log = LoggerFactory
			.getLogger(FriendService.class);
	private static final String WOLF_FRIENDRECOMMENDCACHE = "wolf_FriendRecommendCache";
	private static final FriendAppComparator friendAppComparator = new FriendAppComparator();
	private FriendDao friendDao;
	private ISensitiveWordService sensitiveWordService;
	private IUserService userService;
	private ICommonService commonService;
	private IWolfClientService mainServerClient;
	private IChatClientService messageService;
	private ILogService logService;
	private IVipService vipService;
	/*** 推荐好友规则 */
	private List<FriendRecommend> friendRecommends = null;

	/*** 最小的进入缓存的玩家等级 */
	private int minUsrLv = Integer.MAX_VALUE;
	private int maxUsrLv = Integer.MIN_VALUE;
	/** 缓存所有的查找好友cd时间 */
	private ConcurrentHashMap<Long, Long> cds = new ConcurrentHashMap<Long, Long>();

	public void setVipService(IVipService vipService) {
		this.vipService = vipService;
	}

	public void setMessageService(IChatClientService messageService) {
		this.messageService = messageService;
	}

	public void setLogService(ILogService logService) {
		this.logService = logService;
	}

	public void setMainServerClient(IWolfClientService mainServerClient) {
		this.mainServerClient = mainServerClient;
	}

	public IChatClientService getMessageService() {
		return messageService;
	}

	public void setMinUsrLv(int minUsrLv) {
		this.minUsrLv = minUsrLv;
	}

	public void setCommonService(ICommonService commonService) {
		this.commonService = commonService;
	}

	public void setFriendDao(FriendDao friendDao) {
		this.friendDao = friendDao;
	}

	public void setSensitiveWordService(
			ISensitiveWordService sensitiveWordService) {
		this.sensitiveWordService = sensitiveWordService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public void init() {
		friendRecommends = friendDao.getFriendRecommends();
		for (FriendRecommend fr : friendRecommends) {
			int minLv = fr.getMinLv();
			int maxLv = fr.getMaxLv();
			if (minLv < minUsrLv) {
				minUsrLv = minLv;
			}
			if (maxLv > maxUsrLv) {
				maxUsrLv = maxLv;
			}
		}
	}

	@Override
	public Friend doAddFriend(long userId, User firendUser) {
		Friend friendFri = friendDao.getFriendByFriendUserId(userId,
				firendUser.getUserId());
		if (friendFri == null) {
			friendFri = new Friend();
			friendFri.setUserId(userId);
			friendFri.setFriendUserId(firendUser.getUserId());
			friendFri.setFriendUserName(firendUser.getUserName());
			friendFri.setFriendMainCasId(firendUser.getMainCastleId());
			friendFri.setGroup(FriendGroupType.GROUP_ALL);
			friendFri.setNote("");
			friendFri.setAddTime(UtilDate.nowTimestamp());
			friendDao.insertFriend(friendFri);
		} else {
			throw new BaseException("好友已经存在了。");
		}
		return friendFri;
	}

	@Override
	public void doAppFriend(User myUser, User friendUser) {
		if (friendUser == null || friendUser.getUserId() <= 0) {
			throw new BaseException("该君主不存在！");
		}
		long myUserId = myUser.getUserId();// 发出申请的君主id
		long friendUserId = friendUser.getUserId();// 接受申请的君主id
		// 自己不能申请自己
		if (myUserId == friendUserId) {
			throw new BaseException("不能申请自己为好友。");
		}
		// 好友上限判断
		List<Friend> friList = friendDao.getFriendListByGroupId(myUserId,
				FriendGroupType.GROUP_ALL);
		int maxNum = commonService.getSysParaIntValue(
				AppConstants.SYS_CASTLE_FRIEND_MAXNUM, 200);
		if (friList != null && friList.size() >= maxNum) {
			throw new BaseException("对不起！您的好友已达上限！无法申请添加好友！");
		}

		FriendApp myApp = friendDao.getFriendAppByUserIdAndFriendId(myUserId,
				friendUserId);
		if (myApp != null) {
			throw new BaseException("您已经向对方发出申请。");
		}

		FriendApp friApp = friendDao.getFriendAppByUserIdAndFriendId(
				friendUserId, myUserId);
		if (friApp != null) {
			throw new BaseException("已收到该玩家添加好友邀请。");
		}

		Friend myFri = friendDao
				.getFriendByFriendUserId(myUserId, friendUserId);
		if (myFri != null) {// 检查B在A好友中的情况
			if (FriendGroupType.GROUP_ALL.equals(myFri.getGroup())) {
				throw new BaseException("该君主已经是您的好友。");
			}
			if (FriendGroupType.GROUP_BLACK.equals(myFri.getGroup())) {
				throw new BaseException("该君主已经在黑名单中。");
				// deleteFriend(myFri, 0, null);
				// 删除黑名单的该touser
				// messageService.sendEventMessage(myUser.getUserId(),
				// EventMessage.TYPE_DEL_BLACKNAME,
				// myFri.getFriendUserName());
			}
		}

		Friend friendFri = friendDao.getFriendByFriendUserId(friendUserId,
				myUserId);
		if (friendFri != null) {// 检查A在B好友中的情况
			if (FriendGroupType.GROUP_BLACK.equals(friendFri.getGroup())) {
				throw new BaseException("您已经在对方的黑名单中。");
			}
		}

		// 申请者发出的申请长度
		List<FriendApp> myAppList = friendDao.getAllFriendAppByUserId(myUserId);
		int AppMaxNum = commonService.getSysParaIntValue(
				AppConstants.SYS_CASTLE_APPFRIEND_MAXNUM, 30);
		if (myAppList != null && myAppList.size() > 0) {
			Iterator<FriendApp> itl = myAppList.iterator();
			while (itl.hasNext()) {
				FriendApp app = itl.next();
				app = this.lockFriendApp(app);// 锁定好友申请记录
				if (UtilDate.nowTimestamp().after(
						UtilDate.moveSecond(app.getAddTime(),
								Friend.APP_TIMEOUT_TIME))) {
					friendDao.deleteFriendApp(app);
					itl.remove();
				}
			}
			if (myAppList.size() >= AppMaxNum) {
				throw new BaseException("可申请好友数量已达上限！");
			}
		}

		// 等待审核的申请长度
		List<FriendApp> friendAppList = friendDao
				.getAllFriendAppByFriendUserId(friendUserId);
		if (friendAppList != null && friendAppList.size() > 0) {
			Iterator<FriendApp> itl = friendAppList.iterator();
			while (itl.hasNext()) {
				FriendApp app = itl.next();
				app = this.lockFriendApp(app);// 锁定好友申请记录
				if (UtilDate.nowTimestamp().after(
						UtilDate.moveSecond(app.getAddTime(),
								Friend.APP_TIMEOUT_TIME))) {
					friendDao.deleteFriendApp(app);
					itl.remove();
				}
			}
			int AccMaxNum = commonService.getSysParaIntValue(
					AppConstants.SYS_CASTLE_ACCFRIEND_MAXNUM, 30);
			if (friendAppList.size() >= AccMaxNum) {
				throw new BaseException("该君主的好友受邀数量已达上限！");
			}
		}

		if (myApp == null && friApp == null) {// A未申请过
			// 添加申请记录
			myApp = new FriendApp();
			myApp.setUserId(myUserId);
			myApp.setUserName(myUser.getUserName());
			myApp.setFriendUserId(friendUserId);
			myApp.setFriendUserName(friendUser.getUserName());
			myApp.setAddTime(UtilDate.nowTimestamp());
			friendDao.insertFriendApp(myApp);

			// 发送消息
			String msg = "【" + myUser.getUserName() + "】申请您为好友。";
			messageService.sendMessage(0, friendUserId,
					ChatChannelManager.CHANNEL_SYSTEM, null, msg);
			// FriendInfoMsg finfo = prepareFriendInfoMsg(myUser.getUserId(),
			// friendUser, true, false);
			FriendInfoMsg finfo = prepareFriendInfoMsg(friendUser.getUserId(),
					myUser, true, false);
			// 好友按钮闪烁
			messageService.sendEventMessage(friendUserId,
					EventMessage.TYPE_FRIEND_APP, new SendFriendMsg(
							EventMessage.TYPE_FRIEND_APP, finfo));
		} else {
			throw new BaseException("申请好友失败");
		}
		this.setTolgFriend(myUser, 1, SnsAct.SNSTYPE_INVITE);
	}

	@Override
	public void doAccFriend(User friendUser, User myUser) {
		if (friendUser == null || friendUser.getUserId() <= 0) {
			throw new BaseException("该君主不存在！");
		}
		long friendUserId = friendUser.getUserId();// 发出申请的君主id
		long myUserId = myUser.getUserId();// 接受申请的君主id
		// 取得申请
		FriendApp friApp = friendDao.getFriendAppByUserIdAndFriendId(
				friendUserId, myUserId);
		if (friApp == null) {
			throw new BaseException("好友申请已经处理或对方取消了好友申请");
		}
		friApp = this.lockFriendApp(friApp);// 锁定好友申请记录
		Friend myFri = friendDao
				.getFriendByFriendUserId(myUserId, friendUserId);
		if (myFri != null
				&& FriendGroupType.GROUP_BLACK.equals(myFri.getGroup())) {
			throw new BaseException("该君主已经在您的黑名单中！");
		}

		Friend friendFri = friendDao.getFriendByFriendUserId(friendUserId,
				myUserId);
		if (friendFri != null
				&& FriendGroupType.GROUP_BLACK.equals(friendFri.getGroup())) {
			throw new BaseException("您已经在对方的黑名单中！");
		}
		// 接收方好友上限判断
		List<Friend> friList = friendDao.getFriendListByGroupId(myUserId,
				FriendGroupType.GROUP_ALL);
		int maxNum = commonService.getSysParaIntValue(
				AppConstants.SYS_CASTLE_FRIEND_MAXNUM, 200);
		if (friList != null && friList.size() >= maxNum) {
			throw new BaseException("对不起！您的好友已达上限！无法添加好友！");
		}
		// 申请方好友上限判断
		List<Friend> fl = friendDao.getFriendListByGroupId(friendUserId,
				FriendGroupType.GROUP_ALL);
		int fMaxNum = commonService.getSysParaIntValue(
				AppConstants.SYS_CASTLE_FRIEND_MAXNUM, 200);
		if (fl != null && fl.size() >= fMaxNum) {
			String msg = "【" + myUser.getUserName()
					+ "】通过您的好友申请失败，原因您的好友数量已达上限。";
			messageService.sendMessage(0, friendUserId,
					ChatChannelManager.CHANNEL_SYSTEM, null, msg);
			throw new BaseException(friendUser.getUserName()
					+ "好友达到数量上限,不能添加其为好友。");
		}
		if (myFri == null) {// 加申请的人到接受申请人的好友名单
			myFri = new Friend();
			myFri.setUserId(myUserId);
			myFri.setFriendUserId(friendUserId);
			myFri.setFriendUserName(friendUser.getUserName());
			myFri.setFriendMainCasId(friendUser.getMainCastleId());
			myFri.setGroup(FriendGroupType.GROUP_ALL);
			myFri.setNote("");
			myFri.setAddTime(UtilDate.nowTimestamp());
			friendDao.insertFriend(myFri);
		}

		if (friendFri == null) {// 加接受申请的人到申请人的好友名单
			friendFri = new Friend();
			friendFri.setUserId(friendUserId);
			friendFri.setFriendUserId(myUserId);
			friendFri.setFriendUserName(myUser.getUserName());
			friendFri.setFriendMainCasId(myUser.getMainCastleId());
			friendFri.setGroup(FriendGroupType.GROUP_ALL);
			friendFri.setNote("");
			friendFri.setAddTime(UtilDate.nowTimestamp());
			friendDao.insertFriend(friendFri);
			FriendInfoMsg info = prepareFriendInfoMsg(friendUserId, myUser,
					true, false);
			SendFriendMsg msg = new SendFriendMsg(EventMessage.TYPE_ADD_FRIEND,
					info);
			// 推消息
			messageService.sendEventMessage(friendUserId,
					EventMessage.TYPE_ADD_FRIEND, msg);

		}
		// 删除申请记录
		friendDao.deleteFriendApp(friApp);
		// 发送消息
		String msg = "【" + myUser.getUserName() + "】通过您的好友申请。";
		messageService.sendMessage(0, friendUserId,
				ChatChannelManager.CHANNEL_SYSTEM, null, msg);

	}

	@Override
	public List<FriendInfoMsg> doAccAllFriend(User myUser, List<Long> ids) {
		List<FriendInfoMsg> friends = new ArrayList<FriendInfoMsg>();
		long myUserId = myUser.getUserId();
		// 接收方好友上限判断
		List<Friend> friList = friendDao.getFriendListByGroupId(myUserId,
				FriendGroupType.GROUP_ALL);
		int maxNum = commonService.getSysParaIntValue(
				AppConstants.SYS_CASTLE_FRIEND_MAXNUM, 200);
		if (friList != null && friList.size() >= maxNum) {
			throw new BaseException("对不起！您的好友已达上限！无法添加好友！");
		}
		Map<Long, User> st = new HashMap<Long, User>();
		for (long l : ids) {
			if (l == myUser.getUserId()) {
				throw new BaseException("不能是自己");
			}
			if (st.containsKey(l)) {
				throw new BaseException("id数据重复");
			}
			User friendUser = userService.getUserById(l);
			if (friendUser == null) {
				throw new BaseException("申请不的用户id错误");
			}
			st.put(l, friendUser);
		}
		Collection<User> fu = st.values();
		for (User friendUser : fu) {
			try {
				doAccFriend(friendUser, myUser);
				FriendInfoMsg is = prepareFriendInfoMsg(myUserId, friendUser,
						true, false);
				friends.add(is);
			} catch (Exception e) {
				log.error("一键同意好友申请:", e);
				break;
			}
		}
		processOfflineMsgStatus(myUserId, friends);
		return friends;
	}

	@Override
	public void doRefuseFriend(User firendUser, User myUser) {
		if (firendUser == null || firendUser.getUserId() <= 0) {
			throw new BaseException("不存在该君主！");
		}
		long friendUserId = firendUser.getUserId();// 发出申请的君主id
		long myUserId = myUser.getUserId();// 接受申请的君主id

		// 取得申请
		FriendApp friApp = friendDao.getFriendAppByUserIdAndFriendId(
				friendUserId, myUserId);
		if (friApp != null) {
			friApp = this.lockFriendApp(friApp);// 锁定好友申请记录
			friendDao.deleteFriendApp(friApp);// 删除这条申请记录
		}

		// 发送消息
		String msg = "【" + myUser.getUserName() + "】拒绝您的好友申请。";
		messageService.sendMessage(0, friendUserId,
				ChatChannelManager.CHANNEL_SYSTEM, null, msg);

	}

	@Override
	public List<Long> doRefuseAllFriends(User myUser, List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			throw new BaseException("数据为 null");
		}
		List<Long> userIds = new ArrayList<Long>();
		long myUserId = myUser.getUserId();
		Map<Long, FriendApp> st = new HashMap<Long, FriendApp>();
		for (long l : ids) {
			if (l == myUser.getUserId()) {
				throw new BaseException("不能是自己");
			}
			if (st.containsKey(l)) {
				throw new BaseException("id数据重复");
			}
			FriendApp app = friendDao.getFriendAppByUserIdAndFriendId(l,
					myUserId);
			if (app == null) {
				throw new BaseException("好友申请数据错误" + l);
			}
			st.put(l, app);
		}
		// 等待我审核的申请
		Collection<FriendApp> friAppList = st.values();
		if (friAppList != null && friAppList.size() > 0) {
			Iterator<FriendApp> itl = friAppList.iterator();
			while (itl.hasNext()) {
				FriendApp app = itl.next();
				User friendUser = userService.getUserById(app.getUserId());
				if (friendUser != null) {
					try {
						doRefuseFriend(friendUser, myUser);
						userIds.add(friendUser.getUserId());
					} catch (Exception e) {
						log.error("一键拒绝好友申请:", e);
						break;
					}
				}

			}
		}
		return userIds;
	}

	@Override
	public void doCancelFriend(User myUser, User friendUser) {
		if (friendUser == null || friendUser.getUserId() <= 0) {
			throw new BaseException("不存在该君主！");
		}

		long myUserId = myUser.getUserId();// 发出申请的君主id
		long friendUserId = friendUser.getUserId();// 接受申请的君主id

		// 取得申请
		FriendApp friApp = friendDao.getFriendAppByUserIdAndFriendId(myUserId,
				friendUserId);
		if (friApp != null) {
			friApp = this.lockFriendApp(friApp);// 锁定好友申请记录
			friendDao.deleteFriendApp(friApp);// 删除这条申请记录
		}

		// 发送消息
		String msg = "【" + myUser.getUserName() + "】取消对您的好友申请。";
		messageService.sendMessage(0, friendUserId,
				ChatChannelManager.CHANNEL_SYSTEM, null, msg);
	}

	@Override
	public void doAddGroup(Friend friend, int groupId) {
		if (friend == null)
			throw new BaseException("好友不存在。");
		friend = this.lockFriend(friend);// 锁定好友

		if (friend.getGroupId() == groupId)
			throw new BaseException("好友已经在组中。");

		friend.setGroupId(groupId);// 设置新的组别
		friendDao.updateFriend(friend);
	}

	@Override
	public void doModifyNote(Friend friend, String note) {
		if (note == null || note.trim().equals("")) {
			throw new BaseException("请输入备注");
		}
		if (friend == null) {
			throw new BaseException("好友不存在。");
		}

		friend = this.lockFriend(friend);// 锁定好友

		if (note.trim().length() > 100) {
			throw new BaseException("备注不能超过100个英文字符或是50个汉字");
		}

		if (!note.matches("[a-zA-Z0-9_\u4e00-\u9fa5]{1,16}")) {
			throw new BaseException("备注包含非法字符或空格，请重新输入");
		}
		if (sensitiveWordService.match(note)) {
			throw new BaseException("备注包含非法词语,请重新输入");
		}

		if (friend.getNote().equals(note))
			return;

		friend.setNote(note);
		friendDao.updateFriend(friend);
	}

	@Override
	public void doAddBlack(User user, User blackUser) {
		if (blackUser == null) {
			throw new BaseException("您要添加的君主不存在！");
		}
		if (user.getUserId() == blackUser.getUserId()) {
			throw new BaseException("不能加自己到黑名单。");
		}

		FriendApp friendApp = friendDao.getFriendAppByUserIdAndFriendId(
				user.getUserId(), blackUser.getUserId());
		if (friendApp != null) {// 添加黑名单时，如果该用户在你发出的申请中，则先删除此申请然后再加为好友
			friendDao.deleteFriendApp(friendApp);
		}

		List<Friend> blaList = friendDao.getFriendListByGroupId(
				user.getUserId(), FriendGroupType.GROUP_BLACK);
		int maxNum = commonService.getSysParaIntValue(
				AppConstants.SYS_CASTLE_BLACK_MAXNUM, 50);
		if (blaList != null && blaList.size() >= maxNum) {
			throw new BaseException("黑名单已达上限，请先清理原有黑名单后再行添加！");
		}
		// 黑名单的人不在出现在最近联系人列表里面
		Friend friend = this.getFriendByFriendUserId(user.getUserId(),
				blackUser.getUserId());
		if (friend == null) {
			Friend newfriend = new Friend();
			newfriend.setUserId(user.getUserId());
			newfriend.setFriendUserId(blackUser.getUserId());
			newfriend.setFriendUserName(blackUser.getUserName());
			newfriend.setFriendMainCasId(blackUser.getMainCastleId());
			newfriend.setGroup(FriendGroupType.GROUP_BLACK);
			newfriend.setAddTime(UtilDate.nowTimestamp());
			newfriend.setNote("");
			friendDao.insertFriend(newfriend);
		} else {
			friend = this.lockFriend(friend);// 锁定好友
			if (FriendGroupType.GROUP_BLACK.equals(friend.getGroup())) {
				throw new BaseException("该君主已经在黑名单中！");
			}
			friend.setGroup(FriendGroupType.GROUP_BLACK);// 设置新的组别
			friendDao.updateFriend(friend);
		}

		Friend targetFriend = this.getFriendByFriendUserId(
				blackUser.getUserId(), user.getUserId());
		if (targetFriend != null
				&& (FriendGroupType.GROUP_ALL.equals(targetFriend.getGroup()))) {
			friendDao.deleteFriend(targetFriend);
			// 推消息
			messageService.sendEventMessage(blackUser.getUserId(),
					EventMessage.TYPE_DEL_FRIEND,
					new SendDelFriend(user.getUserId()));

		}
		// 移出最近联系人
		removeFromList(user.getUserId(), blackUser);
		// 发送消息
		String msg = "您被【" + user.getUserName() + "】拉入了黑名单。";
		messageService.sendMessage(0, blackUser.getUserId(),
				ChatChannelManager.CHANNEL_SYSTEM, null, msg);
	}

	@Override
	public void doDelBlack(long userId, User friendUser) {
		Friend f = friendDao.getFriendByFriendUserId(userId,
				friendUser.getUserId());
		if (f == null) {
			throw new BaseException("不存在此玩家");
		}

		if (!f.getGroup().equals(FriendGroupType.GROUP_BLACK)) {
			throw new BaseException("黑名单不存在此玩家");
		}

		friendDao.deleteFriend(f);
	}

	@Override
	public void doDeleteFriend(Friend friend) {
		if (!friend.getGroup().isFriend()) {
			throw new BaseException("删除的不是好友");
		}
		friendDao.deleteFriend(friend);
		// 删除对方数据
		Friend temp = friendDao.getFriendByFriendUserId(
				friend.getFriendUserId(), friend.getUserId());
		if (temp != null) {
			friendDao.deleteFriend(temp);
			// 推消息
			messageService.sendEventMessage(temp.getUserId(),
					EventMessage.TYPE_DEL_FRIEND,
					new SendDelFriend(temp.getFriendUserId()));
		}
	}

	@Override
	public Friend getFriendByFriendUserId(long userId, long friendUserId) {
		return friendDao.getFriendByFriendUserId(userId, friendUserId);
	}

	@Override
	public List<Friend> getFriendListByGroupId(long userId,
			FriendGroupType group) {
		return friendDao.getFriendListByGroupId(userId, group);
	}

	@Override
	public List<FriendInfoMsg> getUserFriends(long userId, int pageNo,
			int pageSize, FriendGroupType group) {
		List<Friend> friends = friendDao.getFriendListByGroupId(userId, group);
		List<FriendInfoMsg> result = new ArrayList<FriendInfoMsg>();
		if (friends != null && !friends.isEmpty()) {
			for (Friend f : friends) {
				User friend = userService.getUserById(f.getFriendUserId());
				FriendInfoMsg msg = prepareFriendInfoMsg(userId, friend, true,
						false);
				result.add(msg);
			}
		}
		return result;
	}

	@Override
	public boolean isFriend(long userId, long friendUserId) {
		if (userId == friendUserId) {
			return false;
		}
		Friend friend = friendDao.getFriendByFriendUserId(userId, friendUserId);
		if (friend != null && (friend.getGroup().isFriend())) {
			// 是好友
			return true;
		}
		return false;
	}

	@Override
	public boolean isBlack(long userId, long friendUserId) {
		Friend friend = friendDao.getFriendByFriendUserId(userId, friendUserId);
		if (friend != null && friend.getGroup() == FriendGroupType.GROUP_BLACK) {
			// 在黑名单
			return true;
		}
		return false;
	}

	/**
	 * 锁定好友
	 * 
	 * @param friend
	 */
	private Friend lockFriend(Friend friend) {
		try {
			friend = (Friend) MemcachedManager.lockObject(friend,
					friend.getId());
		} catch (TimeoutException e) {
			throw new BaseException(e.toString());
		}
		return friend;
	}

	/**
	 * 锁定好友申请
	 * 
	 * @param friendApp
	 */
	private FriendApp lockFriendApp(FriendApp friendApp) {
		try {
			friendApp = (FriendApp) MemcachedManager.lockObject(friendApp,
					friendApp.getId());
		} catch (TimeoutException e) {
			throw new BaseException(e.toString());
		}
		return friendApp;
	}

	@Override
	public int getPageSum(int recordNum) {
		if (recordNum == 0)
			return 0;

		int pageSum = 1;
		if (recordNum > Friend.PAGE_SIZE) {
			pageSum = recordNum / Friend.PAGE_SIZE + 1;
		}
		return pageSum;
	}

	@Override
	public PagerResult getSendedAppByPage(long userId, int pageNo, int pageSize) {
		// 所有我发出的申请
		List<FriendApp> myAppList = friendDao.getAllFriendAppByUserId(userId);
		if (myAppList != null && myAppList.size() > 0) {
			Iterator<FriendApp> itl = myAppList.iterator();
			while (itl.hasNext()) {
				FriendApp app = itl.next();
				// app = this.lockFriendApp(app);//锁定好友申请记录
				if (UtilDate.nowTimestamp().after(
						UtilDate.moveSecond(app.getAddTime(),
								Friend.APP_TIMEOUT_TIME))) {
					// friendDao.deleteFriendApp(app);
					itl.remove();
				}
			}
		}

		int count = myAppList == null ? 0 : myAppList.size();

		if (count > 1) {
			Collections.sort(myAppList, friendAppComparator);
		}

		PagerResult result = new PagerResult();
		result.setPageNo(pageNo);
		result.setPageSize(pageSize);

		int begin = pageNo * pageSize;
		result.setTotal(count);
		if (begin >= count) {
			begin = 0;
			result.setPageNo(0);
		}

		int end = begin + pageSize;
		if (end >= count) {
			end = count;
		}

		List<FriendApp> pageDatas = new ArrayList<FriendApp>();
		for (int i = begin; i < end; i++) {
			pageDatas.add(myAppList.get(i));
		}

		result.setDatas(pageDatas);

		return result;
	}

	@Override
	public List<FriendInfoMsg> getReceivedAppByPage(long userId) {
		// 等待我审核的申请
		List<FriendApp> friAppList = friendDao
				.getAllFriendAppByFriendUserId(userId);
		if (friAppList != null && friAppList.size() > 0) {
			Iterator<FriendApp> itl = friAppList.iterator();
			while (itl.hasNext()) {
				FriendApp app = itl.next();
				// app = this.lockFriendApp(app);//锁定好友申请记录
				if (UtilDate.nowTimestamp().after(
						UtilDate.moveSecond(app.getAddTime(),
								Friend.APP_TIMEOUT_TIME))) {
					// friendDao.deleteFriendApp(app);
					itl.remove();
				}
			}
		}
		int count = friAppList == null ? 0 : friAppList.size();
		if (count > 1) {
			Collections.sort(friAppList, friendAppComparator);
		}
		List<FriendInfoMsg> result = new ArrayList<FriendInfoMsg>();
		if (count >= 1) {
			for (FriendApp fiApp : friAppList) {
				User friend = userService.getUserById(fiApp.getUserId());
				FriendInfoMsg info = prepareFriendInfoMsg(
						fiApp.getFriendUserId(), friend, true, false);
				result.add(info);
			}
		}
		// processOfflineMsgStatus(userId, result);
		return result;
	}

	@Override
	public void cleanFriApp() {
		log.info("clean friApp job start at {}....... ", new Date());
		int val = commonService.getSysParaIntValue(
				AppConstants.FRIEND_APP_VALID_DAYS, 1);
		friendDao.cleanFriApp(val);
		log.info("clean friApp job end  at {}.......day {} ", new Date(), val);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FriendInfoMsg> getRecentTimeFriendList(long userId) {
		List<FriendInfoMsg> result = new ArrayList<FriendInfoMsg>();
		List<RecentTimeFriend> rt = mainServerClient.sendTask(List.class,
				WOLF_FRIENDRECOMMENDCACHE, "getRecentTimeFriendList", userId);
		if (rt == null || rt.isEmpty()) {
			return result;
		}

		for (RecentTimeFriend r : rt) {
			User friendUser = userService.getUserById(r.getUserId());
			if (friendUser == null) {// 避免合服删除账号产生问题
				removeFromList(userId, friendUser);
				continue;
			}
			FriendInfoMsg msg = prepareFriendInfoMsg(userId, friendUser, true,
					false);
			result.add(msg);
		}
		processOfflineMsgStatus(userId, result);
		return result;
	}

	@Override
	public List<FriendInfoMsg> getSocialListByType(long userId,
			GroupType groupType) {
		List<FriendInfoMsg> friends = null;
		if (GroupType.RECENT_FRIEND.equals(groupType)) {
			// 最近联系人列表
			friends = getRecentTimeFriendList(userId);
		} else if (GroupType.FRIEND.equals(groupType)) {
			// 好友列表
			friends = getUserFriends(userId, 0, 0, FriendGroupType.GROUP_ALL);
		} else if (GroupType.FRIEND_BLACK.equals(groupType)) {
			friends = getUserFriends(userId, 0, 0, FriendGroupType.GROUP_BLACK);
		} else {
			throw new BaseException("错误的分组类型。");
		}
		return friends;
	}

	public static class FriendAppComparator implements Comparator<FriendApp> {
		@Override
		public int compare(FriendApp o1, FriendApp o2) {
			return o2.getId() - o1.getId();
		}
	}

	private Map<Long, Long> getRecommendFilterUserIds(long myUserId,
			long[] userIds) {
		// 要过滤掉的Id
		Map<Long, Long> friendUserIdMaps = new HashMap<Long, Long>();

		friendUserIdMaps.put(myUserId, myUserId);

		List<Friend> friList = friendDao.getFriendListByGroupId(myUserId,
				FriendGroupType.GROUP_ALL);
		// 好友上限判断
		int maxNum = commonService.getSysParaIntValue(
				AppConstants.SYS_CASTLE_FRIEND_MAXNUM, 200);
		if (friList != null) {
			if (friList.size() >= maxNum) {
				throw new BaseException("您的好友列表已满，无法进行推荐好友信息发布！");
			}
			for (Friend f : friList) {
				Long tmp = f.getFriendUserId();
				friendUserIdMaps.put(tmp, tmp);
			}
		}

		List<Friend> blackList = friendDao.getFriendListByGroupId(myUserId,
				FriendGroupType.GROUP_BLACK);
		if (blackList != null) {
			for (Friend f : blackList) {
				Long tmp = f.getFriendUserId();
				friendUserIdMaps.put(tmp, tmp);
			}
		}

		int appNum = 0;
		long now = System.currentTimeMillis();
		List<FriendApp> myAppList = friendDao.getAllFriendAppByUserId(myUserId);
		int AppMaxNum = commonService.getSysParaIntValue(
				AppConstants.SYS_CASTLE_APPFRIEND_MAXNUM, 30);
		if (myAppList != null && myAppList.size() > 0) {
			Iterator<FriendApp> itl = myAppList.iterator();
			while (itl.hasNext()) {
				FriendApp app = itl.next();
				Timestamp dttm = app.getAddTime();
				if (dttm == null || now < dttm.getTime()) {
					Long tmp = app.getFriendUserId();
					appNum++;
					friendUserIdMaps.put(tmp, tmp);
				}
			}

			if (userIds != null && appNum >= AppMaxNum) {
				throw new BaseException("您的添加好友信息已满，无法进行推荐好友信息发布！");
			}

		}
		return friendUserIdMaps;
	}

	private void checkSearchFriendCd(long userId) {
		Long time = cds.get(userId);
		if (time == null) {
			time = 0l;
		}
		int cd = commonService.getSysParaIntValue(
				AppConstants.FRIEND_SEARCH_CD_TIME, 10);
		time = time + cd * 1000;
		long now = System.currentTimeMillis();
		if (time <= now) {
			cds.put(userId, now);
			return;
		}
		throw new BaseException("你查找时间过快");
	}

	private int processFriendStatus(long userId, long friendUserId,
			boolean newMsg) {
		int status = 0;

		boolean online = userService.isOnline(friendUserId);
		if (online) {
			status = FriendStatusType.FRIEND_STATUS_ONLINE
					.processStatus(status);
		}
		Friend friend = getFriendByFriendUserId(userId, friendUserId);
		if (friend != null && friend.getGroup().isFriend()) {
			status = FriendStatusType.FRIEND_STATUS_FRIEND
					.processStatus(status);
		}
		if (newMsg) {
			boolean b = hasOfflineMsg(userId, friendUserId);
			if (b) {
				status = FriendStatusType.FRIEND_STATUS_NEW_MSG
						.processStatus(status);
			}
		}

		// 设置体力状态值
		FriendHp friendHp = getFriendHp(userId);
		if (friendHp != null && DateUtils.isSameDay(friendHp.getLastTime())) {
			if (friendHp.isGift(friendUserId)) {
				status = FriendStatusType.FRIEND_STATUS_HP_GIFT
						.processStatus(status);
			}

			if (friendHp.isGiveMe(friendUserId)) {
				status = FriendStatusType.FRIEND_STATUS_HP_GIVEME
						.processStatus(status);
			}

			if (friendHp.isReceive(friendUserId)) {
				status = FriendStatusType.FRIEND_STATUS_HP_RECEIVE
						.processStatus(status);
			}
		}
		return status;
	}

	@Override
	public List<FriendInfoMsg> searchFriendByName(long userId, String userName) {
		checkSearchFriendCd(userId);

		if (userName == null || userName.length() == 0) {
			throw new BaseException("请输入君主名称。");
		}
		User user = userService.getUserByName(userName);
		if (user == null) {
			throw new BaseException("君主不存在！");
		}
		FriendInfoMsg msg = prepareFriendInfoMsg(userId, user, true, false);
		List<FriendInfoMsg> list = new ArrayList<FriendInfoMsg>();
		list.add(msg);
		return list;
	}

	/**
	 * 判断是否 有某个用户的离线消息
	 * 
	 * @param userId
	 * @param friend
	 * @return
	 */
	private boolean hasOfflineMsg(long userId, long friend) {
		Set<Long> st = new HashSet<Long>();
		st = getOfflineMsgStatus(userId);
		if (st == null || st.isEmpty()) {
			return false;
		}
		return st.contains(friend);
	}

	@SuppressWarnings("unchecked")
	private Set<Long> getOfflineMsgStatus(long userId) {
		Set<Long> rt = mainServerClient.sendTask(Set.class,
				"wolf_offlineMsgCacheService", "getOffLineMsgByUserId", userId);
		return rt;
	}

	/**
	 * 批量处理离线消息状态
	 * 
	 * @param list
	 */

	private void processOfflineMsgStatus(long userId, List<FriendInfoMsg> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		Set<Long> st = getOfflineMsgStatus(userId);
		if (st == null || st.isEmpty()) {
			return;
		}

		for (FriendInfoMsg ls : list) {
			if (!st.contains(ls.getUserId())) {
				continue;
			}
			int s = FriendStatusType.FRIEND_STATUS_NEW_MSG.processStatus(ls
					.getStatus());
			ls.setStatus(s);
		}
	}

	/**
	 * 准备前端消息
	 * 
	 * @param userId
	 * @param friend
	 * @param processStatu
	 *            true表示 设置状态值，否则外面自己设置
	 * @param newMsg
	 *            true表示处理离线消息的状态值
	 * @return
	 */
	private FriendInfoMsg prepareFriendInfoMsg(long userId, User friend,
			boolean processStatu, boolean newMsg) {
		long fuserId = friend.getUserId();
		FriendInfoMsg msg = new FriendInfoMsg(fuserId, friend.getUserName(),
				friend.getUsrLv());
		msg.setSex(friend.getSex());
		msg.setVipLevel(vipService.getVipLvByUserId(fuserId));
		msg.setIcon(friend.getIcon());
		msg.setCountryId(friend.getCountryId());
		// TODO 军衔
		msg.setMilitaryRank(0);
		msg.setGuildName("");

		if (processStatu) {
			msg.setStatus(processFriendStatus(userId, fuserId, newMsg));
		}
		return msg;
	}

	@Override
	public List<FriendInfoMsg> getFriendRecommendCache(long myUserId) {
		int MAX = 6;
		List<FriendInfoMsg> datas = new ArrayList<FriendInfoMsg>(MAX);
		User myUser = userService.getUserById(myUserId);
		// 要过滤掉的Id
		Map<Long, Long> filterIdMaps = getRecommendFilterUserIds(myUserId, null);
		Long[] ids = mainServerClient.sendTask(Long[].class,
				WOLF_FRIENDRECOMMENDCACHE, "getCache", myUser.getUsrLv());
		if (ids == null || ids.length <= 0) {
			return datas;
		}

		int num = 0;
		int random = Util.randInt(ids.length);
		for (int i = random; i >= 0; i--) {
			long userId = ids[i];
			if (filterIdMaps.containsKey(userId)) {
				continue;
			}
			// boolean b = userService.isOnline(userId);
			// if (!b) {
			// continue;
			// }
			User friend = userService.getUserById(userId);

			FriendInfoMsg msg = prepareFriendInfoMsg(userId, friend, true,
					false);
			datas.add(msg);
			num++;
			if (num >= MAX) {
				break;
			}

		}
		if (num < MAX) {
			random++;
			for (int i = random; i < ids.length; i++) {
				long userId = ids[i];
				if (filterIdMaps.containsKey(userId)) {
					continue;
				}
				// boolean b = userService.isOnline(userId);
				// if (!b) {
				// continue;
				// }
				User friend = userService.getUserById(userId);
				FriendInfoMsg msg = prepareFriendInfoMsg(userId, friend, true,
						false);
				datas.add(msg);
				num++;
				if (num >= MAX) {
					break;
				}

			}
		}
		// processOfflineMsgStatus(myUserId, datas);
		return datas;
	}

	@Override
	public void putToFriendRecommendCache(long userId, int oldUsrLv,
			int newUsrLv) {
		if (newUsrLv > minUsrLv || oldUsrLv <= maxUsrLv) {
			AsyncWolfTask task = new AsyncWolfTask();
			task.setServiceName(WOLF_FRIENDRECOMMENDCACHE);
			task.setMethodName("addCache");
			task.setParams(new Object[] { userId, oldUsrLv, newUsrLv });
			mainServerClient.asynSendTask(task);

		}

	}

	@Override
	public void getPlatformFriend(UserSession us, Map<String, Object> params) {
		// if (us.isMobileQQ()) {
		// List<Map<String, Object>> friends = tecentMobileQqService
		// .qqFriends(us.getOpenid(), us.getOpenkey(), 1);
		//
		// } else if (us.isWeixin()) {
		// Map<String, Object> friends = tecentWeiXinService.profile(
		// us.getOpenid(), null, us.getOpenkey());
		//
		// }

	}

	@Override
	public void doReceiveHp(long userId, long friendUserId) {
		Friend friend = friendDao.getFriendByFriendUserId(userId, friendUserId);
		if (friend == null || !friend.getGroup().isFriend()) {
			throw new BaseException("对方不是你好友不能领取体力");
		}
		FriendHp friendHp = doGetFriendHp(userId);
		String fUserId = friendUserId + "";
		int maxVal = commonService.getSysParaIntValue(
				AppConstants.FRIEND_RECEIVE_HP, 30);
		// 已经获得的校验
		String receives = friendHp.getReceiveUserIds();
		if (!StringUtils.isEmpty(receives)) {
			String[] arr = receives.split(FriendHp.SPLIST_);
			if (arr.length >= maxVal) {
				throw new BaseException("当日收取已经达到上限");
			}
			for (String aId : arr) {
				if (fUserId.equals(aId)) {
					throw new BaseException("已经领取，不能重复领取");
				}
			}
		}
		// 对方赠送校验
		String give = friendHp.getGiveMeUserIds();
		if (StringUtils.isEmpty(give)) {
			throw new BaseException("该好友未赠送您体力");
		}
		String[] giveArr = give.split(FriendHp.SPLIST_);
		boolean isGive = false;
		for (String g : giveArr) {
			if (g.equals(fUserId)) {
				isGive = true;
				break;
			}
		}
		if (!isGive) {
			throw new BaseException("该好友未赠送您体力");
		}
		int point = commonService.getSysParaIntValue(
				AppConstants.FRIEND_RECEIVE_HP_POINT, 30);
		// 加体力，不允许达到最大
		userService.doAddHpPoint(userId, point, LogHpAct.FRIEND_SEND);

		if (StringUtils.isEmpty(receives)) {
			friendHp.setReceiveUserIds(fUserId);
		} else {
			friendHp.setReceiveUserIds(receives + "," + fUserId);
		}
		friendHp.setLastTime(new Timestamp(System.currentTimeMillis()));
		friendDao.updateFriendHp(friendHp);
		User aUser = userService.getUserById(userId);
		this.setTolgFriend(aUser, point, SnsAct.SNSTYPE_RECEIVEHEART);
	}

	/**
	 * 一键领取并赠送
	 */

	@Override
	public Map<String, List<Long>> doReceiveHpAll(long userId) {
		FriendHp friendHp = doGetFriendHp(userId);
		// 已经获得的校验
		String receives = friendHp.getReceiveUserIds();
		// 对方赠送校验
		String give = friendHp.getGiveMeUserIds();
		if (StringUtils.isEmpty(give)) {
			throw new BaseException("当前无可收取的体力");
		}
		List<Friend> friendList = friendDao.getFriendListByGroupId(userId,
				FriendGroupType.GROUP_ALL);
		if (null == friendList) {
			throw new BaseException("无好友不能进行收取操作");
		}

		Map<String, List<Long>> map = new HashMap<String, List<Long>>();
		map.put("giveMefriends", new ArrayList<Long>());
		map.put("giftfriends", new ArrayList<Long>());

		int maxVal = commonService.getSysParaIntValue(
				AppConstants.FRIEND_RECEIVE_HP, 30);
		// 已经领取
		Set<String> receiveSet = new HashSet<String>();
		if (!StringUtils.isEmpty(receives)) {
			String[] arr = receives.split(FriendHp.SPLIST_);
			if (arr.length >= maxVal) {
				throw new BaseException("当日收取已经达到上限");
			}
			for (String g : arr) {
				receiveSet.add(g);
			}
		}
		// 好友送的
		Set<String> giveSet = new HashSet<String>();
		String[] giveArr = give.split(FriendHp.SPLIST_);
		for (String g : giveArr) {
			giveSet.add(g);
		}
		// 自己已经送出的
		Set<String> giftSet = new HashSet<String>();
		String giftUserIds = friendHp.getGiftUserIds();
		if (!StringUtils.isEmpty(giftUserIds)) {
			String[] arr = giftUserIds.split(FriendHp.SPLIST_);
			for (String g : arr) {
				giftSet.add(g);
			}
		}
		// 取剩余领取个数还还剩下的今日领取个数最小值
		User user = userService.getUserById(userId);

		int maxHpPoint = commonService.getSysParaIntValue(
				AppConstants.USER_MAX_HP_POINT, 600);

		int temp = maxHpPoint - user.getHpPoint();

		if (temp <= 0) {
			throw new BaseException("体力已满，不能进行一键收取");
		}
		int point = commonService.getSysParaIntValue(
				AppConstants.FRIEND_RECEIVE_HP_POINT, 30);

		// 数据记录
		StringBuilder context = new StringBuilder();
		int sucCmt = 0;
		// 这里按giveSet循环效率会好点，但是会导致好友列表领取状态看起来像随机一样
		int sendNum = 0;

		for (Friend friend : friendList) {
			if (sucCmt >= temp) {
				break;
			}
			String fuId = String.valueOf(friend.getFriendUserId());
			// 如果在赠送的当中，不在领取的当中
			if (giveSet.contains(fuId) && !receiveSet.contains(fuId)) {
				receiveSet.add(fuId);
				context.append(fuId).append(FriendHp.SPLIST_);
				sucCmt += point;
				map.get("giveMefriends").add(Long.valueOf(fuId));// 记录领取成功体力
				// 赠送
				if (!giftSet.contains(fuId)) {
					int succ = this.coreSent(userId, friend.getFriendUserId());
					if (succ == 0) {
						map.get("giftfriends").add(Long.valueOf(fuId));// 记录自己送出的
						sendNum++;
					}
					giftSet.add(fuId);
				}

			}

		}
		if (sendNum > 0) {
			// userDailyActivityService.addUserDailyActivity(userId,
			// DailyActivity.ACT_SEND_HP, (byte) 1);
		}
		String friendUserIds = "";
		if (sucCmt > 0) {
			// 加体力，不允许达到最大
			userService.doAddHpPoint(userId, sucCmt, LogHpAct.FRIEND_SEND);
			friendUserIds = context.substring(0, context.length() - 1);
			if (StringUtils.isEmpty(receives)) {
				friendHp.setReceiveUserIds(friendUserIds);
			} else {
				friendHp.setReceiveUserIds(receives + "," + friendUserIds);
			}
			friendDao.updateFriendHp(friendHp);
			User aUser = userService.getUserById(userId);
			this.setTolgFriend(aUser, sucCmt, SnsAct.SNSTYPE_RECEIVEHEART);
		}

		return map;
	}

	private int coreSent(long userId, long friendUserId) {
		Friend friend = friendDao.getFriendByFriendUserId(userId, friendUserId);
		if (friend == null) {
			return 1;
		}
		// 锁对方，只有操作别的玩家的数据才会产生并发问题
		FriendHp friendHp = doGetFriendHp(userId);
		String gift = friendHp.getGiftUserIds();
		String fUserId = friendUserId + "";
		if (friendHp.isGift(friendUserId)) {
			return 2;
		}
		if (StringUtils.isEmpty(gift)) {
			friendHp.setGiftUserIds(fUserId);
		} else {
			friendHp.setGiftUserIds(gift + FriendHp.SPLIST_ + fUserId);
		}
		Timestamp now = new Timestamp(System.currentTimeMillis());
		friendHp.setLastTime(now);
		friendDao.updateFriendHp(friendHp);
		// 好友数据修改
		FriendHp fHp = doGetFriendHp(friendUserId);
		String giveMe = fHp.getGiveMeUserIds();
		if (StringUtils.isEmpty(giveMe)) {
			fHp.setGiveMeUserIds(userId + "");
		} else {
			fHp.setGiveMeUserIds(giveMe + FriendHp.SPLIST_ + userId);
		}
		fHp.setLastTime(now);
		friendDao.updateFriendHp(fHp);
		return 0;
	}

	@Override
	public FriendHp doGetFriendHp(long userId) {
		lockFriendHp(userId);
		FriendHp friendHp = friendDao.getFriendHp(userId);
		if (friendHp == null) {
			friendHp = new FriendHp();
			friendHp.setUserId(userId);
			friendDao.insertFriendHp(friendHp);
		} else {
			if (friendHp.getLastTime() != null
					&& !DateUtils.isSameDay(friendHp.getLastTime())) {
				friendHp.setGiftUserIds("");
				friendHp.setGiveMeUserIds("");
				friendHp.setReceiveUserIds("");
				friendHp.setLastTime(new Timestamp(System.currentTimeMillis()));
				friendDao.updateFriendHp(friendHp);
			}
		}
		return friendHp;

	}

	@Override
	public FriendHp getFriendHp(long userId) {
		return friendDao.getFriendHp(userId);
	}

	/**
	 * 锁定好友
	 * 
	 * @param friend
	 */
	private void lockFriendHp(long userId) {
		try {
			MemcachedManager.lockObject("FRIEND_HP_" + userId);
		} catch (TimeoutException e) {
			throw new BaseException(e.toString());
		}
	}

	@Override
	public List<Long> doSendFriendAll(long userId, List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			throw new BaseException("数据异常");
		}
		Map<Long, Friend> maps = new HashMap<Long, Friend>();
		for (long l : ids) {
			if (l == userId) {
				throw new BaseException("不能给自己赠送体力");
			}
			Friend f = friendDao.getFriendByFriendUserId(userId, l);
			if (f == null) {
				throw new BaseException("好友不存在");
			}
			if (!f.getGroup().isFriend()) {
				throw new BaseException("不是好友");
			}
			if (maps.containsKey(f.getFriendUserId())) {
				throw new BaseException("数据重复");
			}
			maps.put(f.getFriendUserId(), f);
		}

		if (maps.isEmpty()) {
			throw new BaseException("无好友不能进行收取操作");
		}

		List<Long> succ = new ArrayList<Long>();
		Collection<Friend> friendList = maps.values();
		for (Friend friend : friendList) {
			int status = this.coreSent(userId, friend.getFriendUserId());
			if (status == 0) {
				succ.add(friend.getFriendUserId());
			}
			if (status == 2) {
				throw new BaseException("已经赠送了");
			}
		}
		// if (sendNum > 0) {
		// userDailyActivityService.addUserDailyActivity(userId,
		// DailyActivity.ACT_SEND_HP, (byte) 1);
		// }
		User aUser = userService.getUserById(userId);
		this.setTolgFriend(aUser, succ.size(), SnsAct.SNSTYPE_SENDHEART);
		return succ;
	}

	private void setTolgFriend(User aUser, int sendCount, SnsAct snsAct) {
		AbsLogLineBuffer buf = TlogHeadUtil.getTlogHead(
				LogTypeConst.TLOG_TYPE_SNSFLOW, aUser);
		buf.append(1).append(1).append(snsAct.value).append(0);
		logService.log(buf);
	}

	@Override
	public void removeFromList(long userId, User friendUser) {
		mainServerClient.sendTask(Boolean.class, WOLF_FRIENDRECOMMENDCACHE,
				"removeRecentTimeFriend", userId, friendUser.getUserId());
	}

	@Override
	public int getFirendCount(long userId) {
		List<Friend> friendList = friendDao.getAllFriendByUserId(userId);
		return friendList == null ? 0 : friendList.size();
	}
}