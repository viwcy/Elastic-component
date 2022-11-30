package com.viwcy.search.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * TODO  Copyright (c) yun lu 2021 Fau (viwcy4611@gmail.com), ltd
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends ElasticsearchRepository<T, ID> {

    Logger logger = LoggerFactory.getLogger(BaseRepository.class);

    /**
     * 主键ID查找
     */
    default T queryById(ID id) {

        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ID has not be null");
        }
        T t = null;
        try {
            Optional<T> optional = this.findById(id);
            if (optional.isPresent()) {
                t = optional.get();
            }
        } catch (Exception e) {
            logger.error("BaseRepository#queryById has error , e = " + e);
        }
        return t;
    }

    /**
     * 主键ID集合查询
     */
    default List<T> queryByIds(Collection<ID> ids) {

        if (CollectionUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("Collection ID has not be empty");
        }
        List<T> list = new ArrayList<>();
        try {
            Iterable<T> iterable = this.findAllById(ids);
            if (Objects.isNull(iterable)) {
                return list;
            }
            Iterator<T> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        } catch (IllegalArgumentException e) {
            logger.error("BaseRepository#queryByIds has error , e = " + e);
        }
        return list;
    }

    /**
     * 保存
     */
    default boolean _save(T t) {

        if (Objects.isNull(t)) {
            throw new IllegalArgumentException("T has not be null");
        }
        boolean flag = false;
        try {
            T save = this.save(t);
            if (!Objects.isNull(save)) {
                flag = true;
                logger.debug("BaseRepository#saveOrUpdate success , t = " + save);
            }
        } catch (Exception e) {
            logger.error("BaseRepository#saveOrUpdate has error , e = " + e);
        }
        return flag;
    }

    /**
     * 批量保存
     */
    default boolean _saveAll(List<T> list) {

        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("collection has not be empty");
        }
        boolean flag = false;
        try {
            Iterable<T> iterable = this.saveAll(list);
            ArrayList<T> ts = new ArrayList<>();
            if (!Objects.isNull(iterable)) {
                Iterator<T> iterator = iterable.iterator();
                if (!Objects.isNull(iterator)) {
                    while (iterator.hasNext()) {
                        ts.add(iterator.next());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(ts) && ts.size() == list.size()) {
                flag = true;
                logger.debug("BaseRepository#saveOrUpdateBatch success , list = " + list);
            }
        } catch (Exception e) {
            logger.error("BaseRepository#saveOrUpdateBatch has error , e = " + e);
        }
        return flag;
    }

    /**
     * 批量删除，传参ID集合
     */
    default boolean deleteBatch(Collection<ID> ids) {

        if (CollectionUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("collection has not be empty");
        }

        boolean delete = false;
        try {
            for (ID id : ids) {
                this.deleteById(id);
            }
            delete = true;
            logger.debug("BaseRepository#deleteBatch success , ids = " + ids);
        } catch (Exception e) {
            logger.error("BaseRepository#deleteBatch has error , e = " + e);
        }
        return delete;
    }
}
