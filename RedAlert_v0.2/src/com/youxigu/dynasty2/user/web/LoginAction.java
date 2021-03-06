package com.youxigu.dynasty2.user.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youxigu.dynasty2.chat.ChatInterface;
import com.youxigu.dynasty2.chat.proto.CommonHead;
import com.youxigu.dynasty2.chat.proto.CommonHead.Point;
import com.youxigu.dynasty2.chat.proto.CommonHead.ResponseHead;
import com.youxigu.dynasty2.core.flex.amf.AMF3WolfService.SyncStat;
import com.youxigu.dynasty2.core.flex.amf.BaseAction;
import com.youxigu.dynasty2.core.flex.amf.IProtoReportService;
import com.youxigu.dynasty2.map.service.IMapService;
import com.youxigu.dynasty2.openapi.Constant;
import com.youxigu.dynasty2.user.domain.Account;
import com.youxigu.dynasty2.user.domain.User;
import com.youxigu.dynasty2.user.proto.UserMsg.Request10001Define;
import com.youxigu.dynasty2.user.proto.UserMsg.Request10044Define;
import com.youxigu.dynasty2.user.proto.UserMsg.Request10046Define;
import com.youxigu.dynasty2.user.proto.UserMsg.Response10002Define;
import com.youxigu.dynasty2.user.proto.UserMsg.Response10044Define;
import com.youxigu.dynasty2.user.proto.UserMsg.Response10046Define;
import com.youxigu.dynasty2.user.service.IAccountService;
import com.youxigu.dynasty2.user.service.IUserService;
import com.youxigu.dynasty2.util.BaseException;
import com.youxigu.dynasty2.util.CommonUtil;
import com.youxigu.dynasty2.util.StringUtils;
import com.youxigu.wolf.net.OnlineUserSessionManager;
import com.youxigu.wolf.net.Response;
import com.youxigu.wolf.net.UserSession;

/**
 * 运行在mainServer上
 * 
 * @author Administrator
 * 
 */
public class LoginAction extends BaseAction {
	// //^([0-9A-F]{32})$
	private static Logger log = LoggerFactory.getLogger(LoginAction.class);
	private IAccountService accountService;
	private ChatInterface chatService;
	private IProtoReportService protoReportService;
	private String vProtocalVersion;// 本地的proto我那件版本号

	private String supportClientVersion = "1.0.13";
	private int[] supportClientMinVer = null;

	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public void setChatService(ChatInterface chatService) {
		this.chatService = chatService;
	}

	public void setAccountService(IAccountService accountService) {
		this.accountService = accountService;
	}

	public void setProtoReportService(IProtoReportService protoReportService) {
		this.protoReportService = protoReportService;
	}

	public void setSupportClientVersion(String supportClientVersion) {
		this.supportClientVersion = supportClientVersion;
	}

	public void setvProtocalVersion(String vProtocalVersion) {
		this.vProtocalVersion = vProtocalVersion;
	}

	public void init() {
		log.info("当前支持客户端版本号:{}", supportClientVersion);
		supportClientMinVer = paraseVersion(supportClientVersion);

		// 为校验proto文件版本
		Request10001Define.Builder requestBr = Request10001Define.newBuilder();
		byte[] datas = requestBr.build().toByteArray();
		try {
			Request10001Define request = Request10001Define.parseFrom(datas);
			this.vProtocalVersion = request.getVProtocalVersion();
		} catch (Exception e) {
		}
	}

	private int[] paraseVersion(String version) {
		int[] ver = new int[3];
		if (version != null) {
			String[] tmpVerion = StringUtils.split(version, "\\.");
			if (tmpVerion.length > 3) {
				throw new BaseException("当前支持最多3段的版本号格式1");
			}
			for (int i = 0; i < tmpVerion.length; i++) {
				ver[i] = Integer.parseInt(tmpVerion[i]);
			}
		}
		return ver;
	}

	private void checkVersion(String clientVersion) {
		boolean match = true;
		if (clientVersion != null && clientVersion.length() > 0) {

			int[] clientVer = paraseVersion(clientVersion);
			for (int i = 0; i < 3; i++) {
				if (clientVer[i] > supportClientMinVer[i]) {
					break;
				} else if (clientVer[i] < supportClientMinVer[i]) {
					match = false;
					break;
				}
			}
		} else {
			match = false;
		}
		if (!match) {
			throw new BaseException("您的游戏版本不是最新版本，请下载安装最新版本");
			// throw new BaseException("当前系统支持最小版本为" + supportClientVersion
			// + ",请下载新版本");
		}
	}

	/**
	 * 账号注册/登录(10001) <be> 输入：cmd = 10001/aid=账号<br>
	 * 输出：cmd =10001/sid=sessionId/gip=gameServerIp/gport=gameServerPort/err
	 * 
	 * @param params
	 * @param context
	 * @return
	 */
	public Object login(Object obj, Response context) {
		Request10001Define request = (Request10001Define) obj;

		if (!Constant.isTestEnv()) {
			String clientVersion = request.getMobileClient()
					.getVClientVersion();
			if (CommonUtil.isNotEmpty(clientVersion)) {
				checkVersion(clientVersion);
			}
		}

		String vProtocalVersion = request.getVProtocalVersion();
		if (vProtocalVersion != null && !vProtocalVersion.equals("")
				&& !vProtocalVersion.equals(this.vProtocalVersion)) {
			throw new BaseException("客户端proto版本与服务端不一致");
		}

		Map<String, Object> params1 = new HashMap<String, Object>(2);
		params1.put("userip", context.getRemoteIp());
		params1.put("iosession", context.getSession());

		Map<String, Object> result = accountService.login(request, params1);
		Account account = (Account) result.get("account");
		User user = (User) result.get("user");
		UserSession us = (UserSession) result.get("us");
		// params.remove("iosession");
		if (user != null) {
			long userId = user.getUserId();
			Map<String, Object> platParams = new HashMap<String, Object>();
			us.putPlatformProperties(platParams);

			chatService
					.register(userId, user.getUserName(), user.getIcon(),
							user.getSex(), user.getCountryId(),
							user.getGuildId(), user.getUsrLv(), user.getVip(),
							user.getTitle(), platParams);
		}

		Integer queueSize = (Integer) result.remove("queue");

		Response10002Define.Builder response = Response10002Define.newBuilder();

		// 消息头
		ResponseHead.Builder headBr = ResponseHead.newBuilder();
		headBr.setCmd(10002);
		headBr.setRequestCmd(request.getCmd());
		response.setResponseHead(headBr.build());

		// 同步标示
		CommonHead.SyncStat.Builder syncStatBr = CommonHead.SyncStat
				.newBuilder();
		syncStatBr.setId(request.getSyncstat().getId());
		syncStatBr.setStat(SyncStat.SYNC_STATUS_RESPONSE);
		response.setSyncstat(syncStatBr.build());

		if (queueSize != null) {
			response.setQueue(queueSize);
			response.setMy(queueSize);
		} else {
			response.setSid(us.getSessionId());
			response.setGip(us.getGameServerIp());
			response.setGport(us.getGameServerPort());
			response.setAccId(String.valueOf(account.getAccId()));
			response.setAId(account.getAccName());
			String pf = account.getPf();
			response.setPf(pf);
			response.setAppId(Constant.getAppId(us.getpType()));
		}
		response.setZoneid(us.getAreaId());

		if (Constant.isTestEnv()) {
			if ((account.getQqFlag() & Account.QQ_VIP) == Account.QQ_VIP
					|| (account.getQqFlag() & Account.QQ_SUPER) == Account.QQ_SUPER) {
				response.setPType(Constant.PLATFORM_TYPE_QQ);
			} else {
				response.setPType(Constant.PLATFORM_TYPE);
			}
		} else {
			response.setPType(Constant.PLATFORM_TYPE);
		}
		// protoReportService.login(us, account, user, null);
		Response10002Define rerult = response.build();
		return rerult;
	}

	/**
	 * 用户第一次登录，没有user信息，必须在nodeserver创建user之后，注册用户信息到MainServer
	 * 
	 * @param user
	 */
	public void loginFromNodeServer(User user) {
		IoSession session = OnlineUserSessionManager.getIoSessionByAccId(user
				.getAccId());
		UserSession us = (UserSession) session
				.getAttribute(UserSession.KEY_USERSESSION);
		if (us == null) {
			us = new UserSession();
			session.setAttribute(UserSession.KEY_USERSESSION, us);
		}
		us.setAccId(user.getAccId());
		us.setUserId(user.getUserId());
		us.setGender(user.getSex());
		us.setUserName(user.getUserName());
		us.setMainCasId(user.getMainCastleId());
		// us.setOldPwd(user.parsePassword()[0]);
		OnlineUserSessionManager.getUserIdSidMappings().put(user.getUserId(),
				session);
		// boolean isOpen = !officalService.isInOfficalPeriod();
		//
		// chatService.register(user.getUserId(), user.getUserName(), user
		// .getSex(), user.getCountryId(), user.getGuildId(), 0,
		// (isOpen ? user.getOfficalId() : 0), null);
		Map<String, Object> platParams = new HashMap<String, Object>();
		us.putPlatformProperties(platParams);
		chatService.register(user.getUserId(), user.getUserName(),
				user.getIcon(), user.getSex(), user.getCountryId(),
				user.getGuildId(), user.getUsrLv(), user.getVip(),
				user.getTitle(), platParams);
		// udaService.addUserDailyActivity(user.getUserId(),
		// DailyActivity.ACT_LOGIN, (byte) 1, -1, true);

	}

	/**
	 * 10044自动分配建城点
	 * 
	 * @param obj
	 * @param context
	 * @return
	 */
	public Object autoCityPoint(Object obj, Response context) {
		 Request10044Define req = (Request10044Define)obj;
		Response10044Define.Builder res = Response10044Define.newBuilder();
		UserSession us = super.getUserSession(context);
		int[] ps = userService.doAutoCityPoint(us.getUserId());
		if (ps != null) {
			Point.Builder p = Point.newBuilder();
			p.setPosX(ps[0]);
			p.setPosY(ps[1]);
			res.setPon(p.build());
		}
		res.setResponseHead(super.getResponseHead(req.getCmd()));
		return res.build();
	}

	/**
	 * 10046主动设置建城点
	 * 
	 * @param obj
	 * @param context
	 * @return
	 */
	public Object setCityPoint(Object obj, Response context) {
		Request10046Define req = (Request10046Define) obj;
		UserSession us = super.getUserSession(context);
		boolean b = userService.doSetCityPoint(us.getUserId(),req.getPon().getPosX(), req
				.getPon().getPosY());
		Response10046Define.Builder res = Response10046Define.newBuilder();
		if (b) {
			Point.Builder p = Point.newBuilder();
			p.setPosX(req.getPon().getPosX());
			p.setPosY(req.getPon().getPosY());
			res.setPon(p.build());
		}
		res.setResponseHead(super.getResponseHead(req.getCmd()));
		return res.build();
	}

}
