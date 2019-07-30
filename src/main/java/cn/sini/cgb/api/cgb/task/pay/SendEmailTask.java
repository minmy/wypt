package cn.sini.cgb.api.cgb.task.pay;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.admin.identity.query.RoleQuery;
import cn.sini.cgb.admin.identity.query.UserQuery;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney;
import cn.sini.cgb.api.cgb.query.pay.ApplyWithdrawMoneyQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.EmailUtils;
import cn.sini.cgb.common.util.Environment;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 有提现记录时发邮件，并标志提现记录为已发送邮件
 *
 * @author lijianxin
 */
@Component
public class SendEmailTask extends AbstractTask {
    private static final Logger logger = LoggerFactory.getLogger(SendEmailTask.class);
    private static final String TITLE = "《我想拼团》有新的提现申请";
    private static final String ROLENAME = "核销角色";

    @Override
    protected String taskName() {
        return "发送邮件定时任务";
    }

    /**
     * 不需要开启事务,不希望因为一个人信息发送失败之后整个回滚
     */
    @Override
    protected void execute() {
        try (Session session = Environment.getSessionFactory().openSession()) {
            //查看是否存在待提款但没发邮件的体现申请
            List<ApplyWithdrawMoney> applyWithdrawMonies = new ApplyWithdrawMoneyQuery().handleState(ApplyWithdrawMoney.HandleState.DTX)
                    .sendEmail(false).lockMode(LockMode.PESSIMISTIC_WRITE).session(session).list();
            if (CollectionUtils.isEmpty(applyWithdrawMonies)) {
                return;
            }
            //检查某角色的用户是否有邮件
            List<User> users = new UserQuery().roles(new RoleQuery().name(ROLENAME)).readOnly().session(session).list();
            //
            if (users.stream().noneMatch(u -> EmailUtils.isMailAddress(u.getEmail()))) {
                return;
            }
            applyWithdrawMonies.forEach(apply -> {
                //每条申请只要有一条信息发送成功，就标志已发信息，但尽可能多的通知所有此角色的人
                boolean flag = false;
                for (User u : users) {
                    if (flag) {
                        EmailUtils.send(u.getEmail(), TITLE,
                                context(apply.getWeChatUser().getApplyRealName(), apply.getApplyMoneyAmount()));
                    } else {
                        flag = EmailUtils.send(u.getEmail(), TITLE,
                                context(apply.getWeChatUser().getApplyRealName(), apply.getApplyMoneyAmount()));
                    }
                }
                if (flag) {
                    apply.setSendEmail(true);
                    apply.saveOrUpdate(session);
                    session.flush();
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String context(String applyName, BigDecimal applyMoneyAmount) {
        return applyName + "申请提现" + applyMoneyAmount + "元，请尽快审核！";
    }
}
