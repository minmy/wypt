package cn.sini.cgb.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


/**
 * email工具类
 *
 * @author lijianxin
 */
public class EmailUtils {
    private static Logger logger = LoggerFactory.getLogger(EmailUtils.class);
    //有必要时可以写进system.properties
    private static final String HOST = "smtp.sohu.com";
    private static final String EMAIL = "woyaopintuan@sohu.com";
    private static final String USERNAME = "woyaopintuan";
    private static final String PASSWORD = "Sini88385577***";

    public static boolean send(String to, String title, String content) {
        return send(to, EMAIL, title, content, HOST, USERNAME, PASSWORD, false);
    }

    /**
     * 邮件发送，带用户名和密码验证，测试通过
     *
     * @param to         发送目的地邮箱
     * @param from       发送来源地邮箱
     * @param title      邮箱主题
     * @param content    邮箱内容
     * @param smtpServer 邮箱服务器
     * @param user       邮箱有户名
     * @param password   邮箱密码
     * @param isHTML     是否是Html
     */
    public static boolean send(String to, String from, String title, String content, String smtpServer, String user, String password, boolean isHTML) {
        if (!isMailAddress(to) || !isMailAddress(from)) {
            logger.info("邮箱格式不正确");
            return false;
        }
        Properties props = new Properties();
        Authenticator auth = new MailAuthenticator(user, password);
        Session sendMailSession;
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.auth", "true");
        sendMailSession = Session.getInstance(props, auth);
        Message newMessage = new MimeMessage(sendMailSession);
        try {
            newMessage.setFrom(new InternetAddress(from));
            newMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(
                    to));
            newMessage.setSubject(title);
            newMessage.setSentDate(new Date());
            if (isHTML) {
                newMessage.setContent(content, "text/html;charset=UTF-8");
            } else {
                newMessage.setText(content);
            }
            Transport.send(newMessage);
            return true;
        } catch (MessagingException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean isMailAddress(String mail) {
        return StringUtils.isEmpty(mail) ? false : mail.matches("^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
    }

}

class MailAuthenticator extends Authenticator {
    private String user;
    private String password;

    MailAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }
}
