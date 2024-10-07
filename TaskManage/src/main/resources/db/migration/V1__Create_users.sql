CREATE TABLE `users` (
  `user_id` varchar(50) NOT NULL,
  `username` varchar(30) NOT NULL,
  `handle` varchar(4) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `last_active_at` datetime NOT NULL,
  `project_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `project_id_idx` (`project_id`),
  CONSTRAINT `users.project_id` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci