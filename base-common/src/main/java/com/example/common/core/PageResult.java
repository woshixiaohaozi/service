package com.example.common.core;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 统一分页返回结构，承载一页数据及其分页元信息。
 *
 * <p>设计意图：与 {@link BaseQuery} 的分页入参成对出现，
 * 让所有列表接口以相同结构返回总数、页大小、当前页与记录集合，
 * 前端无需针对不同接口分别解析分页字段。
 *
 * <p>使用场景：把 MyBatis-Plus 分页查询的结果转换后，通过 {@link #of(long, long, long, List)} 构造返回。
 *
 * @param <T> 每条记录的数据类型
 */
@Data
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 符合条件的数据总条数，供前端计算总页数 */
    private long total;
    /** 每页展示的条数，对应请求中的 pageSize */
    private long size;
    /** 当前页码，从 1 开始，对应请求中的 pageNum */
    private long current;
    /** 当前页的数据记录集合 */
    private List<T> records;

    /**
     * 根据分页元信息与记录集合构造分页结果。
     *
     * @param total   数据总条数
     * @param size    每页条数
     * @param current 当前页码（从 1 开始）
     * @param records 当前页记录集合
     * @param <T>     记录数据类型
     * @return 填充完毕的分页结果
     */
    public static <T> PageResult<T> of(long total, long size, long current, List<T> records) {
        PageResult<T> p = new PageResult<>();
        p.setTotal(total);
        p.setSize(size);
        p.setCurrent(current);
        p.setRecords(records);
        return p;
    }
}
