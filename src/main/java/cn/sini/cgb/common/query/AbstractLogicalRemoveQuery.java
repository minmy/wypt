package cn.sini.cgb.common.query;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.util.Environment;

/**
 * 查询抽象类
 * 
 * @author 杨海彬
 */
public abstract class AbstractLogicalRemoveQuery extends AbstractQuery {

	/** 包含删除的数据 */
	private boolean includeRemove = false;

	/** 设置查询结果包括删除的数据 */
	public AbstractLogicalRemoveQuery includeRemove() {
		this.includeRemove = true;
		return this;
	}

	/** 要查询的逻辑删除实体类 */
	protected abstract Class<? extends AbstractLogicalRemoveEntity> queryEntity();

	@Override
	protected void assembleCriteria(Criteria criteria) {
		super.assembleCriteria(criteria);
		if (!includeRemove) {
			criteria.add(Restrictions.eq("remove", false));
		}
	}

	/** sql查询 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> queryMapListBySql(String sql, Object[] params) {
		List<Map<String, Object>> results = null;
		Session session = Environment.getSession();
		try {
			Query sqlQuery = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			for (int i = 0; i < params.length; i++) {
				sqlQuery.setParameter(i, params[i]);
			}
			results = sqlQuery.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
}