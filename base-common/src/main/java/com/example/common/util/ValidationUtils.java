package com.example.common.util;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * 常用格式校验工具类，提供手机号与邮箱的合法性判断。
 *
 * <p>设计意图：把这些高频校验规则收敛为静态方法，避免各业务模块重复编写正则；
 * 使用 {@link Pattern} 预编译正则，避免每次调用都重新编译带来的性能开销。
 *
 * <p>使用场景：表单前置校验、导入数据校验等；
 * 所有方法对 null 与空白串均返回 false，调用方无需额外做空值判断。
 */
public final class ValidationUtils {

    /**
     * 中国大陆手机号正则：以 1 开头、第二位 3-9、后接 9 位数字，共 11 位。
     * 预编译为 Pattern 供多次复用。
     */
    private static final Pattern MOBILE = Pattern.compile("^1[3-9][0-9]{9}$");
    /**
     * 邮箱正则：允许字母数字及 . _ + - 作为用户名，域名部分要求至少包含一级子域。
     * 属于实用型校验，不追求覆盖 RFC 全部语法（如引号包裹的本地部分）。
     */
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._+-]+@[A-Za-z0-9-]+([.][A-Za-z0-9-]+)+$");

    // 私有构造：工具类不应被实例化
    private ValidationUtils() {}

    /**
     * 判断是否为合法的中国大陆手机号。
     *
     * @param value 待校验字符串，可为 null 或空白
     * @return 非空白且匹配手机号正则时返回 true，否则返回 false
     */
    public static boolean isMobile(String value) {
        // 短路判断：先用 Hutool 排除 null/空白，避免把 null 传给正则匹配器
        return StrUtil.isNotBlank(value) && MOBILE.matcher(value).matches();
    }

    /**
     * 判断是否为合法的邮箱地址。
     *
     * @param value 待校验字符串，可为 null 或空白
     * @return 非空白且匹配邮箱正则时返回 true，否则返回 false
     */
    public static boolean isEmail(String value) {
        return StrUtil.isNotBlank(value) && EMAIL.matcher(value).matches();
    }
}
