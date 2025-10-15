SET @parentId = 1901452642318532608;
-- 证件项目关联管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '证件项目关联管理', 1000, 2, '/certificate/certificateProject', 'CertificateProject', 'certificate/certificateProject/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 证件项目关联管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1901452642318532609, '列表', @parentId, 3, 'certificate:certificateProject:list', 1, 1, 1, NOW()),
    (1901452642318532610, '详情', @parentId, 3, 'certificate:certificateProject:detail', 2, 1, 1, NOW()),
    (1901452642318532611, '新增', @parentId, 3, 'certificate:certificateProject:add', 3, 1, 1, NOW()),
    (1901452642318532612, '修改', @parentId, 3, 'certificate:certificateProject:update', 4, 1, 1, NOW()),
    (1901452642318532613, '删除', @parentId, 3, 'certificate:certificateProject:delete', 5, 1, 1, NOW()),
    (1901452642318532614, '导出', @parentId, 3, 'certificate:certificateProject:export', 6, 1, 1, NOW());

