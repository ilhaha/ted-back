SET @parentId = 1909073640177364992;
-- 知识类型，存储不同类型的知识占比管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '知识类型，存储不同类型的知识占比管理', 1000, 2, '/examconnect/knowledgeType', 'KnowledgeType', 'examconnect/knowledgeType/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 知识类型，存储不同类型的知识占比管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1909073640177364993, '列表', @parentId, 3, 'examconnect:knowledgeType:list', 1, 1, 1, NOW()),
    (1909073640177364994, '详情', @parentId, 3, 'examconnect:knowledgeType:detail', 2, 1, 1, NOW()),
    (1909073640177364995, '新增', @parentId, 3, 'examconnect:knowledgeType:add', 3, 1, 1, NOW()),
    (1909073640177364996, '修改', @parentId, 3, 'examconnect:knowledgeType:update', 4, 1, 1, NOW()),
    (1909073640177364997, '删除', @parentId, 3, 'examconnect:knowledgeType:delete', 5, 1, 1, NOW()),
    (1909073640177364998, '导出', @parentId, 3, 'examconnect:knowledgeType:export', 6, 1, 1, NOW());

