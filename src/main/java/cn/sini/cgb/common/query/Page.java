package cn.sini.cgb.common.query;

import java.io.Serializable;
import java.util.List;

import cn.sini.cgb.common.entity.EntityUtils;

/**
 * 通用分页对象
 * 
 * @author 杨海彬
 */
public class Page<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 当前页码 */
	private Integer pageNum;

	/** 每页要显示的记录数 */
	private Integer pageSize;

	/** 总记录数 */
	private Long totalRecord;

	/** 总页数 */
	private Long totalPage;

	/** 查询结果集 */
	private List<T> recordList;

	/**
	 * 构造方法
	 * 
	 * @param pageNum 当前页码
	 * @param pageSize 每页要显示的记录数
	 * @param recordList 查询结果集
	 */
	public Page(int pageNum, int pageSize, List<T> recordList) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.recordList = recordList;
	}

	/**
	 * 构造方法
	 * 
	 * @param pageNum 当前页码
	 * @param pageSize 每页要显示的记录数
	 * @param totalRecord 总记录数
	 * @param recordList 查询结果集
	 */
	public Page(int pageNum, int pageSize, long totalRecord, List<T> recordList) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.totalRecord = totalRecord;
		this.totalPage = ((totalRecord + pageSize - 1) / pageSize);
		this.recordList = recordList;
	}

	/**
	 * 获取当前页码
	 * 
	 * @return pageNum 当前页码
	 */
	public Integer getPageNum() {
		return pageNum;
	}

	/**
	 * 获取每页要显示的记录数
	 * 
	 * @return pageSize 每页要显示的记录数
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * 获取总记录数
	 * 
	 * @return totalRecord 总记录数
	 */
	public Long getTotalRecord() {
		return totalRecord;
	}

	/**
	 * 获取总页数
	 * 
	 * @return totalPage 总页数
	 */
	public Long getTotalPage() {
		return totalPage;
	}

	/**
	 * 获取查询结果集
	 * 
	 * @return recordList 查询结果集
	 */
	public List<T> getRecordList() {
		return recordList;
	}

	@Override
	public String toString() {
		return EntityUtils.toString(this);
	}
}