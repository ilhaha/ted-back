package top.continew.admin.worker.model.resp;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/10/31 11:59
 */
@Data
public class ProjectNeedUploadDocVO {

    /**
     * 资料类型id
     */
    private Long id;

    /**
     * 资料类型名称
     */
    private String typeName;

    /**
     * 资料路径
     */
    private String docPath;

}
