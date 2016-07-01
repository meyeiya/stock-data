package com.stock.app.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stock.app.entity.Opt;

@Repository
public class OptDAO extends BasicDao  {

	public void save(Opt opt) {
        try {
            getSession().save(opt);
        } catch (RuntimeException re) {
            throw re;
        }
    }
	
	public List findAll(){
		return getSession().createQuery("from Opt").list();
	}
}
