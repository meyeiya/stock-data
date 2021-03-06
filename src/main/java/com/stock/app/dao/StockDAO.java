package com.stock.app.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.stock.app.entity.Stock;

@Repository
public class StockDAO extends BasicDao  {
    private static final Log log = LogFactory.getLog(StockDAO.class);
    
    public void save(Stock transientInstance) {
        log.debug("saving Stock instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
    public void save(List<Stock> list) {
        log.debug("saving Stock instance");
        try {
        	for (Stock stock : list) {
        		getSession().save(stock);
			}
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(Stock persistentInstance) {
        log.debug("deleting Stock instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
	public Stock merge(Stock detachedInstance) {
        log.debug("merging Stock instance");
        try {
        	Stock result = (Stock) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }
    
	public List findAll() {
		log.debug("finding all Stock instances");
		try {
			String queryString = "from Stock stock order by stock.stockId";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public List findByQuery(String sql){
		Query queryObject = getSession().createQuery(sql);
		return queryObject.list();
	}
	
	public long countByQuery(String sql){
		return (Long)getSession().createQuery(sql).uniqueResult();
	}
	
}