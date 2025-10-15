SET @parentId = 1899724350636855296;
-- 考生资料关系管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生资料关系管理', 1000, 2, '/document/examineeDocument', 'ExamineeDocument', 'document/examineeDocument/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生资料关系管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1899724350636855297, '列表', @parentId, 3, 'document:examineeDocument:list', 1, 1, 1, NOW()),
    (1899724350636855298, '详情', @parentId, 3, 'document:examineeDocument:detail', 2, 1, 1, NOW()),
    (1899724350636855299, '新增', @parentId, 3, 'document:examineeDocument:add', 3, 1, 1, NOW()),
    (1899724350636855300, '修改', @parentId, 3, 'document:examineeDocument:update', 4, 1, 1, NOW()),
    (1899724350636855301, '删除', @parentId, 3, 'document:examineeDocument:delete', 5, 1, 1, NOW()),
    (1899724350636855302, '导出', @parentId, 3, 'document:examineeDocument:export', 6, 1, 1, NOW());

