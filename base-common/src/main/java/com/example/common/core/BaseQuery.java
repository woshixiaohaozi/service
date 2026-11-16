package com.example.common.core;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一分页查询入参基类，定义所有分页查询共用的页码与每页条数。
 *
 * <p>设计意图：通过 Bean Validation 注解在入口处拦截非法分页参数，
 * 避免脏参数穿透到数据库（例如 pageSize 过大拖垮数据库）；
 * 各业务查询对象继承本类即可复用默认值与校验规则。
 *
 * <p>使用场景：需要分页的列表查询 DTO 继承本类；
 * 参数不合法时由 {@code GlobalExceptionHandler} 统一转换为参数校验失败响应。
 */
@Data
public class BaseQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码，从 1 开始，默认第 1 页。
     * 使用 {@link Min} 约束下界，防止传入 0 或负数导致分页计算异常。
     */
    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum = 1;

    /**
     * 每页条数，默认 10 条。
     * 下界防止 0 或负值；上界 500 与 {@code CommonConstant.MAX_PAGE_SIZE} 一致，
     * 避免一次性拉取过多数据造成数据库与内存压力。
     */
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 500, message = "每页条数不能超过 500")
    private Integer pageSize = 10;
}
