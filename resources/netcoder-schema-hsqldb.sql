CREATE CACHED TABLE changes (
  id integer IDENTITY,
  user_id integer NOT NULL,
  problem_id integer NOT NULL,
  type varchar(20) DEFAULT NULL,   -- no enum datatypes in hsqldb
  start_row integer NOT NULL,
  end_row integer NOT NULL,
  start_col integer NOT NULL,
  end_col integer NOT NULL,
  timestamp bigint NOT NULL,
  text longvarchar NOT NULL
);

CREATE CACHED TABLE problems (
  problem_id integer IDENTITY,
  testname varchar(255) NOT NULL,
  description longvarchar NOT NULL
);

INSERT INTO problems VALUES(1, 'sq', 'Square a number.');
