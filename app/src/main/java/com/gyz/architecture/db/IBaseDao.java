package com.gyz.architecture.db;

import java.util.List;

/**
 * @version V1.0
 * @FileName: com.gyz.architecture.db.IBaseDao.java
 * @author: ZhaoHao
 * @date: 2017-02-08 22:43
 */
public interface IBaseDao<T> {

    /**
     * 增
     * @param entity
     * @return
     */
    Long insert(T entity);

    /**
     * 删
     * @param where
     * @return
     */
    int delete(T where);

    /**
     * 改
     * @param entity
     * @param where
     * @return
     */
    int update(T entity,T where);

    /**
     * 查
     */
    List<T> query(T where);

    List<T> query(T where, String orderBy,Integer startIndex,Integer limit);

    List<T> query(String sql);
}
