SET @parentId = 2011276139600089088;
-- 考生类型管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生类型管理', 1000, 2, '/training/candidateType', 'CandidateType', 'training/candidateType/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生类型管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2011276139600089089, '列表', @parentId, 3, 'training:candidateType:list', 1, 1, 1, NOW()),
    (2011276139600089090, '详情', @parentId, 3, 'training:candidateType:detail', 2, 1, 1, NOW()),
    (2011276139600089091, '新增', @parentId, 3, 'training:candidateType:add', 3, 1, 1, NOW()),
    (2011276139600089092, '修改', @parentId, 3, 'training:candidateType:update', 4, 1, 1, NOW()),
    (2011276139600089093, '删除', @parentId, 3, 'training:candidateType:delete', 5, 1, 1, NOW()),
    (2011276139600089094, '导出', @parentId, 3, 'training:candidateType:export', 6, 1, 1, NOW());

