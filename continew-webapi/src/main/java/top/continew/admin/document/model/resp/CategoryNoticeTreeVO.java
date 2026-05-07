package top.continew.admin.document.model.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class CategoryNoticeTreeVO implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private Long value;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    private List<CategoryNoticeTreeVO> children;
}