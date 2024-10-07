CREATE TABLE `user_projects` (
  `user_id` varchar(50) NOT NULL,
  `project_id` varchar(50) NOT NULL,
  `handle` varchar(4) NOT NULL,
  `unapproved_count` int NOT NULL DEFAULT '0',
  `requests_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`project_id`),
  KEY `idx_project_id` (`project_id`,`user_id`),
  CONSTRAINT `project_id` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci