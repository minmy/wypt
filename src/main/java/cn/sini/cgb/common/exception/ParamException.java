package cn.sini.cgb.common.exception;

/**
 * 参数异常
 * 
 * @author 杨海彬
 */
public class ParamException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ParamException(String msg) {
		super(msg);
	}

	public ParamException(String msg, Throwable t) {
		super(msg, t);
	}
}