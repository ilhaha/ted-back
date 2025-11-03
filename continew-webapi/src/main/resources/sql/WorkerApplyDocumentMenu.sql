SET @parentId = 1984071609930334208;
-- 作业人员报名上传的资料管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '作业人员报名上传的资料管理', 1000, 2, '/worker/workerApplyDocument', 'WorkerApplyDocument', 'worker/workerApplyDocument/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 作业人员报名上传的资料管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1984071609930334209, '列表', @parentId, 3, 'worker:workerApplyDocument:list', 1, 1, 1, NOW()),
    (1984071609930334210, '详情', @parentId, 3, 'worker:workerApplyDocument:detail', 2, 1, 1, NOW()),
    (1984071609930334211, '新增', @parentId, 3, 'worker:workerApplyDocument:add', 3, 1, 1, NOW()),
    (1984071609930334212, '修改', @parentId, 3, 'worker:workerApplyDocument:update', 4, 1, 1, NOW()),
    (1984071609930334213, '删除', @parentId, 3, 'worker:workerApplyDocument:delete', 5, 1, 1, NOW()),
    (1984071609930334214, '导出', @parentId, 3, 'worker:workerApplyDocument:export', 6, 1, 1, NOW());

