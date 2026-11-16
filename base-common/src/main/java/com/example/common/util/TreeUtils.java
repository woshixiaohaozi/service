package com.example.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用树形结构构建工具，把扁平的"ID + 父ID"节点列表组装成森林（根节点列表）。
 *
 * <p>设计意图：菜单、部门、分类等场景的原始数据通常是平铺列表，
 * 本工具用一次哈希映射完成父子关系的装配，时间复杂度 O(n)，
 * 避免逐节点递归遍历列表造成的 O(n²) 开销；节点类型由调用方通过
 * {@link TreeNode} 接口定义，工具类本身与具体业务实体解耦。
 *
 * <p>使用注意：本方法只负责把"无父节点"的根节点筛选出来，并给非根节点准备 children 容器，
 * 调用方的实体需在 setChildren 实现中维护自己的子节点列表；
 * 节点 ID 重复时保留先出现的那个（由合并函数 (a,b)->a 决定）。
 */
public final class TreeUtils {

    // 私有构造：工具类不应被实例化
    private TreeUtils() {}

    /**
     * 树节点契约：业务实体实现本接口后即可交给 {@link #build(List)} 构建树。
     * 接口只要求读取 id / parentId 并写入 children，不限制实体其他字段。
     */
    public interface TreeNode {
        /**
         * @return 节点唯一标识
         */
        Long getId();
        /**
         * @return 父节点 ID；为 null 表示该节点是根节点
         */
        Long getParentId();
        /**
         * 设置子节点集合，由实现类决定如何存储（通常是给 children 字段赋值）。
         *
         * @param children 子节点列表
         */
        void setChildren(List<?> children);
    }

    /**
     * 把扁平节点列表组装成森林，返回所有根节点（parentId 为空或指向不存在节点的节点）。
     *
     * @param nodes   扁平的节点列表，可为 null；元素需实现 {@link TreeNode}
     * @param <T>     节点类型，必须实现 TreeNode 接口
     * @return 根节点列表；入参为 null 或空时返回空列表，不会返回 null
     */
    public static <T extends TreeNode> List<T> build(List<T> nodes) {
        List<T> roots = new ArrayList<>();
        // 防御性处理：null 或空列表直接返回空集合，避免后续 stream 抛 NPE
        if (nodes == null || nodes.isEmpty()) {
            return roots;
        }
        // 先建立 id -> 节点 的哈希映射，查找父节点时可 O(1) 定位，避免嵌套遍历
        Map<Long, T> idMap = nodes.stream()
                .collect(Collectors.toMap(TreeNode::getId, Function.identity(), (a, b) -> a));
        for (T node : nodes) {
            // parentId 为空，或在映射中找不到父节点，均视为根节点；
            // parent == node 用来兜住"自己指向自己"的脏数据，防止形成无效孤儿节点
            T parent = node.getParentId() == null ? null : idMap.get(node.getParentId());
            if (parent == null || parent == node) {
                roots.add(node);
            } else {
                // 非根节点初始化 children 容器，便于调用方随后把子节点挂到父节点上
                node.setChildren(new ArrayList<>());
            }
        }
        return roots;
    }
}
