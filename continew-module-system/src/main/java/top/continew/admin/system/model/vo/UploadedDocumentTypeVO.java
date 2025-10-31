package top.continew.admin.system.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2025/10/21 20:49
 */
@Data
public class UploadedDocumentTypeVO implements Serializable {

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
