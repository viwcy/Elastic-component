package com.viwcy.search.service.page;

import com.viwcy.search.service.helper.ElasticSearchHelper;
import com.viwcy.search.vo.SearchAfterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 * <p>
 * 用于封装构建search_after分页查询，子类继承实现即可
 * S为分页返回实体类型
 */
@Slf4j
public abstract class AbstractSearchAfterService<S> {

    @Resource
    private ElasticSearchHelper searchHelper;

    /**
     * 构建search_after分页返回实体
     */
    public final SearchAfterVO<S> page(List<S> list, int maxSize, int num, Class<S> clazz) {

        SearchAfterVO<S> vo = new SearchAfterVO<>();
        if (CollectionUtils.isEmpty(list)) {
            return vo;
        }
        List<List<S>> partition = searchHelper.splitList(list, maxSize, num);
        if (CollectionUtils.isEmpty(partition)) {
            return vo;
        }
        Field[] allFields = getAllFields(clazz);
        List<Object[]> objects = new ArrayList<>();
        for (int i = 0; i < partition.size(); i++) {
            List<S> part = partition.get(i);
            if (CollectionUtils.isEmpty(part)) {
                continue;
            }
            S s = part.get(part.size() - 1);
            if (Objects.isNull(s)) {
                continue;
            }
            if (i < partition.size() - 1) {
                //下一个
                List<S> next = partition.get(i + 1);
                if (CollectionUtils.isEmpty(next)) {
                    continue;
                }
                objects.add(buildSearchAfter(s, allFields, clazz));
            }
        }
        vo.setRecords(partition.get(0));
        vo.setSearch_after(objects);
        return vo;
    }

    /**
     * 构建search_after标志字段值
     */
    public final Object[] buildSearchAfter(S s, Field[] allFields, Class<S> clazz) {
        String[] sortFields = getSortFields();
        if (sortFields == null || sortFields.length == 0) {
            return new Object[0];
        }
        Object[] objects = new Object[sortFields.length];
        for (int i = 0; i < sortFields.length; i++) {
            objects[i] = getFieldValueByName(s, sortFields[i], allFields, clazz);
        }
        return objects;
    }

    /**
     * 获取属性值
     */
    public final Object getFieldValueByName(S s, String fieldName, Field[] allFields, Class<S> clazz) {
        try {
            for (Field field : allFields) {
                String name = field.getName();
                if (!name.equals(fieldName)) {
                    continue;
                }
                String firstLetter = fieldName.substring(0, 1).toUpperCase();
                String getter = "get" + firstLetter + fieldName.substring(1);
                Method method = clazz.getMethod(getter);
                Object value;
                String fieldTypeName = field.getType().getSimpleName();
                if (fieldTypeName.equals("Date")) {
                    Date date = (Date) method.invoke(s);
                    value = date.getTime();
                } else {
                    value = method.invoke(s);
                }
                return value;
            }
        } catch (Exception e) {
            log.error("get field value has error , e = " + e);
        }
        return null;
    }

    /**
     * 获取本类及其父类的属性
     *
     * @param clazz 当前类对象
     * @return 字段数组
     */
    private final Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

    //排序字段，search_after必要条件，必须含有唯一不重复的字段
    public abstract String[] getSortFields();
}
