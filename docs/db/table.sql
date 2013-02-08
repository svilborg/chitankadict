CREATE TABLE "word" (
  "name" varchar(100) DEFAULT NULL,
  "title" longtext,
  "meaning" longtext,
  "synonyms" longtext,
  "misspells" text,
  PRIMARY KEY ("name")
);
