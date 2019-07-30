package cn.sini.cgb.common.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;

import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.util.CommonUtils;
import cn.sini.cgb.common.util.Environment;

/**
 * 查询抽象类
 * 
 * @author 杨海彬
 */
@SuppressWarnings("unchecked")
public abstract class AbstractQuery {

	private String comment;
	private Session session;
	private boolean readOnly;
	private LockMode lockMode;
	private ResultTransformer resultTransformer;
	private ProjectionList projectionList = Projections.projectionList();
	private List<Criterion> criterionList = new ArrayList<Criterion>();
	private List<Order> orderList = new ArrayList<Order>();
	private List<Map<String, Object>> aliasList = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> subQueryList = new ArrayList<Map<String, Object>>();

	/** 添加ID查询条件 */
	public AbstractQuery id(Serializable... id) {
		Object[] values = CommonUtils.removeEmptyElement(id);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq(HibernateUtils.getIdFieldName(queryEntity()), values[0]));
			} else {
				addCriterion(Restrictions.in(HibernateUtils.getIdFieldName(queryEntity()), values));
			}
		}
		return this;
	}

	/** 添加ID不等于查询条件 */
	public AbstractQuery idNotIn(Serializable... id) {
		Object[] values = CommonUtils.removeEmptyElement(id);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.ne(HibernateUtils.getIdFieldName(queryEntity()), values[0]));
			} else {
				addCriterion(Restrictions.not(Restrictions.in(HibernateUtils.getIdFieldName(queryEntity()), values)));
			}
		}
		return this;
	}

	/** 添加统计规则 */
	public AbstractQuery rowCount() {
		addProjection(Projections.rowCount());
		return this;
	}

	/** 添加分组规则 */
	public AbstractQuery groupBy(String propertyName) {
		if (StringUtils.isNotEmpty(propertyName)) {
			addProjection(Projections.groupProperty(propertyName));
		}
		return this;
	}

	/** 添加分组规则，并指定分组别名 */
	public AbstractQuery groupBy(String propertyName, String alias) {
		if (StringUtils.isNotEmpty(propertyName)) {
			addProjection(Projections.groupProperty(propertyName), alias);
		}
		return this;
	}

	/** 添加排序规则 */
	public AbstractQuery orderBy(String propertyName, boolean asc) {
		if (StringUtils.isNotEmpty(propertyName)) {
			this.orderList.add(asc ? Order.asc(propertyName) : Order.desc(propertyName));
		}
		return this;
	}

	/** 设置查询注释 */
	public AbstractQuery comment(String comment) {
		this.comment = comment;
		return this;
	}

	/** 设置Hibernate Session对象 */
	public AbstractQuery session(Session session) {
		this.session = session;
		return this;
	}

	/** 启用查询结果只读 */
	public AbstractQuery readOnly() {
		this.readOnly = true;
		return this;
	}

	/** 设置数据库锁模式 */
	public AbstractQuery lockMode(LockMode lockMode) {
		this.lockMode = lockMode;
		return this;
	}

	/** 设置查询结果转换器 */
	public AbstractQuery resultTransformer(ResultTransformer resultTransformer) {
		this.resultTransformer = resultTransformer;
		return this;
	}

	/** 要查询的实体类 */
	protected abstract Class<? extends AbstractEntity> queryEntity();

	/** 添加投影规则 */
	public void addProjection(Projection projection) {
		this.projectionList.add(projection);
	}

	/** 添加投影规则，并指定投影字段别名 */
	public void addProjection(Projection projection, String alias) {
		this.projectionList.add(projection, alias);
	}

	/** 添加字段别名，默认使用inner join连接方式 */
	protected void addFieldAlias(String propertyName, String alias) {
		addFieldAlias(propertyName, alias, JoinType.INNER_JOIN);
	}

	/** 添加字段别名 */
	protected void addFieldAlias(String propertyName, String alias, JoinType joinType) {
		Map<String, Object> aliasMap = new HashMap<String, Object>();
		aliasMap.put("propertyName", propertyName);
		aliasMap.put("alias", alias);
		aliasMap.put("joinType", joinType);
		this.aliasList.add(aliasMap);
	}

	/** 添加查询规则 */
	protected void addCriterion(Criterion criterion) {
		this.criterionList.add(criterion);
	}

	/** 添加多表连接查询规则，默认使用inner join连接方式 */
	protected void addCriteria(String propertyName, AbstractQuery query) {
		addCriteria(propertyName, query, query.queryEntity().getSimpleName(), JoinType.INNER_JOIN);
	}

	/** 添加多表连接查询规则，并指定连接别名，默认使用inner join连接方式 */
	protected void addCriteria(String propertyName, AbstractQuery query, String alias) {
		addCriteria(propertyName, query, alias, JoinType.INNER_JOIN);
	}

	/** 添加多表连接查询规则 */
	protected void addCriteria(String propertyName, AbstractQuery query, String alias, JoinType joinType) {
		Map<String, Object> subQueryMap = new HashMap<String, Object>();
		subQueryMap.put("propertyName", propertyName);
		subQueryMap.put("query", query);
		subQueryMap.put("alias", alias);
		subQueryMap.put("joinType", joinType);
		this.subQueryList.add(subQueryMap);
	}

	/** 查询总记录数，本方法会忽略所有投影规则，如group by、sum、avg等等。如不想忽略请调用rowCount方法 */
	public Long count() {
		ProjectionList tempProjectionList = this.projectionList;
		this.projectionList = Projections.projectionList().add(Projections.rowCount());
		Long count = uniqueResult();
		this.projectionList = tempProjectionList;
		return count;
	}

	/** 查询唯一结果 */
	public <T> T uniqueResult() {
		return (T) createCriteria().uniqueResult();
	}

	/** 查询第一条结果 */
	public <T> T firstResult() {
		return (T) createCriteria().setFirstResult(0).setMaxResults(1).uniqueResult();
	}

	/** 查询集合结果 */
	public <T> List<T> list() {
		return createCriteria().list();
	}

	/** 查询集合结果 */
	public <T> List<T> list(int firstResult) {
		return createCriteria().setFirstResult(firstResult).list();
	}

	/** 查询集合结果 */
	public <T> List<T> list(int firstResult, int maxResults) {
		return createCriteria().setFirstResult(firstResult).setMaxResults(maxResults).list();
	}

	/** 查询不带总数的分页结果 */
	public <T> Page<T> pageNotCount(int pageNum, int pageSize) {
		List<T> recordList = createCriteria().setFirstResult((pageNum - 1) * pageSize).setMaxResults(pageSize).list();
		return new Page<T>(pageNum, pageSize, recordList);
	}

	/** 查询带总数的分页结果 */
	public <T> Page<T> pageHasCount(int pageNum, int pageSize) {
		List<T> recordList = createCriteria().setFirstResult((pageNum - 1) * pageSize).setMaxResults(pageSize).list();
		return new Page<T>(pageNum, pageSize, count(), recordList);
	}

	/** 创建Criteria对象 */
	private Criteria createCriteria() {
		if (this.session == null) {
			this.session = Environment.getSession();
		}
		Criteria criteria = this.session.createCriteria(queryEntity(), queryEntity().getSimpleName());
		criteria.setComment(this.comment);
		criteria.setReadOnly(this.readOnly);
		if (this.lockMode != null) {
			criteria.setLockMode(this.lockMode);
		}
		if (this.resultTransformer != null) {
			criteria.setResultTransformer(this.resultTransformer);
		}
		assembleCriteria(criteria);
		return criteria;
	}

	/** 组装Criteria对象 */
	protected void assembleCriteria(Criteria criteria) {
		if (this.projectionList.getLength() > 0) {
			criteria.setProjection(this.projectionList);
		}
		for (Criterion criterion : this.criterionList) {
			criteria.add(criterion);
		}
		for (Order order : this.orderList) {
			criteria.addOrder(order);
		}
		for (Map<String, Object> map : this.aliasList) {
			String propertyName = (String) map.get("propertyName");
			String alias = (String) map.get("alias");
			JoinType joinType = (JoinType) map.get("joinType");
			criteria.createAlias(propertyName, alias, joinType);
		}
		for (Map<String, Object> map : this.subQueryList) {
			String propertyName = (String) map.get("propertyName");
			AbstractQuery query = (AbstractQuery) map.get("query");
			String alias = (String) map.get("alias");
			JoinType joinType = (JoinType) map.get("joinType");
			Criteria subCriteria = criteria.createCriteria(propertyName, alias, joinType);
			query.assembleCriteria(subCriteria);
		}
	}
}