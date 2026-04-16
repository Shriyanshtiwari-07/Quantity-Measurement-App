CREATE DATABASE IF NOT EXISTS user_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS measurement_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON user_db.*        TO 'parents'@'%';
GRANT ALL PRIVILEGES ON measurement_db.* TO 'parents'@'%';
FLUSH PRIVILEGES;