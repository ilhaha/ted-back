package top.continew.admin.exam.model.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2026/5/7 16:24
 */
@Data
public class DocumentFileReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资料类型ID
     */
    private Long typeId;

    /**
     * 文件地址列表
     */
    private List<String> urls;

}
