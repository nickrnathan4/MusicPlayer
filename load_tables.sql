CREATE TABLE indexedsong
(
  songid serial NOT NULL,
  songtitle character varying(200),
  songartist character varying(200),
  songpath character varying(200),
  CONSTRAINT indexedsong_songid_key PRIMARY KEY (songid)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE keypoint
(
  id serial NOT NULL,
  value character varying(50),
  songid integer,
  "time" integer,
  CONSTRAINT keypoint_id_key PRIMARY KEY (id),
  CONSTRAINT keypoint_songid_fkey FOREIGN KEY (songid)
      REFERENCES indexedsong (songid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);