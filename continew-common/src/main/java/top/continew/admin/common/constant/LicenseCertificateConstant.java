package top.continew.admin.common.constant;

/**
 * @author ilhaha
 * @Create 2025/12/25 16:00
 */
public class LicenseCertificateConstant {

    /**
     * 数据来源
     */
    public static final String DATASOURCE = "北京市特种设备检测中心考试中心";

    /**
     * 信息录入单位
     */
    public static final String INFO_INPUTORG = "北京市特种设备检测中心考试中心";

    /**
     * 原授权单位
     */
    public static final String ORIGINAL_AUTH_COM = "北京市市场监督管理局";

    /**
     * 授权单位
     */
    public static final String AUTH_COM = "北京市市场监督管理局";

    /**
     * 工作单位
     */
    public static final String WORK_UNIT = "未知";

    /**
     * 证书编号前缀
     */
    public static final String LCNS_NO_PREFIX = "TS";

    /**
     * 证书有效期年限
     */
    public static final Integer VALIDITY_PERIOD_YEARS = 4;

    /**
     * Window 路径
     */
    public static final String PATH_SEPARATOR = "/";

    /**
     * 存放一寸照的文件夹
     */
    public static final String FACE_PHOTO_FOLDER = "pics/";

    /**
     * 一寸照后缀
     */
    public static final String FACE_PHOTO_SUFFIX = ".jpg";

    /**
     * xml文件名
     */
    public static final String XML_NAME = "t_special_equipment_person.xml";

    /**
     * xml头
     */
    public static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n";

    /**
     * xml跟标签开始标签
     */
    public static final String ROOT_LABEL_START = "<Persons>\n";

    /**
     * xml内容
     */
    public static final String XML_CONTENT = """
            <datasource DATASOURCE="%s" INFOINPUTORG="%s"/>
            <PersonInfo PSN_NAME="%s" IDCARD_NO="%s" ORIGINAL_COM_NAME="%s" COM_NAME="%s" APPLY_TYPE="%s" APPLY_DATE="%s" IS_VERIFY="%s" IS_OPR="%s">
            <PsnLcnsGeneral LCNS_KIND="%s" LCNS_CATEGORY="%s" LCNS_NO="%s" CERT_DATE="%s" AUTH_DATE="%s" END_DATE="%s" ORIGINAL_AUTH_COM="%s" AUTH_COM="%s" REMARK="%s" STATE="%s">
            <PsnLcnsDetail PSNLCNS_ITEM="%s" PSNLCNS_ITEM_CODE="%s" PERMIT_SCOPE="%s" REMARK="%s" STATE="%s"/>
            </PsnLcnsGeneral>
            </PersonInfo>
            """;

    /**
     * xml跟标签结束标签
     */
    public static final String ROOT_LABEL_END = "</Persons>";

    /**
     * xml编码
     */
    public static final String XML_CODING = "GBK";

    /**
     * 返回的文件后缀
     */
    public static final String RETURN_FILE_SUFFIX = ".zip";

    /**
     * 以附件的形式返回
     */
    public static final String ATTACHMENT = "attachment";


}
