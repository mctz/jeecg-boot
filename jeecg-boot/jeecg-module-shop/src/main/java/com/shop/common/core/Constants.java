package com.shop.common.core;

/**
 * 系统常量
 * 2019-10-29 15:55
 */
public class Constants {

    /* 文件服务器配置 */
    public static final String UPLOAD_DIR = System.getProperty("user.dir") + "/upload/shop/";  // 上传的目录*/

    public static final boolean UPLOAD_UUID_NAME = false;  // 文件上传是否用uuid命名
    public static final boolean UPLOAD_MD5_NAME = true;  // 文件上传是否用MD5命名

    // OpenOffice在不同操作系统上的安装路径
    public static final String OPEN_OFFICE_PATH_WINDOWS = "C:/OpenOffice";
    public static final String OPEN_OFFICE_PATH_LINUX = "/opt/openoffice.org3";
    public static final String OPEN_OFFICE_PATH_MAC = "/Applications/OpenOffice.org.app/Contents/";

    /* 返回结果统一 */
    public static final int RESULT_OK_CODE = 0;  // 默认成功码
    public static final int RESULT_ERROR_CODE = 1;  // 默认失败码

    /**
     * 支付超时时间(分钟)
     */
    public static Integer PAY_TIMEOUT_MINUTES = 5;

    public static void main(String args[]) {
        System.out.println(System.getProperty("user.dir"));
    }
}