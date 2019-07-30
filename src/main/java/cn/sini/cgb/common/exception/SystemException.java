package cn.sini.cgb.common.exception;

/**
 * 系统异常
 * 
 * @author 杨海彬
 */
public class SystemException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SystemException(String msg) {
		super(msg);
	}

	public SystemException(String msg, Throwable t) {
		super(msg, t);
	}
}