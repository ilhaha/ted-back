SET @parentId = 1991049408574390272;
-- 作业人员准考证管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '作业人员准考证管理', 1000, 2, '/worker/workerExamTicket', 'WorkerExamTicket', 'worker/workerExamTicket/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 作业人员准考证管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1991049408574390273, '列表', @parentId, 3, 'worker:workerExamTicket:list', 1, 1, 1, NOW()),
    (1991049408574390274, '详情', @parentId, 3, 'worker:workerExamTicket:detail', 2, 1, 1, NOW()),
    (1991049408574390275, '新增', @parentId, 3, 'worker:workerExamTicket:add', 3, 1, 1, NOW()),
    (1991049408574390276, '修改', @parentId, 3, 'worker:workerExamTicket:update', 4, 1, 1, NOW()),
    (1991049408574390277, '删除', @parentId, 3, 'worker:workerExamTicket:delete', 5, 1, 1, NOW()),
    (1991049408574390278, '导出', @parentId, 3, 'worker:workerExamTicket:export', 6, 1, 1, NOW());

