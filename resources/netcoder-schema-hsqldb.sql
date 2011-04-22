CREATE CACHED TABLE users (
  id integer IDENTITY,
  username varchar(20),
  password_md5 varchar(32),    -- 16 byte md5 hash value of plaintext password
  salt varchar(16)             -- 8 bytes of salt
);

-- don't allow multiple users with the same user name
CREATE UNIQUE INDEX users_username_index on users (username);

-- test account: username "user", password "abc"
insert into users values (NULL, 'user', 'b252713e97d2b96b51ab0b5422258daa', '5011ffcedffe0a14');

CREATE CACHED TABLE terms (
  id integer IDENTITY,
  name varchar(20) NOT NULL,
  seq integer NOT NULL
);

INSERT INTO terms VALUES (NULL, 'Winter', 1);
INSERT INTO terms VALUES (NULL, 'Spring', 2);
INSERT INTO terms VALUES (NULL, 'Summer', 3);
INSERT INTO terms VALUES (NULL, 'Summer I', 4);
INSERT INTO terms VALUES (NULL, 'Summer II', 5);
INSERT INTO terms VALUES (NULL, 'Fall', 6);

CREATE CACHED TABLE courses (
  id integer IDENTITY,
  name varchar(20) NOT NULL,            -- e.g., "CS 101"
  title longvarchar NOT NULL,           -- e.g., "Introduction to Computer Science I"
  url longvarchar NOT NULL,             -- course web page
  term_id integer NOT NULL,
  year integer NOT NULL,
  
  FOREIGN KEY (term_id) REFERENCES terms(id)
);

INSERT INTO courses values(NULL, 'CS 101', 'Introduction to Computer Science I', 'http://cs.unseen.edu/s11/cs101', 1, 2011);

CREATE CACHED TABLE course_registrations (
  id integer IDENTITY,
  course_id integer NOT NULL,
  user_id integer NOT NULL,
  registration_type integer NOT NULL,    -- enum: student, instructor
  
  FOREIGN KEY (course_id) REFERENCES courses(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO course_registrations VALUES (NULL, 0, 0, 0);

CREATE CACHED TABLE problems (
  problem_id integer IDENTITY,
  course_id integer NOT NULL,
  testname varchar(255) NOT NULL,
  brief_description varchar(60) NOT NULL,
  description longvarchar NOT NULL,
  
  FOREIGN KEY (course_id) REFERENCES courses(id)
);

INSERT INTO problems VALUES(NULL, 0, 'sq', 'Square a number', 'Write a method called "sq" that returns the square of an integer parameter.');

CREATE CACHED TABLE test_cases (
  test_case_id integer IDENTITY,
  problem_id integer NOT NULL,
  test_case_name varchar(40),
  input longvarchar NOT NULL,
  output longvarchar NOT NULL,
  FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
);

INSERT INTO test_cases VALUES(NULL, 0, 'Test1', '5', '25');
INSERT INTO test_cases VALUES(NULL, 0, 'Test2', '9', '81');
INSERT INTO test_cases VALUES(NULL, 0, 'Test3', '10', '100');
INSERT INTO test_cases VALUES(NULL, 0, 'Test4', '-1', '1');

CREATE CACHED TABLE events (
  id bigint IDENTITY,
  user_id integer NOT NULL,
  problem_id integer NOT NULL,
  type integer NOT NULL,
  timestamp bigint NOT NULL,

  FOREIGN KEY (problem_id) REFERENCES problems(problem_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- FIXME: this table should be called "change_events"
CREATE CACHED TABLE changes (
  id integer IDENTITY,
  event_id integer NOT NULL,
  type integer NOT NULL,   -- no enum datatypes in hsqldb
  start_row integer NOT NULL,
  end_row integer NOT NULL,
  start_col integer NOT NULL,
  end_col integer NOT NULL,
  text longvarchar NOT NULL,
  
  FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE CACHED TABLE affect_events (
  id integer IDENTITY,
  event_id integer NOT NULL,
  emotion integer NOT NULL,
  other_emotion varchar(40) DEFAULT NULL,
  emotion_level integer NOT NULL,
  
  FOREIGN KEY (event_id) REFERENCES events(id)
);
