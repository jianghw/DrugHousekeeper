package com.cjy.flb.manager;

import java.util.Collection;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 项目名称：
 * 类描述：
 * 创建人：jianghw
 * 创建时间：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface IDatabase<M, K> {

    boolean insert(M m);

    /**
     * 根据实体删除
     * @param m
     * @return
     */
    boolean delete(M m);

    /**
     * 根据主键删除
     * @param key
     * @return
     */
    boolean deleteByKey(K key);

    /**
     * 根据实体批量删除
     * @param mList
     * @return
     */
    boolean deleteList(List<M> mList);

    /**
     * 批量删除
     * @param key
     * @return
     */
    boolean deleteByKeyInTx(K... key);

    boolean deleteAll();

    boolean insertOrReplace(M m);

    boolean update(M m);

    boolean updateInTx(M... m);

    boolean updateList(List<M> mList);

    M selectByPrimaryKey(K key);

    List<M> loadAll();

    boolean refresh(M m);

    /**
     * 关闭可用连接
     */
    void closeDbConnections();

    /**
     * 清理缓存
     */
    void clearDaoSession();

    /**
     * Delete all tables and content from our database
     */
    boolean dropDatabase();

    /**
     * 事务
     */
    void runInTx(Runnable runnable);

    /**
     * 获取Dao
     *
     * @return
     */
    AbstractDao<M, K> getAbstractDao();

    /**
     * 添加集合
     *
     * @param mList
     */
    boolean insertList(List<M> mList);

    /**
     * 添加集合
     *
     * @param mList
     */
    boolean insertOrReplaceList(List<M> mList);

    /**
     * 自定义查询
     *
     * @return
     */
    QueryBuilder<M> getQueryBuilder();

    /**
     * @param where
     * @param selectionArg
     * @return
     */
    List<M> queryRaw(String where, String... selectionArg);

    /**
     * @param where
     * @param selectionArg
     * @return
     */
    Query<M> queryRawCreate(String where, Object... selectionArg);

    /**
     * @param where
     * @param selectionArg
     * @return
     */
    Query<M> queryRawCreateListArgs(String where, Collection<Object> selectionArg);

}
