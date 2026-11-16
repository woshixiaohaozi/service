package com.example.system.dto;

import com.example.common.core.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQuery extends BaseQuery {
    private String username;
    private Integer status;
}
