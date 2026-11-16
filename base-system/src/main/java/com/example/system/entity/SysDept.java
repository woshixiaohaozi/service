package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统部门（组织机构）实体，对应表 sys_dept。
 *
 * <p>部门构成一棵树，通过 parentId 表达上下级关系，leaderId 指向部门负责人。
 * 部门是“数据权限”的基础：查询数据时可按用户所属部门及其子部门过滤，
 * 实现“只能看本部门/本部门及下级数据”的行级权限控制。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /** 部门名称，如“研发部”。 */
    private String deptName;

    /** 父部门 ID，0 表示顶级部门。 */
    private Long parentId;

    /** 部门负责人用户 ID，对应 sys_user.id。 */
    private Long leaderId;

    /** 排序号，同级部门按此值升序排列。 */
    private Integer sort;

    /** 状态：1 启用，0 停用。 */
    private Integer status;
}
