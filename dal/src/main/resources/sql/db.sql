CREATE TABLE `job_execute_log` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_id` bigint(128) NOT NULL COMMENT 'jobId',
  `executor_count` tinyint(4) NOT NULL DEFAULT '1' COMMENT '重试次数/执行次数',
  `execute_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' COMMENT '被执行期丢到分发线程池时间',
  `expression_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' COMMENT '表达式代表时间',
  `state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行状态:1执行成功，2执行失败',
  `response` varchar(1024) NOT NULL DEFAULT '' COMMENT '执行成功返回结果',
  `scan_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' COMMENT '扫描到时间',
  `send_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' COMMENT '系统真正执行分发时间',
  `addqueue_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' COMMENT '扫描后添加到队列时间',
  `execute_url` varchar(128) NOT NULL DEFAULT '' COMMENT '请求url',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COMMENT='job执行记录表'

CREATE TABLE `job_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_name` varchar(128) NOT NULL DEFAULT '' COMMENT '业务名',
  `time_expression` varchar(20) NOT NULL DEFAULT '' COMMENT '时间表达式',
  `submit_ip` varchar(16) NOT NULL DEFAULT '' COMMENT '执行器ip列表',
  `execute_url` varchar(128) NOT NULL COMMENT '请求url',
  `execute_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' COMMENT '执行时间',
  `is_valid` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效:1有效,0无效,99强制不可用',
  `retry_count` tinyint(4) NOT NULL DEFAULT '1' COMMENT '重试次数',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `idx_execute_date` (`execute_date`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='job信息表'