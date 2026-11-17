package com.example.common.core;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类，集中声明业务表通用的四类字段：主键、创建审计、更新审计、逻辑删除。
 *
 * <p>设计意图：让所有业务实体继承本类即可获得一致的 ID 生成策略与审计能力，
 * 避免在每个实体中重复定义相同字段；配合 MyBatis-Plus 的自动填充与逻辑删除机制，
 * 无需在业务代码里手写 createTime / updateTime / deleted 的赋值与过滤。
 *
 * <p>使用场景：所有需要主键与审计字段的数据库实体均应继承本类。
 *
 * <p>依赖说明：本类引用了 MyBatis-Plus 注解，base-common 通过 framework 模块传递引入
 * mybatis-plus-annotation；若单独使用 common 模块，请自行补充该依赖。
 */
@Data
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。采用 {@link IdType#ASSIGN_ID} 由 MyBatis-Plus 在插入前生成分布式 ID，
     * 不依赖数据库自增，便于分库分表与数据迁移。
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建人，插入时由自动填充处理器写入，业务代码无需手动赋值。
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间，插入时自动填充；序列化为 "yyyy-MM-dd HH:mm:ss" 便于前端直接展示。
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新人，插入与更新时均会自动填充，记录最后一次变更的操作者。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间，插入与更新时均会自动填充；格式化规则与 createTime 一致。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记。{@link TableLogic} 使 MyBatis-Plus 自动在 SQL 中追加删除条件，
     * 数据只做标记而不物理删除，便于事后审计与数据恢复。
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
