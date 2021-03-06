package com.youxigu.dynasty2.mail.service.impl;

import com.ibatis.sqlmap.engine.cache.memcached.MemcachedManager;
import com.manu.core.ServiceLocator;
import com.youxigu.dynasty2.chat.EventMessage;
import com.youxigu.dynasty2.chat.client.IChatClientService;
import com.youxigu.dynasty2.combat.domain.*;
import com.youxigu.dynasty2.common.AppConstants;
import com.youxigu.dynasty2.common.service.ICommonService;
import com.youxigu.dynasty2.common.service.ISensitiveWordService;
import com.youxigu.dynasty2.entity.domain.Item;
import com.youxigu.dynasty2.entity.service.IEntityService;
import com.youxigu.dynasty2.log.AbsLogLineBuffer;
import com.youxigu.dynasty2.log.ILogService;
import com.youxigu.dynasty2.log.LogBeanFactory;
import com.youxigu.dynasty2.log.LogTypeConst;
import com.youxigu.dynasty2.mail.dao.IMailMessageDao;
import com.youxigu.dynasty2.mail.domain.MailMessage;
import com.youxigu.dynasty2.mail.proto.MailMsg;
import com.youxigu.dynasty2.mail.proto.SendMailMsg;
import com.youxigu.dynasty2.mail.service.IMailMessageService;
import com.youxigu.dynasty2.openapi.Constant;
import com.youxigu.dynasty2.treasury.service.ITreasuryService;
import com.youxigu.dynasty2.user.domain.Account;
import com.youxigu.dynasty2.user.domain.User;
import com.youxigu.dynasty2.user.domain.UserCount;
import com.youxigu.dynasty2.user.service.IAccountService;
import com.youxigu.dynasty2.user.service.IUserService;
import com.youxigu.dynasty2.util.BaseException;
import com.youxigu.dynasty2.util.DateUtils;
import com.youxigu.wolf.net.OnlineUserSessionManager;
import com.youxigu.wolf.net.UserSession;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * 没有实现广播邮件发送，只能发送给单个用户或者一组用户
 * 
 * 
 * 
 * TODO:目前的效率不高，需要优化 用缓存可能都不如直接访问数据库
 * 
 * @author Administrator
 * 
 */
public class MailMessageService implements IMailMessageService {
	public static final Logger log = LoggerFactory.getLogger(MailMessageService.class);

	public static final String MSG_LOCK_PREFIX = "MSG_LOCK_";
	public static final String MSG_USERLIST_PREFIX = "MSG_USER_";

    public static final int EXTRACT_APPENDIX_SPECIFIED = 0;//提取指定邮件的附件
    public static final int EXTRACT_APPENDIX_ALL = 1;//提取所有邮件的附件

    private IMailMessageDao mailMessageDao;
    private IUserService userService;
    private IChatClientService messageService;// 聊天服务
    private ISensitiveWordService sensitiveWordService;
    private VelocityEngine velocityEngine;
    private ITreasuryService treasuryService;
    private IEntityService entityService;
    private IAccountService accountService;
    private ILogService logService;
    private ICommonService commonService;

	private String templateName = "template/systemMail.vm";
    private MailMessageComparator compartor = new MailMessageComparator();
    /**
	 * 邮件过期时间,系统每天删除一次
	 */
	private int mailValidDays = 7;

	/**
	 * 带附件的邮件过期日期
	 */
	private int mailAppendixValidDays = 90;

	public void setAccountService(IAccountService accountService) {
		this.accountService = accountService;
	}

	public void setEntityService(IEntityService entityService) {
		this.entityService = entityService;
	}

	public void setTreasuryService(ITreasuryService treasuryService) {
		this.treasuryService = treasuryService;
	}

	public void setSensitiveWordService(ISensitiveWordService sensitiveWordService) {
		this.sensitiveWordService = sensitiveWordService;
	}

	public void setMailValidDays(int mailValidDays) {
		this.mailValidDays = mailValidDays;
	}

	public void setMailAppendixValidDays(int mailAppendixValidDays) {
		this.mailAppendixValidDays = mailAppendixValidDays;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public void setLogService(ILogService logService) {
		this.logService = logService;
	}

	public void setMessageService(IChatClientService messageService) {
		this.messageService = messageService;
	}

	public void setMailMessageDao(IMailMessageDao mailMessageDao) {
		this.mailMessageDao = mailMessageDao;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

    public void setCommonService(ICommonService commonService) {
        this.commonService = commonService;
    }
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public void deleteMessage(long receiveUserId, int messageId) {
		lockMessage(receiveUserId);
		MailMessage message = mailMessageDao.getUserMessage(receiveUserId, messageId);
		if (message != null && message.getReceiveUserId() == receiveUserId) {
			if (message.getStatus0() == 0) {
				mailMessageDao.deleteMessage(message);
			}
		}
	}

	@Override
	public void deleteMessage(MailMessage message) {
        if (message != null) {
            lockMessage(message.getReceiveUserId());
            if (message.getStatus0() == 0) {
                mailMessageDao.deleteMessage(message);
            }
        }
    }

    @Override
	public void deleteMessage(long receiveUserId, int[] messageIds) {
		if (messageIds != null) {
			lockMessage(receiveUserId);
			for (int id : messageIds) {
				MailMessage message = mailMessageDao.getUserMessage(receiveUserId, id);
				if (message != null && message.getReceiveUserId() == receiveUserId) {
					if (message.getStatus0() == 0) {
						mailMessageDao.deleteMessage(message);
					}
				}
			}
		}
	}

	@Override
	public MailMessage getUserMessage(long receiveUserId, int messageId) {
		MailMessage msg = mailMessageDao.getUserMessage(receiveUserId, messageId);
		return msg;
	}

	@Override
	public List<MailMessage> getUserAllMessages(long receiveUserId) {
		return mailMessageDao.getUserMessages(receiveUserId);
	}

	@Override
	public Map<String, Object> getUserMessages(long receiveUserId, byte messageType, byte status, int pageNo,
			int pageSize) {
		int begin = pageNo * pageSize;
		int end = begin + pageSize;

		int unRead_normal = 0;
		int unRead_sys = 0;
		int unRead_pvp = 0;
		int count = 0;

		//		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>(pageSize);
		List<MailMessage> pagedMessages = new ArrayList<MailMessage>();

		List<MailMessage> messages = mailMessageDao.getUserMessages(receiveUserId);

		long validTime = System.currentTimeMillis() - mailValidDays * AppConstants.DATE_MILLS;
		long validAppendixTime = System.currentTimeMillis() - mailAppendixValidDays * 1L * AppConstants.DATE_MILLS;

		// 按ID排序，==按日期排序，大的ID排前面
		if (messages.size() > 1) {
			Collections.sort(messages, compartor);
		}

		for (MailMessage message : messages) {
            log.debug("message -> id:{}; status0:{}; status:{}; sendDttm:{}",
                    new Object[]{message.getMessageId(), message.getStatus0(),
                            message.getStatus(), message.getSendDttm()});

			Timestamp t = message.getSendDttm();
			if (message.getStatus0() == 0) {
				if (t != null && t.getTime() < validTime) {
					continue;
				}
			} else {
				if (t != null && t.getTime() < validAppendixTime) {
					continue;
				}
			}
			byte type = message.getMessageType();
			byte stat = message.getStatus();
			if (type == messageType) {
				if (status < 0 || status == stat) {
					if (count >= begin && count < end) {
						pagedMessages.add(message);
                        log.debug("pagedMessage -> id:{}; status0:{}; status:{}; sendDttm:{}",
                                new Object[]{message.getMessageId(), message.getStatus0(),
                                        message.getStatus(), message.getSendDttm()});
					}
					count++;
				}
			}
			if (stat == MailMessage.STATUS_UNREAD) {
				// 统计各个类型邮件的未读数量
				if (type == MailMessage.TYPE_NORMAL) {
					unRead_normal++;
				} else if (type == MailMessage.TYPE_SYSTEM) {
					unRead_sys++;
				} else if (type == MailMessage.TYPE_SYSTEM_PVP) {
					unRead_pvp++;
				}
			}

		}

		int pages = (count / pageSize + (count % pageSize == 0 ? 0 : 1));

		Map<String, Object> params = new HashMap<String, Object>(8);
		params.put("pagedMessages", pagedMessages);
		params.put("pageNo", pageNo);
		params.put("pageSize", pageSize);
		params.put("pages", pages);
		params.put("count", count);
		params.put("unRead_normal", unRead_normal);
		params.put("unRead_sys", unRead_sys);
		params.put("unRead_pvp", unRead_pvp);

		return params;
	}

	//	}

	// }

	// }

	/**
	 * 创建并广播邮件
	 *
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	private void _sendSimpleMessage(MailMessage message) {

		lockMessage(message.getReceiveUserId());

		message.setStatus(MailMessage.STATUS_UNREAD);
		mailMessageDao.createMessage(message);

		// 发送消息到前台，通知有新邮件
		messageService.sendEventMessage(message.getReceiveUserId(), EventMessage.TYPE_NEWMAIL, new SendMailMsg(
				EventMessage.TYPE_NEWMAIL, message.getMessageType()));

		// 修改接收者新邮件计数
		// addMailMessageCount(message);
		try {
			setTlog(message.getSendUserId(), message.getReceiveUserId(), message);
		} catch (RuntimeException e) {
			log.error("", e);
		}
		try {
            User user = userService.getUserById(message.getReceiveUserId());
            AbsLogLineBuffer buffer = LogBeanFactory.buildMailLog(user, message);
            logService.log(buffer);
		} catch (RuntimeException e) {
			log.error("", e);
		}
	}

	/**
	 *
	 * 0 为邮件，1 为私聊频道，2 为世界频道，3 为国家频道，4 // 为联盟频道，5个性签名，6联盟公告
	 *
	 * @param userId
	 * @param toUserId
	 * @param message
	 */
	private void setTlog(long userId, long toUserId, MailMessage message) {
		if (userId != 0) {
			User fromUser = userService.getUserById(userId);
			User toUser = userService.getUserById(toUserId);
			String openId = null;
			String ip = null;
			// String appid = null;
			String head = "";
			int dType = 0;
			UserSession us = OnlineUserSessionManager.getUserSessionByUserId(fromUser.getUserId());
			if (us != null) {
				openId = us.getOpenid();
				ip = us.getUserip();
				if (us.getdType() == Constant.DEVIDE_TYPE_IOS) {
					dType = 0;
				} else {
					dType = 1;
				}
				if (us.isVistor()) {
					head = "G_";
				}
			}
			// 微信 0 /手Q 1
			int ptype = 0;
			if (Constant.PLATFORM_TYPE == 1) {
				ptype = 1;
			}
			// <entry name="vGameSvrId" type="string" size="25"
			// desc="登录的游戏服务端编号" />
			// <entry name="dtEventTime" type="datetime" desc="游戏事件的时间, 格式
			// YYYY-MM-DD
			// HH:MM:SS" />
			// <entry name="vGameAppID" type="string" size="32" desc="游戏APPID"
			// />
			// <entry name="vOpenID" type="string" size="64" desc="用户OPENID号" />
			// <entry name="iPlatID" type="int" desc="ios 0 /android 1" />
			// <entry name="iAreaID" type="int" desc="微信 0 /手Q 1"/>
			// <entry name="iZoneID" type="int" desc="小区号id"/>
			// <entry name="iPlayerCountry" type="int" desc="发送方阵营编号"/>
			// <entry name="iPelayeLevel" type="int" desc="发送方等级"/>
			// <entry name="PlayeBattlePoint" type="int" desc="发送方战斗力"/>
			// <entry name="vUserIP" type="string" size="15" desc="发送信息玩家ip地址"
			// />
			// <entry name="vReceiverOpenID" type="string" size="64"
			// desc="接收方角色OPENID号" />
			// <entry name="iReceiverPlayerCountry" type="int" desc="接收方阵营编号"/>
			// <entry name="iReceiverPlayeLevel" type="int" desc="接收方等级"/>
			// <entry name="iChatType" type="int" desc="信息类型，0 为邮件，1 为私聊频道，2
			// 为世界频道，3 为国家频道，4
			// 为联盟频道，5个性签名，6联盟公告" />
			// <entry name="vTitleContents" type="string" size="64"
			// desc="邮件标题内容，目前最多能发送单条50字节的信息内容" />
			// <entry name="vChatContents" type="string" size="530"
			// desc="信息内容，目前最多能发送单条512字节的信息内容" />
			AbsLogLineBuffer lb = AbsLogLineBuffer
					.getBuffer("0", AbsLogLineBuffer.TYPE_TLOG, LogTypeConst.TLOG_TYPE_SecTalkFlow)
					.append(Constant.SVR_IP).append(new Date()).appendLegacy(head + Constant.getAppId()).append(openId)
					.append(dType).append(ptype).append(Constant.AREA_ID);
			lb.append(0).append(0).append(0).append(ip);
			String vReceiverOpenID = "";
			int iReceiverPlayerCountry = 0;
			int iReceiverPlayeLevel = 0;
			if (toUser != null) {// 私聊
				Account acc = accountService.getAccount(toUser.getAccId());
				vReceiverOpenID = acc.getAccName();
				iReceiverPlayerCountry = toUser.getCountryId();
			}
			lb.append(vReceiverOpenID).append(iReceiverPlayerCountry).append(iReceiverPlayeLevel);
			lb.append(0).append(message.getTitle()).append(message.getComment());
			logService.log(lb);
		}

	}

	@Override
	public void createSimpleMessage(MailMessage message) {
		checkMessage(message, true);
		_sendSimpleMessage(message);

	}

	@Override
	public void createSimpleMessages(MailMessage message, List<Long> userIds) {
		// 只检查一次
		checkMessage(message, false);
		long userId = message.getSendUserId();
		if (userId != 0) {// TODO:不锁了
			Timestamp nowDttm = new Timestamp(System.currentTimeMillis());
			UserCount uc = userService.getUserCount(userId, UserCount.TYPE_MAIL_DAILYNUM);
			if (uc == null) {
				uc = new UserCount();
				uc.setUserId(userId);
				uc.setNum(1);
				uc.setLastDttm(nowDttm);
				uc.setType(UserCount.TYPE_MAIL_DAILYNUM);
				userService.createUserCount(uc);
			} else {
				Timestamp lastDttm = uc.getLastDttm();
				if (DateUtils.isSameDay(lastDttm, nowDttm)) {
					if (nowDttm.getTime() - lastDttm.getTime() < 10000) {
						throw new BaseException("发送邮件的最小时间间隔为10秒");
					}
					int oldNum = uc.getNum();
					if (oldNum >= 100) {
						throw new BaseException("一天最多发送100封邮件");
					}
					uc.setNum(oldNum + 1);
				} else {
					uc.setNum(1);
				}
				uc.setLastDttm(nowDttm);
				userService.updateUserCount(uc);
			}

		}
		for (Long uid : userIds) {
			message = new MailMessage(message);
			message.setReceiveUserId(uid);
			_sendSimpleMessage(message);
		}
	}

	/**
	 * 邮件格式验证
	 *
	 * @param message
	 *            消息
	 */
	private void checkMessage(MailMessage message, boolean checkReceiver) {
		if (message.getSendUserId() == 0) {// 不是系统邮件
			return;
		}
		if (message.getSendUserId() == message.getReceiveUserId()) {
			throw new BaseException("不能给自己发邮件");
		}

		if (checkReceiver && message.getReceiveUserId() <= 0) {
			throw new BaseException("必须指定邮件接收者");
		}

		if (message.getTitle() == null || message.getTitle().trim().equals("")) {
			throw new BaseException("邮件标题不能为空");
		}

		// if (sensitiveWordService.match(message.getTitle())) {//将标题中的铭感词替换为**
		message.setTitle(sensitiveWordService.replace(message.getTitle(), null, false));
		// }

		if (message.getTitle().length() > 20) {
			throw new BaseException("邮件标题不能超过20个字");
		}

		if (message.getComment() == null || message.getComment().trim().equals("")) {
			throw new BaseException("邮件内容不能为空");
		}

		// if (sensitiveWordService.match(message.getComment())) {//
		// 将内容中的铭感词替换为**
		message.setComment(sensitiveWordService.replace(message.getComment(), null, false));
		// }

		if (message.getComment().length() > 500) {
			throw new BaseException("邮件内容不能超过500个字");
		}
		if (checkReceiver) {
			if (message.getReceiveUserId() <= 0) {// 验证收件人
				throw new BaseException("用户无效");
			}
		}

		if (message.getSendDttm() == null) {
			message.setSendDttm(new Timestamp(System.currentTimeMillis()));
		}
	}

	/**
	 * 用模板装饰消息
	 *
	 * @param message
	 */
	private void renderMessage(MailMessage message, String templateName) {
		if (templateName == null)
			templateName = this.templateName;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("comment", message.getComment());
		try {
			String comment = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateName, params);
			message.setComment(comment);
		} catch (VelocityException e) {
			log.error("Error while processing Vilocity template ", e);
		}
	}

    @Override
    public void createPvpMessage(long toUserId, String title, String comment, Combat combat){
 		MailMessage msg = new MailMessage();
		msg.setSendUserId(0);
		msg.setReceiveUserId(toUserId);
        msg.setSendDttm(new Timestamp(System.currentTimeMillis()));
		msg.setTitle(title);
		msg.setComment(comment);
		msg.setMessageType(MailMessage.TYPE_SYSTEM_PVP);
        msg.setMap(getCombatDetail(toUserId, combat));
		createSystemMessage(msg);
    }

    private byte[] getCombatDetail(long toUserId, Combat combat) {
        MailMsg.CombatMail.Builder combatBuilder = MailMsg.CombatMail.newBuilder();
        combatBuilder.setCombatId(combat.getCombatId());
        combatBuilder.setWin(getWinValue(toUserId, combat));

        MailMsg.CombatMail.MailTeamInfo.Builder atkBuilder = MailMsg.CombatMail.MailTeamInfo.newBuilder();
        CombatTeam atkTeam = combat.getAttacker();
        setupTeamInfo(atkBuilder, atkTeam);

        MailMsg.CombatMail.MailTeamInfo.Builder dfcBuilder = MailMsg.CombatMail.MailTeamInfo.newBuilder();
        CombatTeam dfcTeam = combat.getDefender();
        setupTeamInfo(dfcBuilder, dfcTeam);

        setupGain(toUserId, combat.getCombatRob());

        return combatBuilder.build().toByteArray();
    }

    private void setupGain(long toUserId, CombatRob combatRob) {
        //todo: CombatRob完成后修改此处
    }

    private void setupTeamInfo(MailMsg.CombatMail.MailTeamInfo.Builder teamBuilder, CombatTeam team) {
        teamBuilder.setUserId(team.getUserId());
        teamBuilder.setUserName(team.getTeamName());
        teamBuilder.setCordX(team.getPosX());
        teamBuilder.setCordY(team.getPosY());
        teamBuilder.setTeamPower(team.getTeamPower());

        int totalHarm = 0;
        int totalLostHp = 0;
        int totalRemainHp = 0;
        for(CombatUnit cu : team.getUnits()){
            MailMsg.CombatMail.MailHeroInfo.Builder heroBuilder = MailMsg.CombatMail.MailHeroInfo.newBuilder();
            heroBuilder.setUnitHeroId(cu.getUnitEntId());
            heroBuilder.setHarm(cu.getTotalHarm());
            heroBuilder.setRemainHp(cu.getTotalHp());
            heroBuilder.setLostHp(cu.getTotalLostHp());
            teamBuilder.addHerosInfo(heroBuilder);

            totalHarm += cu.getTotalHarm();
            totalLostHp += cu.getTotalLostHp();
            totalRemainHp += cu.getTotalHp();
        }
        teamBuilder.setHarm(totalHarm);
        teamBuilder.setLostHp(totalLostHp);
        teamBuilder.setRemainHp(totalRemainHp);
    }

//    private CombatTeam getCombatTeam(long toUserId, Combat combat){
//        if(combat.getAttacker().getUserId() == toUserId){
//            return combat.getAttacker();
//        }
//        else if(combat.getDefender().getUserId() == toUserId){
//            return combat.getDefender();
//        }
//        else{
//            log.error("用户没有参与该战斗。用户id：{0}，战斗id：{1}", toUserId, combat.getCombatId());
//            throw new BaseException("用户没有参与该战斗");
//        }
//    }

    private boolean getWinValue(long toUserId, Combat combat) {
        boolean win = false;
        if (combat.getWinType() == CombatConstants.WIN_ATK) {
            if (combat.getAttacker().getUserId() == toUserId) {
                win = true;
            } else if (combat.getDefender().getUserId() == toUserId) {
                win = false;
            } else {
                throw new BaseException("战报接收者userId错误");
            }
        }
        if (combat.getWinType() == CombatConstants.WIN_DEF) {
            if (combat.getAttacker().getUserId() == toUserId) {
                win = false;
            } else if (combat.getDefender().getUserId() == toUserId) {
                win = true;
            } else {
                throw new BaseException("战报接收者userId错误");
            }
        }
        return win;
    }

    @Override
	public void createSystemMessage(MailMessage message) {
		// checkMessage(message);
		// renderMessage(message);
		_sendSimpleMessage(message);
	}

	@Override
	public void createSystemMessage(long toUserId, String title, String context, byte type) {
		MailMessage msg = new MailMessage();
		// msg.setSendUserId(0);
		msg.setReceiveUserId(toUserId);
		msg.setTitle(title);
		msg.setComment(context);
		// msg.setMessageType(MailMessage.TYPE_SYSTEM);
		msg.setMessageType(type);
		msg.setSendDttm(new Timestamp(System.currentTimeMillis()));
		createSystemMessage(msg);
	}

	// }

	@Override
	public void createSystemMessageWithTemplate(long toUserId, String title, String context, String templateName,
			byte type) {
		MailMessage msg = new MailMessage();
		// msg.setSendUserId(0);
		msg.setReceiveUserId(toUserId);
		msg.setTitle(title);
		msg.setComment(context);
		msg.setMessageType(type);
		msg.setSendDttm(new Timestamp(System.currentTimeMillis()));
		createSystemMessageWithTemplate(msg, templateName);

	}

	@Override
	public void createSystemMessageWithTemplate(MailMessage message, List<Long> userIds, String templateName) {
		for (Long uid : userIds) {
			message.setReceiveUserId(uid);
			renderMessage(message, templateName);
			_sendSimpleMessage(message);
		}

	}

	@Override
	public void createSystemMessageWithTemplate(MailMessage message, String templateName) {
		renderMessage(message, templateName);
		_sendSimpleMessage(message);

	}

	@Override
	public void createSystemMessageWithTemplate(MailMessage message, Map<String, Object> context, String templateName) {
		if (templateName == null)
			templateName = this.templateName;
		try {
			String comment = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateName, context);
			message.setComment(comment);
			createSystemMessage(message);
		} catch (VelocityException e) {
			log.error("Error while processing Vilocity template ", e);
		}
	}

	@Override
	public void createSystemMessageWithTemplate(long toUserId, String title, Map<String, Object> context,
			String templateName, byte type) {
		if (templateName == null)
			templateName = this.templateName;
		try {
			String comment = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateName, context);
			createSystemMessage(toUserId, title, comment, type);
		} catch (VelocityException e) {
			log.error("Error while processing Vilocity template ", e);
		}
	}

	public String getContextByTemplate(Map<String, Object> context, String templateName) {
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateName, context);
	}

	@Override
	public void updateMessage2Read(long receiveUserId, int messageId) {
		MailMessage message = mailMessageDao.getUserMessage(receiveUserId, messageId);
		if (message != null && message.getReceiveUserId() == receiveUserId) {
			// lockMessage(receiveUserId);
			if (message.getStatus() == MailMessage.STATUS_UNREAD) {
				//
				// deleteMailMessageCount(message);
				message.setStatus(MailMessage.STATUS_READ);
				mailMessageDao.updateMessage2Read(message);
			}
		}
	}

	@Override
	public MailMessage updateMessage2fetchAppendix(long receiveUserId, int messageId, int pos, Map<Integer, Integer> appendices) {
		lockMessage(receiveUserId);
		MailMessage message = mailMessageDao.getUserMessage(receiveUserId, messageId);
		if (message == null || message.getReceiveUserId() != receiveUserId) {
			throw new BaseException("邮件不存在");
		}

		if (pos < 0 || pos > 5) {
			throw new BaseException("邮件槽不存在");
		}

		if (message.getEntityIdByPos(pos) == 0) {
			throw new BaseException("邮件中没有附件不能提取");
		}

		if (message.getStatus0ByPos(pos) == MailMessage.APPENDIX_NONE) {
			throw new BaseException("附件已经提取");
		}
		int entId = message.getEntityIdByPos(pos);

		int itemNum = message.getItemNumByPos(pos);
		if (itemNum <= 0) {
			throw new BaseException("邮件中没有附件不能提取");
		}
		int realNum = itemNum;
		Item item = (Item) entityService.getEntity(entId);
		if (item.getNotJoinPack() <= 0) {
			treasuryService.lockTreasury(receiveUserId);
			// 背包剩余空间
			int treasuryFreeNum = treasuryService.getBagFreeCount(receiveUserId);

			if (item.getSumAble() == 0) {
				// 不可堆叠的,
				if (treasuryFreeNum < realNum) {
					realNum = treasuryFreeNum;
				}
			} else {
				// 可堆叠的,这里简单处理，没有考虑原有道具的堆叠数
				if (treasuryFreeNum > 0) {
					treasuryFreeNum = treasuryFreeNum * item.getStackNum();
					if (treasuryFreeNum < realNum) {
						realNum = treasuryFreeNum;
					}
				}
			}
			if (realNum <= 0) {
				throw new BaseException("背包已满");
			}
		}

		// 邮件收到的都是礼品的，绑定的按道具本身的绑定属性
		treasuryService.addItemToTreasury(receiveUserId, entId, realNum, message.getIsGift(), -1, true, false,
				com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_57);
		if (realNum >= itemNum) {
			if (pos == 0) {
				message.setEntityId0(message.getEntityId0() * -1);
			} else if (pos == 1) {
				message.setEntityId1(message.getEntityId1() * -1);
			} else if (pos == 2) {
				message.setEntityId2(message.getEntityId2() * -1);
			} else if (pos == 3) {
				message.setEntityId3(message.getEntityId3() * -1);
			} else if (pos == 4) {
				message.setEntityId4(message.getEntityId4() * -1);
			} else if (pos == 5) {
				message.setEntityId5(message.getEntityId5() * -1);
			}

			message.setStatus0ByPos(pos, MailMessage.APPENDIX_NONE);
		} else {
			message.setItem(pos, entId, itemNum - realNum);
		}
		mailMessageDao.updateMessage2fetchAppendix(message);

        addItemToMap(appendices, entId, realNum);

		return message;
	}

	@Override
	public MailMessage updateMessage2fetchAppendix(long receiveUserId, int messageId, Map<Integer, Integer> appendices) {
        lockMessage(receiveUserId);
        MailMessage message = mailMessageDao.getUserMessage(receiveUserId, messageId);
        if (message == null || message.getReceiveUserId() != receiveUserId) {
            throw new BaseException("邮件不存在");
        }
        return updateMessage2fetchAppendix(receiveUserId, message, appendices, true);
    }

    @Override
    public MailMessage updateMessage2fetchAppendix(long receiveUserId, MailMessage message,
                                                   Map<Integer, Integer> appendices, boolean throwException) {
        lockMessage(receiveUserId);
        boolean hasAppendix = false;

        Map<Integer, Integer> tmpItems = new HashMap<Integer, Integer>();
        byte isGift = message.getIsGift();
        if (message.getStatus0ByPos(0) == MailMessage.APPENDIX_HAVE) {
            int itemId = Math.abs(message.getEntityId0());
            int itemNum = message.getItemNum0();

            if (itemId != 0 && itemNum != 0) {
                treasuryService.addItemToTreasury(receiveUserId, itemId, itemNum, isGift, -1, true, false,
                        com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_57);
                hasAppendix = true;
                addItemToMap(tmpItems, itemId, itemNum);
            }
            message.setStatus0ByPos(0, MailMessage.APPENDIX_NONE);
            message.setEntityId0(-itemId);

        }

        if (message.getStatus0ByPos(1) == MailMessage.APPENDIX_HAVE) {
            int itemId = message.getEntityId1();
            int itemNum = message.getItemNum1();

            if (itemId != 0 && itemNum != 0) {
                treasuryService.addItemToTreasury(receiveUserId, itemId, itemNum, isGift, -1, true, false,
                        com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_57);
                hasAppendix = true;
                addItemToMap(tmpItems, itemId, itemNum);
            }
            message.setStatus0ByPos(1, MailMessage.APPENDIX_NONE);
            message.setEntityId1(-itemId);
        }

        if (message.getStatus0ByPos(2) == MailMessage.APPENDIX_HAVE) {
            int itemId = message.getEntityId2();
            int itemNum = message.getItemNum2();

            if (itemId != 0 && itemNum != 0) {
                treasuryService.addItemToTreasury(receiveUserId, itemId, itemNum, isGift, -1, true, false,
                        com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_57);
                hasAppendix = true;
                addItemToMap(tmpItems, itemId, itemNum);
            }
            message.setStatus0ByPos(2, MailMessage.APPENDIX_NONE);
            message.setEntityId2(-itemId);
        }

        if (message.getStatus0ByPos(3) == MailMessage.APPENDIX_HAVE) {
            int itemId = message.getEntityId3();
            int itemNum = message.getItemNum3();

            if (itemId != 0 && itemNum != 0) {
                treasuryService.addItemToTreasury(receiveUserId, itemId, itemNum, isGift, -1, true, false,
                        com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_57);
                hasAppendix = true;
                addItemToMap(tmpItems, itemId, itemNum);
            }
            message.setStatus0ByPos(3, MailMessage.APPENDIX_NONE);
            message.setEntityId3(-itemId);
        }

        if (message.getStatus0ByPos(4) == MailMessage.APPENDIX_HAVE) {
            int itemId = message.getEntityId4();
            int itemNum = message.getItemNum4();

            if (itemId != 0 && itemNum != 0) {
                treasuryService.addItemToTreasury(receiveUserId, itemId, itemNum, isGift, -1, true, false,
                        com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_57);
                hasAppendix = true;
                addItemToMap(tmpItems, itemId, itemNum);
            }
            message.setStatus0ByPos(4, MailMessage.APPENDIX_NONE);
            message.setEntityId4(-itemId);
        }

        if (message.getStatus0ByPos(5) == MailMessage.APPENDIX_HAVE) {
            int itemId = message.getEntityId5();
            int itemNum = message.getItemNum5();

            if (itemId != 0 && itemNum != 0) {
                treasuryService.addItemToTreasury(receiveUserId, itemId, itemNum, isGift, -1, true, false,
                        com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_57);
                hasAppendix = true;
                addItemToMap(tmpItems, itemId, itemNum);
            }
            message.setStatus0ByPos(5, MailMessage.APPENDIX_NONE);
            message.setEntityId5(-itemId);
        }
        if (!hasAppendix) {
            if (throwException) {
                throw new BaseException("没有可领取的附件");
            } else {
                log.info("用户{}的邮件{}没有可提取的附件", message.getReceiveUserId(), message.getMessageId());
                return message;
            }
        }
        message.setStatus(MailMessage.STATUS_READ);
        mailMessageDao.updateMessage2fetchAppendix(message);

        //只有在全部格子提取成功后，才将结果放入返回的map里，避免背包已满事物回滚后后记录的道具与实际提取的道具不一致
        for (Map.Entry<Integer, Integer> entry : tmpItems.entrySet()) {
            addItemToMap(appendices, entry.getKey(), entry.getValue());
        }
        return message;
    }

    @Override
    public void extractAppendix(long receiveUserId, int type, List<Integer> msgIds, Map<Integer, Integer> appendices){
        if (type == EXTRACT_APPENDIX_SPECIFIED) {
            if (msgIds == null || msgIds.size() == 0) {
                throw new BaseException("未指定要提取附件的邮件。");
            }
        }
        List<MailMessage> messages = mailMessageDao.getUserMessages(receiveUserId);
        if (messages == null || messages.size() == 0) {
            throw new BaseException("没有附件可提取。");
        }

        //即使某个邮件的附件提取出错，也要把已经提取到的附件返回给用户
        MailMessageService mailService = (MailMessageService) ServiceLocator.getSpringBean("mailMessageService");
        for (MailMessage message : messages) {
            //提取指定邮件。如果指定的邮件已提取，不提示用户，直接跳过
            if (type == EXTRACT_APPENDIX_SPECIFIED && msgIds.contains(message.getMessageId())) {
                mailService.updateMessage2fetchAppendix(receiveUserId, message, appendices, false);
            }
            //提取所有未提取附件的邮件。
            if (type == EXTRACT_APPENDIX_ALL) {
                if (message.getStatus0() != MailMessage.APPENDIX_NONE) {
                    mailService.updateMessage2fetchAppendix(receiveUserId, message, appendices, false);
                }
            }
        }
    }

	@Override
	public void cleanMessage() {
		log.info("clean mail job start at {}....... ", new Date());
		mailMessageDao.deleteMessage(mailValidDays, mailAppendixValidDays);
		// List<List<MailCount>> result =
		// mailMessageDao.deleteMessage(mailValidDays);
		// if ( result != null ) {
		// for(List<MailCount> mcList:result){
		// if (mcList!=null)
		// for(MailCount mc:mcList){
		//
		// int[] mailCount = getMailMessageCount(mc.getReceiveUserId());
		// if( mc.getMessageType() == MailMessage.TYPE_NORMAL ) {
		// int k = mailCount[0]-mc.getN();
		// mailCount[0]=(k>0)?k:0;
		// }
		// if( mc.getMessageType() == MailMessage.TYPE_SYSTEM ) {
		// int k = mailCount[1]-mc.getN();
		// mailCount[1]=(k>0)?k:0;
		// }
		// if( mc.getMessageType() == MailMessage.TYPE_SYSTEM_PVP ) {
		// int k = mailCount[2]-mc.getN();
		// mailCount[2]=(k>0)?k:0;
		// }
		// setMailMessageCount(mc.getReceiveUserId(),mailCount);
		// }
		// }
		// }
		log.info("clean mail job end  at {}....... ", new Date());
		/**
		 * SqlMapClientImpl client = (SqlMapClientImpl) this.getSqlMapClient();
		 * client.delegate.getShardingConfig("")
		 */
		// TODO Auto-generated method stub
	}

	private void lockMessage(long userId) {
		try {
			MemcachedManager.lock(MSG_LOCK_PREFIX + userId);
		} catch (TimeoutException e) {
			throw new BaseException(e);
		}
	}

	private void lockMessage(long userId, int msgId) {
		try {
			MemcachedManager.lock(MSG_LOCK_PREFIX + userId + msgId);
		} catch (TimeoutException e) {
			throw new BaseException(e);
		}
	}

    static class MailMessageComparator implements Comparator<MailMessage> {
        @Override
        public int compare(MailMessage o1, MailMessage o2) {
            //Collection.sort排序时，compare返回值越小，排序越靠前
            //先按有无附件排序，有附件在前；再按发送时间排序，发送晚的在前
            //如时间也相同，最后按messageId排序，确保每次请求时顺序一致
            //status0>0表示有附件，等于0表示无附件或已提取
            if ((o1.getStatus0() > 0) && o2.getStatus0() <= 0) {
                return -1000;
            } else if ((o1.getStatus0() <= 0) && o2.getStatus0() > 0) {
                return 1;
            } else {
                if ((o1.getSendDttm().getTime() - o2.getSendDttm().getTime()) > 0) {
                    return -100;
                } else if ((o1.getSendDttm().getTime() - o2.getSendDttm().getTime()) < 0) {
                    return 10;
                } else {
                    if (o1.getMessageId() > o2.getMessageId()) {
                        return -10;
                    } else if (o1.getMessageId() < o2.getMessageId()) {
                        return 1000;
                    }
                    return 0;
                }
            }
        }
    }

	@Override
	public List<MailMessage> listMessages(long receiveUserId) {
		return mailMessageDao.getUserMessages(receiveUserId);
	}

    private MailCount statisticsUnreadMailCount(List<MailMessage> messages){
        int unRead_normal = 0;
		int unRead_sys = 0;
		int unRead_pvp = 0;

		for (MailMessage message : messages) {
			byte type = message.getMessageType();
			byte stat = message.getStatus();

			if (stat == MailMessage.STATUS_UNREAD) {
				// 统计各个类型邮件的未读数量
				if (type == MailMessage.TYPE_NORMAL) {
					unRead_normal++;
				} else if (type == MailMessage.TYPE_SYSTEM) {
					unRead_sys++;
				} else if (type == MailMessage.TYPE_SYSTEM_PVP) {
					unRead_pvp++;
				}
			}

		}
        MailCount result = new MailCount();
        result.setNormal(unRead_normal);
        result.setSystem(unRead_sys);
        result.setSystemPvp(unRead_pvp);
        return result;
    }

    @Override
    public void createMailInternal(long sendUserId, long receiveUserId, String sendTime,
                                   String readTime, String title, String comment,
                                   byte status, byte mailType, String appendix){
//        for(int i=0; i<1000; i++) {
        MailMessage message = new MailMessage();
        message.setSendUserId(sendUserId);
        message.setReceiveUserId(receiveUserId);
        message.setSendDttm(parseDateTime(sendTime));
        message.setReadDttm(parseDateTime(readTime));
        message.setComment(comment);
        message.setTitle(title);
        message.setStatus(status);
        message.setMessageType(mailType);

        if (appendix != null && appendix.trim().length() > 0) {
            List<Integer> items = parseAppendix(appendix);
            for (int j = 0; j < items.size() / 2; j++) {
                if (items.get(2 * j) != 0) {
                    message.setEntityIdByPos(j, items.get(2 * j), items.get(2 * j + 1));
                }
            }
        }

        mailMessageDao.createMessage(message);
//        }
    }

    class MailCount{
        private int normal;
        private int system;
        private int systemPvp;

        public int getNormal() {
            return normal;
        }

        public void setNormal(int normal) {
            this.normal = normal;
        }

        public int getSystem() {
            return system;
        }

        public void setSystem(int system) {
            this.system = system;
        }

        public int getSystemPvp() {
            return systemPvp;
        }

        public void setSystemPvp(int systemPvp) {
            this.systemPvp = systemPvp;
        }
    }

    private Timestamp parseDateTime(String timeString){
        if(timeString.trim().length()==0){
            return null;
        }
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeString);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            throw new BaseException("错误的时间格式。正确格式：yyyy-MM-dd HH:mm:ss");
        }
    }

    private List<Integer> parseAppendix(String appendix){
        ArrayList<Integer> result = new ArrayList<Integer>();
        String[] items = appendix.split(",");
        for (String item : items){
            String[] itemRow = item.split(":");
            if(itemRow.length != 2){
                throw new BaseException("附件格式错误");
            }
            try {
                result.add(Integer.parseInt(itemRow[0]));
                result.add(Integer.parseInt(itemRow[1]));
            }
            catch (NumberFormatException e){
                throw new BaseException("附件格式错误，非有效数字");
            }
        }
        if(result.size()<12){
            for(int i = result.size(); i<12; i++){
                result.add(0);
            }
        }
        return result;
    }

    private Map<Integer, Integer> addItemToMap(Map<Integer, Integer> items, int itemId, int itemNum){
        if(items.containsKey(itemId)){
            int sum = items.get(itemId) + itemNum;
            items.put(itemId, sum);
        }
        else{
            items.put(itemId, itemNum);
        }
        return items;
    }
        // private IUserAttrService userAttrService;
    //	private void addItemDefines(int itemId,
    //			List<Map<String, Object>> itemDefines) {
    //		if (itemId >= AppConstants.ENT_DYNAMIC_ID_MIN
    //				&& itemId <= AppConstants.ENT_DYNAMIC_ID_MAX) {// 运营配置礼包
    //
    //			for (Map<String, Object> one : itemDefines) {
    //				if ((Integer) one.get("entId") == itemId) {
    //					return;
    //				}
    //			}
    //
    //			Item item = (Item) entityService.getEntity(itemId);
    //			if (item != null) {
    //				itemDefines.add(item.toMap());
    //			}
    //
    //		}
    // private int getCount(List<MailMessage> mailList) {
    // int result = 0;
    // if (mailList != null)
    // for (MailMessage mm : mailList) {
    // if (mm.getStatus() == MailMessage.STATUS_UNREAD)
    // result += 1;
    // }
    // return result;
    // protected int[] splitListInt( String src, String gap ){
    //
    // int[] result = new int[]{0,0,0};
    // if (src==null) return result;
    // String[] temp = Util.split( src, gap );
    // int i=0;
    // if(temp!=null) {
    // for (String s : temp) {
    //
    // try {
    // result[i] = Integer.valueOf(s);
    // } catch ( Exception e ){
    // log.error( e.getMessage() );
    // }
    // i++;
    // if ( i > 2 ) break;
    // }
    // }
    //
    // return result;
    // }
    // private int[] getMailMessageCount(long userId) {
    // String countStr =
    // userAttrService.getStringUserAttr(userId,AppConstants.SYS_MAIL_COUNT_NEW);
    // return splitListInt(countStr,",");
    // }
    // private void setMailMessageCount(long userId,int[] mailCount) {
    // String result = "0,0,0";
    // if ( mailCount!=null && mailCount.length>0 ) {
    // result=mailCount[0]+"";
    // for ( int i=1;i<mailCount.length;i++ ) {
    // result += "," + mailCount[i];
    // }
    // }
    // userAttrService.saveUserAttr(userId, AppConstants.SYS_MAIL_COUNT_NEW,
    // result);
    // }
    // private void deleteMailMessageCount( MailMessage message ) {
    // if ( message == null || message.getStatus() != MailMessage.STATUS_UNREAD
    // ) return;
    // int[] mailCount = getMailMessageCount(message.getReceiveUserId());
    // if( message.getMessageType() == MailMessage.TYPE_NORMAL ) {
    // int k = mailCount[0]-1;
    // mailCount[0]=(k>0)?k:0;
    // }
    // if( message.getMessageType() == MailMessage.TYPE_SYSTEM ) {
    // int k = mailCount[1]-1;
    // mailCount[1]=(k>0)?k:0;
    // }
    // if( message.getMessageType() == MailMessage.TYPE_SYSTEM_PVP ) {
    // int k = mailCount[2]-1;
    // mailCount[2]=(k>0)?k:0;
    // }
    // setMailMessageCount(message.getReceiveUserId(),mailCount);
    // }
    // private void addMailMessageCount( MailMessage message ) {
    //
    // if ( message == null ) return;
    // int[] mailCount = getMailMessageCount(message.getReceiveUserId());
    // if( message.getMessageType() == MailMessage.TYPE_NORMAL ) {
    // int k = mailCount[0]+1;
    // mailCount[0]=(k>0)?k:0;
    // }
    // if( message.getMessageType() == MailMessage.TYPE_SYSTEM ) {
    // int k = mailCount[1]+1;
    // mailCount[1]=(k>0)?k:0;
    // }
    // if( message.getMessageType() == MailMessage.TYPE_SYSTEM_PVP ) {
    // int k = mailCount[2]+1;
    // mailCount[2]=(k>0)?k:0;
    // }
    // setMailMessageCount(message.getReceiveUserId(),mailCount);
    // @Override
    // public void createSystemMessage(MailMessage message, List<Long> userIds)
    // {
    // // checkMessage(message);
    // for (Long uid : userIds) {
    // message.setMessageId(0);
    // message.setReceiveUserId(uid);
    // // renderMessage(message);
    // _sendSimpleMessage(message);
    // }
}
