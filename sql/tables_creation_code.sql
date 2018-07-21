--------------------
### match table ###
--------------------
create table matches(
id INT NOT NULL AUTO_INCREMENT,
date DATE NOT NULL,
home_or_away VARCHAR(50) NOT NULL,
opponent VARCHAR(50) NOT NULL,
formation VARCHAR(15) NOT NULL DEFAULT '',
team_instructions VARCHAR(1000) NOT NULL DEFAULT '',
opponent_comments VARCHAR(1000) NOT NULL DEFAULT '',
PRIMARY KEY(id));


--------------------
### player table ###
--------------------
CREATE TABLE players(
id INT NOT NULL AUTO_INCREMENT,
last_name VARCHAR(30) NOT NULL,
middle_name VARCHAR(1) NOT NULL,
first_name VARCHAR(30) NOT NULL,
nationality VARCHAR(50) NOT NULL,
position VARCHAR(5) NOT NULL,
preferred_foot VARCHAR(3) NOT NULL,
birth_date DATE NOT NULL,
age INT NOT NULL AS DATEDIFF('YEAR', birth_date, current_date()),
PRIMARY KEY(id));


-----------------------------
### player related tables ###
-----------------------------
--------------------------
### goals_scored table ###
--------------------------
CREATE TABLE goals_scored(
player_id INT NOT NULL,
match_id INT NOT NULL,
goals INT NOT NULL,
CHECK (goals>0), 
PRIMARY KEY(player_id, match_id),
FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE);

ALTER TABLE goals_scored
ADD FOREIGN KEY (match_id) 
REFERENCES matches(id) ON DELETE CASCADE;


----------------------------
### minutes_played table ###
----------------------------
CREATE TABLE minutes_played( 
player_id INT NOT NULL, 
match_id INT NOT NULL, 
minutes INT NOT NULL,
CHECK (minutes>=1 AND minutes<=120), 
PRIMARY KEY(player_id, match_id), 
FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE);

ALTER TABLE minutes_played
ADD FOREIGN KEY (match_id) 
REFERENCES matches(id) ON DELETE CASCADE;

--------------------
### rating table ###
--------------------
CREATE TABLE ratings(
player_id INT NOT NULL,
match_id INT NOT NULL,
rating double NOT NULL,
CHECK (rating>=0.5 AND rating<=10), 
PRIMARY KEY(player_id, match_id),
FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE);

ALTER TABLE ratings
ADD FOREIGN KEY (match_id) 
REFERENCES matches(id) ON DELETE CASCADE;

--------------------------
### yellow_cards table ###
--------------------------
CREATE TABLE yellow_cards(
player_id INT NOT NULL,
match_id INT NOT NULL,
PRIMARY KEY(player_id, match_id),
FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE);

ALTER TABLE yellow_cards
ADD FOREIGN KEY (match_id) 
REFERENCES matches(id) ON DELETE CASCADE;

-----------------------
### red_cards table ###
-----------------------
CREATE TABLE red_cards(
player_id INT NOT NULL,
match_id INT NOT NULL,
PRIMARY KEY(player_id, match_id),
FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE);

ALTER TABLE red_cards
ADD FOREIGN KEY (match_id) 
REFERENCES matches(id) ON DELETE CASCADE;



----------------------
### training table ###
----------------------
CREATE TABLE trainings(
id INT NOT NULL AUTO_INCREMENT,
date DATE NOT NULL,
plans VARCHAR(1000) NOT NULL DEFAULT '',
duration INT NOT NULL DEFAULT 0,
PRIMARY KEY(id));

-------------------------------
### training related tables ###
-------------------------------
------------------------
### unattended table ###
------------------------
CREATE TABLE unattended(
training_id INT NOT NULL,
player_id INT NOT NULL,
PRIMARY KEY(training_id, player_id),
FOREIGN KEY(training_id) REFERENCES training(id) ON DELETE CASCADE);

ALTER TABLE unattended
ADD FOREIGN KEY (player_id) 
REFERENCES players(id) ON DELETE CASCADE;