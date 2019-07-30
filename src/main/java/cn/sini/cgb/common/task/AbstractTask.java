package cn.sini.cgb.common.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import cn.sini.cgb.common.exception.SystemException;
import cn.sini.cgb.common.util.Environment;

/**
 * 抽象定时器任务类
 * 
 * @author 杨海彬
 */
public abstract class AbstractTask {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTask.class);

	/** 是否启用定时任务 */
	@Value("#{systemProperties['job.enable']}")
	private boolean jobEnable;

	/** 定时器任务中文名称 */
	protected abstract String taskName();

	/** 任务入口 */
	protected abstract void execute() throws Exception;

	/** 输出开始结束日志 */
	protected boolean showStartEndLog() {
		return true;
	}

	/** 有事物入口 */
	@Transactional
	public void hasTransactional() {
		run();
	}

	/** 无事务入口 */
	public void notTransactional() {
		run();
	}

	/** 运行 */
	private void run() {
		if (this.jobEnable && Environment.isStartFinish()) {
			if (showStartEndLog()) {
				LOGGER.info("【{}】开始", taskName());
			}
			Date startTime = new Date();
			try {
				execute();
			} catch (Exception e) {
				throw new SystemException("【" + taskName() + "】运行异常", e);
			} finally {
				if (showStartEndLog()) {
					LOGGER.info("【{}】结束，耗时【{}】毫秒", taskName(), new Date().getTime() - startTime.getTime());
				}
			}
		}
	}
}