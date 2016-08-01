package com.stock.app.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.stock.app.entity.StockDaily;

@Repository
public class StockDailyDAO extends BasicDao{
	private static final Log log = LogFactory.getLog(StockDailyDAO.class);
    
    public void save(StockDaily transientInstance) {
        log.debug("saving StockDaily instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
    public void save(List<StockDaily> list) {
        log.debug("saving StockDaily instance");
        try {
        	for (StockDaily stock : list) {
        		getSession().save(stock);
			}
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(StockDaily persistentInstance) {
        log.debug("deleting StockDaily instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    
	public List findAll() {
		log.debug("finding all StockDaily instances");
		try {
			String queryString = "from StockDaily";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<StockDaily> findByStockId(String stockId) {
		log.debug("finding findByStockId instances");
		try {
			String queryString = "from StockDaily daily where daily.stockId=:stockId and daily.openValue<>0 order by daily.tradeDate";
	        Query queryObject = getSession().createQuery(queryString);
	        queryObject.setParameter("stockId", stockId);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public List<StockDaily> findByQuery(String sql,Map<String,Object> args) {
		Query query=getSession().createQuery(sql);
		if(args!=null&&args.size()>0){
			for (String key : args.keySet()) {
				query.setParameter(key, args.get(key));
			}
		}
		return query.list();
	}
}
