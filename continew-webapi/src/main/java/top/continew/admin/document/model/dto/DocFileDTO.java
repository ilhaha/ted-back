package top.continew.admin.document.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/22 19:43
 */
@Data
public class DocFileDTO {

    /**
     * 资料类型ID
     */
    private Long typeId;

    /**
     * 对应的文件URL集合
     */
    private List<String> urls;
}
